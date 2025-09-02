package kaist.iclab.tracker.sensor.galaxywatch

import android.Manifest
import android.health.connect.HealthPermissions
import android.os.Build
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.listener.SamsungHealthSensorInitializer
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.serialization.Serializable

class SkinTemperatureSensor(
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
    samsungHealthSensorInitializer: SamsungHealthSensorInitializer
) : BaseSensor<SkinTemperatureSensor.Config, SkinTemperatureSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    override val permissions = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) HealthPermissions.READ_SKIN_TEMPERATURE else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config : SensorConfig

    override val initialConfig: Config = Config()

    @Serializable
    data class Entity(
        val received: Long,
        val timestamp: Long,
        val objectTemperature: Float,
        val ambientTemperature: Float,
        val status: Int
    ) : SensorEntity()


    private val tracker by lazy {
        samsungHealthSensorInitializer.getTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
    }


    private val listener = SamsungHealthSensorInitializer.DataListener { dataPoint ->
        val timestamp = System.currentTimeMillis()
        listeners.forEach {
            it.invoke(
                Entity(
                    timestamp,
                    dataPoint.timestamp,
                    dataPoint.getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE),
                    dataPoint.getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE),
                    dataPoint.getValue(ValueKey.SkinTemperatureSet.STATUS)
                )
            )
        }
    }

    override fun onStart() {
        tracker.setEventListener(listener)
    }

    override fun onStop() {
        tracker.unsetEventListener()
    }
}
