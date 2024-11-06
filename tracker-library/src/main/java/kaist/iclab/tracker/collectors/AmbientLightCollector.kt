package kaist.iclab.tracker.collectors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.CollectorState
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import java.util.concurrent.TimeUnit

class AmbientLightCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface,
) : AbstractCollector<AmbientLightCollector.Config, AmbientLightCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    data class Config(
        val interval: Long
    ) : CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.SECONDS.toMillis(3)
    )

    // Check whether there is a light sensor
    override fun isAvailable(): Availability {
        val status = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null
        return Availability(status, if (status) null else "AmbientLight Sensor is not available")
    }

    override fun enable() {
        if (_stateFlow.value.flag == CollectorState.FLAG.DISABLED) {
            _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
        }
    }

    override fun disable() {
        if (_stateFlow.value.flag == CollectorState.FLAG.ENABLED) {
            _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
        }
    }

    override fun start() {
        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.RUNNING))
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { sensor ->
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                TimeUnit.MILLISECONDS.toMicros(configFlow.value.interval).toInt()
            )
        }
    }

    override fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val accuracy: Int,
        val value: Float
    ) : DataEntity(received)

    private val sensorManager: SensorManager by lazy{
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }


    private val sensorEventListener = object:SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}
        override fun onSensorChanged(event: SensorEvent?) {
            val timestamp = System.currentTimeMillis()
            event?.let {
                listener?.invoke(
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