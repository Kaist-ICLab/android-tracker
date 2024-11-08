package kaist.iclab.tracker.collectors

import android.content.Context
import android.content.pm.ServiceInfo
import android.net.TrafficStats
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.triggers.AlarmTrigger
import java.util.concurrent.TimeUnit

class DataTrafficStatCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<DataTrafficStatCollector.Config, DataTrafficStatCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>(
//        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) android.Manifest.permission.SCHEDULE_EXACT_ALARM else null,

    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>(
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED else null,
    ).toTypedArray()

    data class Config(
        val interval: Long,
    ) : CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.SECONDS.toMillis(5)
    )

    override fun start() {
        super.start()
        alarmTrigger = AlarmTrigger(context, ACTION, CODE,
            defaultConfig.interval) {
            val timestamp = System.currentTimeMillis()
            listener?.invoke(
                Entity(
                    timestamp,
                    timestamp,
//                TrafficStats.getTotalRxBytes(),
//                TrafficStats.getTotalTxBytes(),
//                TrafficStats.getMobileRxBytes(),
//                TrafficStats.getMobileTxBytes(),
                )
            )
        }
        alarmTrigger.register()
    }

    override fun stop() {
        alarmTrigger.unregister()
        super.stop()
    }

    override fun isAvailable() = Availability(true)


    val ACTION = "kaist.iclab.tracker.ACTION_DATA_TRAFFIC_STAT"
    val CODE = 0x11

    lateinit var alarmTrigger: AlarmTrigger

    data class Entity(
        override val received: Long,
        val timestamp: Long,
//        val totalRx: Long,
//        val totalTx: Long,
//        val mobileRx: Long,
//        val mobileTx: Long,
    ) : DataEntity(received)

}