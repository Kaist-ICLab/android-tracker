package kaist.iclab.tracker.collectors.OLD//package kaist.iclab.tracker.collectors
//
//import android.content.Context
//import android.net.TrafficStats
//import kaist.iclab.tracker.triggers.AlarmTrigger
//
//class DataTrafficCollector(
//    override val context: Context,
//) : AbstractCollector(context){
//
//    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"
//    val ACTION_CODE = 0x3
//
//    var config: Config = Config(60000L)
//
//    override val permissions: Array<String> = arrayOf()
//
//    data class Config(
//        val interval: Long
//    ): AbstractCollector.Config()
//
//    data class DataEntity(
//        val timestamp: Long,
//        val totalRx: Long,
//        val totalTx: Long,
//        val mobileRx: Long,
//        val mobileTx: Long,
//    ): AbstractCollector.DataEntity()
//
//
//    override fun isAvailable(): Boolean = TrafficStats.UNSUPPORTED !in listOf(
//        TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes(),
//        TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes()
//    ).map { it.toInt() }
//
//    lateinit var alarmTrigger: AlarmTrigger
//
//    override fun start() {
//        alarmTrigger = AlarmTrigger(
//            context,
//            ACTION,
//            ACTION_CODE,
//            config.interval
//        ) {
//             listener?.invoke(
//                 DataEntity(
//                     System.currentTimeMillis(),
//                     TrafficStats.getTotalRxBytes(),
//                     TrafficStats.getTotalTxBytes(),
//                     TrafficStats.getMobileRxBytes(),
//                     TrafficStats.getMobileTxBytes()
//                 )
//             )
//        }
//        alarmTrigger.register()
//    }
//    override fun stop() {
//        alarmTrigger.unregister()
//    }
//}