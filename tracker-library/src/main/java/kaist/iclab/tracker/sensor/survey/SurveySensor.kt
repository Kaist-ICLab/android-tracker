package kaist.iclab.tracker.sensor.survey

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kaist.iclab.tracker.TrackerUtil.formatLapsedTime
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.listener.SingleAlarmListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.survey.activity.DefaultSurveyActivity
import kaist.iclab.tracker.sensor.survey.activity.SurveyActivity
import kaist.iclab.tracker.storage.core.StateStorage
import kaist.iclab.tracker.storage.core.SurveyScheduleStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class SurveySensor(
    private val context: Context,
    permissionManager: PermissionManager,
    private val configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
    private val scheduleStorage: SurveyScheduleStorage,
    val survey: Map<String, Survey>,
    @param:DrawableRes private val icon: Int
): BaseSensor<SurveySensor.Config, SurveySensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    companion object {
        private val TAG = SurveySensor::class.simpleName
        private val SCHEDULE_INTERVAL = TimeUnit.MINUTES.toMillis(15)
        const val NOTIFICATION_CHANNEL_ID = "android_tracker_survey"
        const val NOTIFICATION_CHANNEL_NAME = "Survey"
        const val NOTIFICATION_ID = 1236478193
    }

    override val permissions: Array<String> = listOfNotNull(
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.SCHEDULE_EXACT_ALARM else null,
    ).toTypedArray()

    override val foregroundServiceTypes = arrayOf<Int>()

    private val surveyActionName = "kaist.iclab.tracker.${name}_REQUEST"
    private val surveyActionCode = 0x11
    private val scheduleActionName = "kaist.iclab.tracker.survey_schedule_REQUEST"
    private val scheduleActionCode = 0x1234

    private val surveyAlarmListener = SingleAlarmListener(
        context = context,
        actionName = surveyActionName,
        actionCode = surveyActionCode
    )

    private val scheduleCheckListener = AlarmListener(
        context = context,
        actionName = scheduleActionName,
        actionCode = scheduleActionCode,
        actionIntervalInMilliseconds = SCHEDULE_INTERVAL
    )

    data class Config (
        val startTimeOfDay: Long,
        val endTimeOfDay: Long,
        val scheduleMethod: Map<String, SurveyScheduleMethod>
    ): SensorConfig


    @Serializable
    data class Entity (
//        val intendedTriggerTime: Long,
//        val actualTriggerTime: Long,
//        val reactionTime: Long,
        val responseTime: Long,

//        val received: Long,
//        val title: String,
//        val message: String,
//        val timeoutUntil: Long,
//        val timeoutAction: Long,
        val response: JsonElement,
    ): SensorEntity()

    fun openSurvey(id: String) {
        val intent = Intent(context, DefaultSurveyActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        SurveyActivity.survey = survey.getValue(id)
        SurveyActivity.survey.initSurveyResponse()
        SurveyActivity.surveyCallback = { response ->
            listeners.forEach {
                it.invoke(Entity(
                    responseTime = System.currentTimeMillis(),
                    response = response
                ))
            }
        }

        context.startActivity(intent)
    }

    override fun init() {
        super.init()
        // We are using separate channel for survey notification
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            "Notification to inform survey time"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getESMSchedule(startTime: Long, endTime: Long, config: SurveyScheduleMethod.ESM): List<Long> {

        val lengthOfDay = endTime - startTime

        val intervals = mutableListOf<Long>(0)
        repeat(config.numSurvey - 1) {
            val intervalLimit = lengthOfDay / (config.numSurvey - 1)
            val actualMaxInterval = config.maxInterval.coerceAtMost(intervalLimit)
            val actualMinInterval = config.minInterval.coerceAtMost(actualMaxInterval)

            // Try to spread out the schedule more (skewed to the maximum value)
            val skewedRandom = 1 - Math.random().pow(2.0)
            val interval = ((actualMaxInterval - actualMinInterval) * skewedRandom + actualMinInterval).toLong()
            intervals.add(interval)
        }

        Log.v(TAG, "Intervals: ${intervals.map{ it.formatLapsedTime() }}")

        val intervalSum = intervals.sum()
        val startMargin = (Math.random() * (lengthOfDay - intervalSum)).toLong()

        var accumulatedTime = startMargin + startTime
        val accumulatedTimeList = mutableListOf<Long>()

        intervals.forEach {
            accumulatedTime += it
            accumulatedTimeList.add(accumulatedTime)
        }

        return accumulatedTimeList
    }

    private fun scheduleTodaySurvey(): SurveySchedule? {
        val config = configStorage.get()

        val zoneId = ZoneId.systemDefault()
        val baseDate = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()

        val startTime = baseDate + config.startTimeOfDay
        val endTime = baseDate + config.endTimeOfDay

        config.scheduleMethod.forEach { id, scheduleMethod ->
            val schedule = when(scheduleMethod) {
                is SurveyScheduleMethod.ESM -> getESMSchedule(startTime, endTime, scheduleMethod)
                is SurveyScheduleMethod.Fixed -> scheduleMethod.timeOfDay.map { it + baseDate }
            }

            schedule.forEach {
                scheduleStorage.addSchedule(SurveySchedule(
                    surveyId = id,
                    triggerTime = it
                ))
            }
        }

        return scheduleStorage.getNextSchedule()
    }

    private fun setupNextSurveySchedule() {
        val currentTime = System.currentTimeMillis()
        val nextSchedule = scheduleStorage.getNextSchedule() ?: (if(!scheduleStorage.isTodayScheduleExist()) scheduleTodaySurvey() else null)

        if(nextSchedule == null) {
            return
        }

        val timeUntilNextSurvey = nextSchedule.triggerTime - currentTime
        if(timeUntilNextSurvey <= SCHEDULE_INTERVAL * 2) {
            Log.d(TAG, "Survey scheduled after $timeUntilNextSurvey ms! Using exact alarm for next wakeup")

            // Pass uuid data to the alarm
            val bundle = Bundle()
            bundle.putString("uuid", nextSchedule.uuid)

            surveyAlarmListener.scheduleNextAlarm(timeUntilNextSurvey, isExact = true, bundle=bundle)
        }
    }

    private val scheduleCheckCallback = { intent: Intent? -> setupNextSurveySchedule() }

    private val surveyCallback = surveyCallback@{ intent: Intent? ->
        if(intent == null) return@surveyCallback

        Log.d(TAG, "Action code: ${intent.action}")

        val uuid = intent.getStringExtra("uuid")!!
        Log.d(TAG, "Survey triggered: $uuid")

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Do Survey!")
            .setContentText("Survey triggered! uuid: $uuid")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(icon)
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        setupNextSurveySchedule()
    }

    override fun onStart() {
        scheduleCheckListener.addListener(scheduleCheckCallback)
        surveyAlarmListener.addListener(surveyCallback)

        scheduleCheckCallback(null)
    }

    override fun onStop() {
        scheduleCheckListener.removeListener(scheduleCheckCallback)
        surveyAlarmListener.removeListener(surveyCallback)
    }
}