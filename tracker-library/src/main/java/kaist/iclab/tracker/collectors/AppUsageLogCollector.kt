//package kaist.iclab.tracker.collectors
//
//import android.Manifest
//import android.app.PendingIntent
//import android.app.usage.UsageEvents
//import android.app.usage.UsageStatsManager
//import android.content.Context
//import android.content.Intent
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.net.wifi.WifiManager
//import android.os.Build
//import com.google.android.gms.location.ActivityRecognition
//import com.google.android.gms.location.ActivityRecognitionClient
//import com.google.android.gms.location.ActivityTransition
//import com.google.android.gms.location.ActivityTransitionRequest
//import com.google.android.gms.location.ActivityTransitionResult
//import com.google.android.gms.location.DetectedActivity
//import kaist.iclab.tracker.triggers.AlarmTrigger
//import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
//import java.util.concurrent.TimeUnit
//
//class AppUsageLogCollector(
//    override val context: Context
//) : AbstractCollector(context) {
//
//    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"
//    val CODE = 0xEE
//
//    var config = Config(
//        TimeUnit.MINUTES.toMillis(30)
//    )
//
//    data class Config(
//        val interval: Long,
//    ) : AbstractCollector.Config()
//
//    data class DataEntity(
//        val timestamp: Long,
//        val packageName: String,
////        val installed: String,
//        val eventType: Int
//    ) : AbstractCollector.DataEntity()
//
//    override val permissions = listOfNotNull(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Manifest.permission.PACKAGE_USAGE_STATS else null,
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    override fun isAvailable(): Boolean = true
//
//    private val alarmTrigger  = AlarmTrigger(
//        context,
//        ACTION,
//        CODE,
//        config.interval
//    ){
//        val usageStatManager =context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val timestamp = System.currentTimeMillis()
//        /*Give margin for alarm amy not correctly given*/
//        val events = usageStatManager.queryEvents(timestamp - config.interval - TimeUnit.MINUTES.toMillis(5), timestamp)
//        val event = UsageEvents.Event()
//        while(events.hasNextEvent()){
//            events.getNextEvent(event)
//            listener?.invoke(DataEntity(event.timeStamp, event.packageName, event.eventType))
//        }
//    }
//
//
//    override fun start() {
//        alarmTrigger.register()
//    }
//
//    override fun stop() {
//        alarmTrigger.unregister()
//    }
//}