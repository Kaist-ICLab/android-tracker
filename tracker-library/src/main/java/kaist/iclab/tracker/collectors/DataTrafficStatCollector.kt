package kaist.iclab.tracker.collectors

import android.content.Context
import android.net.TrafficStats
import kaist.iclab.tracker.collectors.LocationCollector.Config
import kaist.iclab.tracker.triggers.AlarmTrigger
import java.util.concurrent.TimeUnit

class DataTrafficStatCollector(
    override val context: Context
) : AbstractCollector(context) {

    val ACTION = "kaist.iclab.tracker.ACTION_DATA_TRAFFIC_STAT"
    val CODE = 0x1

    var config: Config = Config(
        TimeUnit.MINUTES.toMillis(3)
    )

    data class DataEntity(
        val timestamp: Long,
        val totalRx: Long,
        val totalTx: Long,
        val mobileRx: Long,
        val mobileTx: Long,
    ) : AbstractCollector.DataEntity()

    data class Config(
        val interval: Long,
    ) : AbstractCollector.Config()

    override val permissions = listOfNotNull<String>().toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()


    override fun isAvailable(): Boolean = true

    private val alarmTrigger = AlarmTrigger(context, ACTION, CODE,
        config.interval) {
        val timestamp = System.currentTimeMillis()
        listener?.invoke(
            DataEntity(
                timestamp,
                TrafficStats.getTotalRxBytes(),
                TrafficStats.getTotalTxBytes(),
                TrafficStats.getMobileRxBytes(),
                TrafficStats.getMobileTxBytes(),
            )
        )
    }

    override fun start() {
        alarmTrigger.register()
    }

    override fun stop() {
        alarmTrigger.unregister()
    }
}