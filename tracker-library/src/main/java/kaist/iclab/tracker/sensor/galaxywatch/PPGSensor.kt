package kaist.iclab.tracker.sensor.galaxywatch

import android.Manifest
import android.content.Context
import android.os.Build
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.PpgType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.listener.SamsungHealthSensorInitializer
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage

class PPGSensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    stateStorage: StateStorage<SensorState>,
    samsungHealthSensorInitializer: SamsungHealthSensorInitializer
) : BaseSensor<PPGSensor.Config, PPGSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    override val permissions = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config : SensorConfig

    override val defaultConfig: Config = Config()

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val green: Int,
        val red: Int,
        val ir: Int,
        val greenStatus: Int,
        val redStatus: Int,
        val irStatus: Int,
    ) : SensorEntity


    private val tracker by lazy {
        samsungHealthSensorInitializer.getTracker(HealthTrackerType.PPG_CONTINUOUS, setOf(PpgType.GREEN, PpgType.RED, PpgType.IR))
    }


    private val listener = SamsungHealthSensorInitializer.DataListener { dataPoint ->
        val timestamp = System.currentTimeMillis()
        listeners.forEach {
            it.invoke(
                Entity(
                    timestamp,
                    dataPoint.timestamp,
                    dataPoint.getValue(ValueKey.PpgSet.PPG_GREEN),
                    dataPoint.getValue(ValueKey.PpgSet.PPG_RED),
                    dataPoint.getValue(ValueKey.PpgSet.PPG_IR),
                    dataPoint.getValue(ValueKey.PpgSet.GREEN_STATUS),
                    dataPoint.getValue(ValueKey.PpgSet.RED_STATUS),
                    dataPoint.getValue(ValueKey.PpgSet.IR_STATUS),
                )
            )
        }
    }

    override fun init() {}

    override fun onStart() {
        tracker.setEventListener(listener)
    }

    override fun onStop() {
        tracker.unsetEventListener()
    }
}
