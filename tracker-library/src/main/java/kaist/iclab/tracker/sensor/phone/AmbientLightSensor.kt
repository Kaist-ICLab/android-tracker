package kaist.iclab.tracker.sensor.phone

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.TimeUnit

class AmbientLightSensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    stateStorage: StateStorage<SensorState>,
) : BaseSensor<AmbientLightSensor.Config, AmbientLightSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long
    ) : SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val accuracy: Int,
        val value: Float
    ) : SensorEntity

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(3)
    )
    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorEventListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}
        override fun onSensorChanged(event: SensorEvent?) {
            val timestamp = System.currentTimeMillis()
            event?.let {
                listeners.forEach { listener ->
                    listener.invoke(
                        Entity(
                            timestamp,
                            it.timestamp,
                            it.accuracy,
                            it.values[0]
                        )
                    )
                }
            }
        }
    }

    override fun init() {
        Log.v("AmbientLightCollector", "Is init ever called?")

//        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null)
//            sensorStateFlow.value = SensorState(SensorState.FLAG.UNAVAILABLE)
//        return Availability(status, if (status) null else "AmbientLight Sensor is not available")
    }

    override fun onStart() {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { sensor ->
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                TimeUnit.MILLISECONDS.toMicros(configStateFlow.value.interval).toInt()
            )
        }
    }

    override fun onStop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}