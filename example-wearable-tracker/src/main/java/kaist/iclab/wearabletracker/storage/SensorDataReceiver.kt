package kaist.iclab.wearabletracker.storage

import android.util.Log
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity

class SensorDataReceiver(
    private val sensors: List<Sensor<*, SensorEntity>>
) {
    private val listener = sensors.associate {
        it.name to { e: SensorEntity -> Log.d(it.name, e.toString()); Unit }
    }

    fun registerListener() {
        for (sensor in sensors) {
            sensor.addListener(listener[sensor.name]!!)
        }
    }

    fun unregisterListener() {
        for (sensor in sensors) {
            sensor.removeListener(listener[sensor.name]!!)
        }
    }
}