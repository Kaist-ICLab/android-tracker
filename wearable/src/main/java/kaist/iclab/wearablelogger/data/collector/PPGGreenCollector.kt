package kaist.iclab.wearablelogger.data.collector

import android.content.Context
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import dev.iclab.tracker.database.DatabaseInterface
import kaist.iclab.wearablelogger.data.source.HealthTrackerSource
import kaist.iclab.wearablelogger.data.trigger.HealthTrackerTrigger

class PPGGreenCollector(
    context: Context,
    database: DatabaseInterface,
    healthTrackerSource: HealthTrackerSource
) : WearableSensorCollector(context, database, healthTrackerSource) {
    override val NAME: String = "PPG_GREEN"

    override val trigger = object : HealthTrackerTrigger() {
        override fun onDataReceived(dataPoints: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            dataPoints.forEach {
                database.insert(
                    NAME,
                    mapOf(
                        "received" to timestamp,
                        "timestamp" to it.timestamp,
                        "ppg" to it.getValue(ValueKey.PpgGreenSet.PPG_GREEN),
                        "status" to it.getValue(ValueKey.PpgGreenSet.STATUS)
                    )
                )
            }
        }
    }

    override val tracker = healthTrackerSource.getTracker(HealthTrackerType.PPG_GREEN)
}