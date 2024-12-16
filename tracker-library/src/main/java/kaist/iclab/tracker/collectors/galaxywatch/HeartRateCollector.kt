package kaist.iclab.tracker.collectors.galaxywatch

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.collectors.core.AbstractCollector
import kaist.iclab.tracker.collectors.core.Availability
import kaist.iclab.tracker.collectors.core.CollectorConfig
import kaist.iclab.tracker.collectors.core.DataEntity
import kaist.iclab.tracker.listeners.SamsungHealthSensorListener
import kaist.iclab.tracker.permission.PermissionManagerInterface

class HeartRateCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface,
    healthSensosrListener: SamsungHealthSensorListener
) : AbstractCollector<HeartRateCollector.Config, HeartRateCollector.Entity>(
    permissionManager
) {
    override val permissions = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config : CollectorConfig()

    override val defaultConfig = Config()

    override fun isAvailable(): Availability = Availability(true)

    private val tracker = healthSensosrListener.getTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)

    val trigger = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            dataPoints.forEach { dataPoint ->
                listener?.invoke(
                    Entity(
                        timestamp,
                        dataPoint.timestamp,
                        dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE),
                        dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS),
                        dataPoint.getValue(ValueKey.HeartRateSet.IBI_LIST),
                        dataPoint.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST),
                    )
                )
            }
        }

        override fun onError(trackerError: HealthTracker.TrackerError) {
            Log.d(TAG, "onError")
            when (trackerError) {
                HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
                    TAG,
                    "ERROR: Permission Failed"
                )

                HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
                    TAG,
                    "ERROR: SDK Policy Error"
                )

                else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
            }
        }

        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
        }
    }

    override fun start() {
        super.start()
        tracker.setEventListener(trigger)
    }

    override fun stop() {
        tracker.unsetEventListener()
        super.stop()
    }


    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val hr: Int,
        val hrStatus: Int,
        val ibi: List<Int>,
        val ibiStatus: List<Int>,
    ) : DataEntity(received)
}