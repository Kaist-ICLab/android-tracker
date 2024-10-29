package kaist.iclab.tracker.collectors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.concurrent.TimeUnit

class AmbientLightCollector(
    override val context: Context
) : AbstractCollector(context) {
    var config: Config = Config(
        TimeUnit.MINUTES.toMillis(3),
    )

    data class DataEntity(
        val timestamp: Long,
        val accuracy: Int,
        val value: Float
    ) : AbstractCollector.DataEntity()


    data class Config(
        val interval: Long
    ) : AbstractCollector.Config()


    override val permissions = listOfNotNull<String>().toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    // Check whether there is a light sensor
    override fun isAvailable(): Boolean {
        return sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null
    }

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val sensorEventListener = object:SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                listener?.invoke(
                    DataEntity(
                        System.currentTimeMillis(),
                        it.accuracy,
                        it.values[0]
                    )
                )
            }
        }
    }

    override fun start() {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { sensor ->
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                TimeUnit.MILLISECONDS.toMicros(config.interval).toInt()
            )
        }
    }

    override fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}