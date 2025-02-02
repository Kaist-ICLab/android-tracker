package kaist.iclab.tracker.collector.phone

import android.content.Context
import android.net.TrafficStats
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.listener.AlarmListener
import java.util.concurrent.TimeUnit

//class DataTrafficStatCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<DataTrafficStatCollector.Config, DataTrafficStatCollector.Entity>(permissionManager) {
//    override val permissions = listOfNotNull<String>().toTypedArray()
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    data class Config(
//        val interval: Long,
//    ) : CollectorConfig()
//
//    override val defaultConfig = Config(
//        TimeUnit.MINUTES.toMillis(1)
//    )
//
//    override fun start() {
//        super.start()
//        alarmListener = AlarmListener(context, ACTION, CODE,
//            defaultConfig.interval) {
//            val timestamp = System.currentTimeMillis()
//            listener?.invoke(
//                Entity(
//                    timestamp,
//                    timestamp,
//                TrafficStats.getTotalRxBytes(),
//                TrafficStats.getTotalTxBytes(),
//                TrafficStats.getMobileRxBytes(),
//                TrafficStats.getMobileTxBytes(),
//                )
//            )
//        }
//        alarmListener.register()
//    }
//
//    override fun stop() {
//        alarmListener.unregister()
//        super.stop()
//    }
//
//    override fun isAvailable() = Availability(true)
//
//    val ACTION = "kaist.iclab.tracker.ACTION_DATA_TRAFFIC_STAT"
//    val CODE = 0x11
//
//    lateinit var alarmListener: AlarmListener
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val totalRx: Long,
//        val totalTx: Long,
//        val mobileRx: Long,
//        val mobileTx: Long,
//    ) : DataEntity(received)
//
//}