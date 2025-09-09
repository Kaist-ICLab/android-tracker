package kaist.iclab.tracker.sensor.galaxywatch

import android.Manifest
import android.os.Build
import android.util.Log
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.listener.SamsungHealthSensorInitializer
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// NOTE: EDA measurement is available with Galaxy Watch 8 series or later models.

class EDASensor(
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
    private val samsungHealthSensorInitializer: SamsungHealthSensorInitializer,
) : BaseSensor<EDASensor.Config, EDASensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    override val permissions = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) "com.samsung.android.hardware.sensormanager.permission.READ_ADDITIONAL_HEALTH_DATA" else null,
        Manifest.permission.BODY_SENSORS,
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
        val skinConductance: Float,
        val status: Int
    ) : SensorEntity()


    private val tracker by lazy {
        samsungHealthSensorInitializer.getTracker(HealthTrackerType.EDA_CONTINUOUS)
    }


    private val listener = SamsungHealthSensorInitializer.DataListener { dataPoint ->
        val timestamp = System.currentTimeMillis()
        listeners.forEach {
            it.invoke(
                Entity(
                    timestamp,
                    dataPoint.timestamp,
                    dataPoint.getValue(ValueKey.EdaSet.SKIN_CONDUCTANCE),
                    dataPoint.getValue(ValueKey.EdaSet.STATUS)
                )
            )
        }
    }

    override fun init() {
        super.init()

        // Check EDA support on this device
        // Since binding to the service takes a while, we subscribe to the connection stateflow and check it when it is actually binded
        CoroutineScope(Dispatchers.IO).launch {
            samsungHealthSensorInitializer.connectionStateFlow.collect { isConnected ->
                if(!isConnected) return@collect
                if (!samsungHealthSensorInitializer.isTrackerAvailable(HealthTrackerType.EDA_CONTINUOUS)) {
                    Log.w(name, "EDASensor is unavailable")
                    stateStorage.set(SensorState(SensorState.FLAG.UNAVAILABLE, "EDA not supported on this device"))
                }

                this.cancel()
            }
        }
    }

    override fun onStart() {
        tracker.setEventListener(listener)
    }

    override fun onStop() {
        tracker.unsetEventListener()
    }
}
