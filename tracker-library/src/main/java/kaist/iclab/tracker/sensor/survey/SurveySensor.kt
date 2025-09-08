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
import kaist.iclab.tracker.storage.core.StateStorage
import kaist.iclab.tracker.storage.core.SurveyScheduleStorage
import kotlinx.serialization.Serializable
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
        val configs: List<SurveyConfig>
    ): SensorConfig


    @Serializable
    data class Entity (
        val intendedTriggerTime: Long,
        val actualTriggerTime: Long,
        val reactionTime: Long,
        val responseTime: Long,

        val received: Long,
        val title: String,
        val message: String,
        val instruction: String,
        val timeoutUntil: Long,
        val timeoutAction: Long,
    ): SensorEntity()

    fun openSurvey() {
        // TODO: Launch activity
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

    private fun scheduleTodaySurvey(): SurveySchedule? {
        val config = configStorage.get()

        val zoneId = ZoneId.systemDefault()
        val baseDate = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()

        val startTime = baseDate + config.startTimeOfDay
        val endTime = baseDate + config.endTimeOfDay
        val lengthOfDay = endTime - startTime

        for(surveyConfig in config.configs) {
            val intervalTime = mutableListOf<Long>()
            repeat(surveyConfig.numSurvey - 1) {
                val intervalLimit = lengthOfDay / (surveyConfig.numSurvey - 1)
                val actualMaxInterval = surveyConfig.maxInterval.coerceAtMost(intervalLimit)
                val actualMinInterval = surveyConfig.minInterval.coerceAtMost(actualMaxInterval)

                // Try to spread out the schedule more (skewed to the maximum value)
                val skewedRandom = 1 - Math.random().pow(2.0)
                val interval = ((actualMaxInterval - actualMinInterval) * skewedRandom + actualMinInterval).toLong()
                intervalTime.add(interval)
            }

            Log.v(TAG, "Intervals: ${intervalTime.map{ it.formatLapsedTime() }}")

            val intervalSum = intervalTime.sum()
            val startMargin = (Math.random() * (lengthOfDay - intervalSum)).toLong()

            var accumulatedTime = startMargin + startTime
            intervalTime.forEach {
                accumulatedTime += it
                scheduleStorage.addSchedule(SurveySchedule(
                    surveyId = surveyConfig.id,
                    triggerTime = accumulatedTime
                ))
            }
        }

        return scheduleStorage.getNextSchedule()
    }

    private fun setupNextSurvey() {
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

    private val scheduleCheckCallback = { intent: Intent? -> setupNextSurvey() }

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

        setupNextSurvey()
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