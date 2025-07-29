package kaist.iclab.tracker.sensor.phone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import com.samsung.android.sdk.health.data.request.Ordering
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.listener.SamsungHealthDataInitializer
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.max

class StepSensor(
    context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    val stateStorage: StateStorage<SensorState>,
    samsungHealthDataInitializer: SamsungHealthDataInitializer
): BaseSensor<StepSensor.Config, StepSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    override val permissions = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
        DataTypes.STEPS.name,
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH else null
    ).toTypedArray()

    data class Config(
        // The time period to sync past step data at the initialization of the class, in seconds
        val syncPastLimitSeconds: Long,
        // The time period to load step data at each invocation, as step data sync is often not in real-time.
        val timeMarginSeconds: Long,
        val bucketSizeMinutes: Int,
        val readIntervalMillis: Long,
    ): SensorConfig

//    override val defaultConfig: Config = Config(
//        syncPastLimitSeconds = TimeUnit.DAYS.toSeconds(7),
//        timeMarginSeconds = TimeUnit.HOURS.toSeconds(1),
//        bucketSizeMinutes = 10
//    )

    data class Entity(
        val timestamp: Long,
        val startTime: Long,
        val endTime: Long,
        val steps: Long
    ): SensorEntity

    private val actionName = "kaist.iclab.tracker.${NAME}_REQUEST"
    private val store = samsungHealthDataInitializer.store
    private val actionCode = 0x11
    private val alarmListener = AlarmListener(
        context = context,
        actionName = actionName,
        actionCode = actionCode,
        defaultConfig.readIntervalMillis
    )

    private var lastSynced = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(configStateFlow.value.syncPastLimitSeconds)

    private val mainCallback = { _: Intent? ->
        val config = configStateFlow.value
        val fromTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSynced), ZoneId.systemDefault())
            .minusSeconds(config.timeMarginSeconds)

        val req = DataType.StepsType
            .TOTAL
            .requestBuilder
            .setLocalTimeFilterWithGroup(
                LocalTimeFilter.since(
                    fromTime
                ),
                LocalTimeGroup.of(LocalTimeGroupUnit.MINUTELY, config.bucketSizeMinutes)
            )
            .setOrdering(Ordering.ASC)
            .build()
        val timestamp = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            store.aggregateData(req).dataList.forEach {
                val entity = Entity(
                    timestamp = timestamp,
                    startTime = it.startTime.toEpochMilli(),
                    endTime = it.endTime.toEpochMilli(),
                    steps = it.value ?: 0
                )
                Log.d(NAME, entity.toString())
                lastSynced = max(lastSynced, it.endTime.toEpochMilli())

                listeners.forEach {
                    it.invoke(entity)
                }
            }
        }
        Unit
    }

    override fun init() {
        stateStorage.set(SensorState(SensorState.FLAG.DISABLED, ""))
    }

    override fun onStart() {
        alarmListener.addListener(mainCallback)
    }

    override fun onStop() {
        alarmListener.removeListener(mainCallback)
    }
}