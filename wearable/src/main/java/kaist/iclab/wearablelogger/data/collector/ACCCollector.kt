package kaist.iclab.wearablelogger.data.collector

import android.content.Context
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import dev.iclab.tracker.database.DatabaseInterface
import kaist.iclab.wearablelogger.data.source.HealthTrackerSource
import kaist.iclab.wearablelogger.data.trigger.HealthTrackerTrigger

class ACCCollector(
    context: Context,
    database: DatabaseInterface,
    healthTrackerSource: HealthTrackerSource
) : WearableSensorCollector(context, database, healthTrackerSource) {
    override val NAME: String = "ACC"

    override val trigger = object : HealthTrackerTrigger() {
        override fun onDataReceived(dataPoints: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            dataPoints.forEach {
                database.insert(
                    NAME,
                    mapOf(
                        "received" to timestamp,
                        "timestamp" to it.timestamp,
                        "x" to it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X),
                        "y" to it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y),
                        "z" to it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z)
                    )
                )
            }
        }
    }

    override val tracker = healthTrackerSource.getTracker(HealthTrackerType.ACCELEROMETER)
}