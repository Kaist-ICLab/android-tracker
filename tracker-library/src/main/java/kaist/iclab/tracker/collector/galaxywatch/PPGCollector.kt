package kaist.iclab.tracker.collector.galaxywatch

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.listener.SamsungHealthSensorListener
import kaist.iclab.tracker.permission.PermissionManagerInterface

//class PPGCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface,
//    healthSensosrListener: SamsungHealthSensorListener
//) : AbstractCollector<PPGCollector.Config, PPGCollector.Entity>(
//    permissionManager
//) {
//    override val permissions = listOfNotNull(
//        Manifest.permission.BODY_SENSORS,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    /*No attribute required... can not be data class*/
//    class Config : CollectorConfig()
//
//    override val defaultConfig = Config()
//
//    override fun isAvailable(): Availability = Availability(true)
//
//    private val tracker = healthSensosrListener.getTracker(HealthTrackerType.PPG_CONTINUOUS)
//
//    val trigger = object : HealthTracker.TrackerEventListener {
//        override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
//            val timestamp = System.currentTimeMillis()
//            dataPoints.forEach { dataPoint ->
//                listener?.invoke(
//                    Entity(
//                        timestamp,
//                        dataPoint.timestamp,
//                        dataPoint.getValue(ValueKey.PpgSet.PPG_GREEN),
//                        dataPoint.getValue(ValueKey.PpgSet.PPG_RED),
//                        dataPoint.getValue(ValueKey.PpgSet.PPG_IR),
//                        dataPoint.getValue(ValueKey.PpgSet.GREEN_STATUS),
//                        dataPoint.getValue(ValueKey.PpgSet.RED_STATUS),
//                        dataPoint.getValue(ValueKey.PpgSet.IR_STATUS),
//                    )
//                )
//            }
//        }
//
//        override fun onError(trackerError: HealthTracker.TrackerError) {
//            Log.d(TAG, "onError")
//            when (trackerError) {
//                HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
//                    TAG,
//                    "ERROR: Permission Failed"
//                )
//
//                HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
//                    TAG,
//                    "ERROR: SDK Policy Error"
//                )
//
//                else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
//            }
//        }
//
//        override fun onFlushCompleted() {
//            Log.d(TAG, "onFlushCompleted")
//        }
//    }
//
//    override fun start() {
//        super.start()
//        tracker.setEventListener(trigger)
//    }
//
//    override fun stop() {
//        tracker.unsetEventListener()
//        super.stop()
//    }
//
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val green: Int,
//        val red: Int,
//        val ir: Int,
//        val greenStatus: Int,
//        val redStatus: Int,
//        val irStatus: Int,
//    ) : DataEntity(received)
//}