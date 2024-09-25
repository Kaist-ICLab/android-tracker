package kaist.iclab.lab_galaxywatch_tracker.data.collector

import android.content.Context
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.lab_galaxywatch_tracker.data.source.HealthTrackerSource
import kaist.iclab.lab_galaxywatch_tracker.data.trigger.HealthTrackerTrigger

class SkinTempCollector(
    context: Context,
    database: DatabaseInterface,
    healthTrackerSource: HealthTrackerSource
) : WearableSensorCollector(context, database, healthTrackerSource) {
    override val NAME: String = "SKIN_TEMP"

    override val trigger = object : HealthTrackerTrigger() {
        override fun onDataReceived(dataPoints: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            dataPoints.forEach {
                database.insert(
                    NAME,
                    mapOf(
                        "received" to timestamp,
                        "timestamp" to it.timestamp,
                        "ambientTemp" to it.getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE),
                        "objectTemp" to it.getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE),
                        "status" to it.getValue(ValueKey.SkinTemperatureSet.STATUS)
                    )
                )
            }
        }
    }

    override val tracker = healthTrackerSource.getTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
}