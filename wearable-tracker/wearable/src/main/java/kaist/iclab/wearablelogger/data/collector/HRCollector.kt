package kaist.iclab.wearablelogger.data.collector

import android.content.Context
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import dev.iclab.tracker.database.DatabaseInterface
import kaist.iclab.wearablelogger.data.source.HealthTrackerSource
import kaist.iclab.wearablelogger.data.trigger.HealthTrackerTrigger

class HRCollector(
    context: Context,
    database: DatabaseInterface,
    healthTrackerSource: HealthTrackerSource
) : WearableSensorCollector(context, database, healthTrackerSource) {
    override val NAME: String = "HR"

    override val trigger = object : HealthTrackerTrigger() {
        override fun onDataReceived(dataPoints: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            dataPoints.forEach {
                database.insert(
                    NAME,
                    mapOf(
                        "received" to timestamp,
                        "timestamp" to it.timestamp,
                        "hr" to it.getValue(ValueKey.HeartRateSet.HEART_RATE),
                        "hrStatus" to it.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS),
                        "ibi" to it.getValue(ValueKey.HeartRateSet.IBI_LIST),
                        "ibiStatus" to it.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST),
                    )
                )
            }
        }
    }

    override val tracker = healthTrackerSource.getTracker(HealthTrackerType.HEART_RATE)
}