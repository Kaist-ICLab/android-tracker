package kaist.iclab.wearablelogger.data

import android.content.Context
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.ValueKey
import dev.iclab.tracker.database.DatabaseInterface

class PPGGreenCollector(
    context: Context,
    database: DatabaseInterface
) : WearableSensorCollector(context, database) {
    override val NAME: String = "PPG_GREEN"


    private val trigger = object : HealthTrackerTrigger() {
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

    override fun isAvailable(): Boolean = true

    override fun start() {

    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}