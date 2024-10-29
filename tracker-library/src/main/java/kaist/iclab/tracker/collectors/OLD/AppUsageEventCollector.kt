package kaist.iclab.tracker.collectors.OLD//package kaist.iclab.tracker.collectors
//
//import android.Manifest
//import android.app.usage.UsageEvents
//import android.app.usage.UsageStatsManager
//import android.content.Context
//import android.os.Build
//import android.util.Log
//import kaist.iclab.tracker.database.DatabaseInterface
//import kaist.iclab.tracker.triggers.AlarmTrigger
//import java.util.concurrent.TimeUnit
//import kotlin.math.max
//
//
//class AppUsageEventCollector(
//    override val context: Context,
//    override val database: DatabaseInterface
//):AbstractCollector(context, database) {
//    companion object {
//        const val NAME = "APP_USAGE_EVENT"
//        const val TAG = "AppUsageEventCollector"
//        const val ACTION_REQUEST = "kaist.iclab.tracker.ACTION_APP_USAGE_EVENT_REQUEST"
//        const val ACTION_CODE_REQUEST = 0x2
//    }
//
//    override val NAME: String
//        get() = Companion.NAME
//
//    override val permissions: Array<String> = arrayOf(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Manifest.permission.PACKAGE_USAGE_STATS else null
//    ).filterNotNull().toTypedArray()
//
//    var trigger: AlarmTrigger? = null
//
//    var lastTimeDataWritten:Long = 0L
//
//    fun listener() {
//        val currTime = System.currentTimeMillis()
//        val prevTime = max(
//            currTime - TimeUnit.HOURS.toMillis(12),
//            lastTimeDataWritten
//        )
//        val usageStatManager =
//            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val events = usageStatManager.queryEvents(prevTime, currTime)
//        while (events.hasNextEvent()) {
//            val event = UsageEvents.Event()
//            events.getNextEvent(event)
//            event.toMap().let {
//                Log.d(TAG, "EVENT $it")
//            }
//        }
//    }
//
//    fun UsageEvents.Event.toMap(): Map<String, Any> {
//        val data = mutableMapOf(
//            "timestamp" to this.timeStamp,
//            "package_name" to this.packageName,
//            "class_name" to this.className,
//            "event_type" to this.eventType,
//            // "configuration" to this.configuration, change it if there is need
//        )
//        /*TODO: getExtras parsing which is added on API Level 35*/
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            data["app_stand_by_bucket"] = this.appStandbyBucket
//        }
//        return data
//    }
//
//    /* Check whether the system allow to collect data
//    * In case of sensor malfunction or broken, it would not be available.*/
//    override fun isAvailable() = true
//
//
//    /* Start collector to collect data
//    * */
//    override fun start(){
//        trigger = AlarmTrigger(
//            context,
//            ACTION_REQUEST,
//            ACTION_CODE_REQUEST,
//            60000L
//        ) {
//            listener()
//        }
//        trigger?.register()
//    }
//
//    /* Stop collector to stop collecting data
//    * */
//    override fun stop(){
//        trigger?.unregister()
//        trigger = null
//    }
//
//}