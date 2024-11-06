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
//import android.service.notification.NotificationListenerService
//import android.service.notification.StatusBarNotification
//import android.util.Log
//import androidx.core.app.NotificationManagerCompat
//import androidx.transition.Visibility
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
//class NotificationCollector(
//    override val context: Context
//) : AbstractCollector(context) {
//
//    data class DataEntity(
//        val timestamp: Long,
//        val packageName: String,
//        val eventType: String,
//        val title: String,
//        val text: String,
//        val visibility: Int,
//        val category: String
//    ) : AbstractCollector.DataEntity()
//
//    override val permissions = listOfNotNull<String>(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    override fun isAvailable(): Boolean =
//        context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)
//
//    object NotificationTrigger: NotificationListenerService() {
//
//        var instance: NotificationTrigger? = null
//        var isListening = false
//        var listener: ((DataEntity) -> Unit)? = null
//        fun startListening() {
//            isListening = true
//        }
//
//        fun stopListening() {
//            isListening = false
//        }
//
//        override fun onCreate() {
//            super.onCreate()
//            instance = this
//        }
//
//        override fun onDestroy() {
//            super.onDestroy()
//            instance = null
//        }
//
//        override fun onNotificationPosted(sbn: StatusBarNotification) {
//            handleNotf(sbn, "POSTED")
//        }
//
//        override fun onNotificationRemoved(sbn: StatusBarNotification) {
//            handleNotf(sbn, "REMOVED")
//        }
//
//        private fun handleNotf(sbn: StatusBarNotification, eventType: String) {
//            if (isListening) {
//                listener?.invoke(
//                    DataEntity(
//                        System.currentTimeMillis(),
//                        sbn.packageName,
//                        eventType,
//                        sbn.notification.extras.getString("android.title") ?: "",
//                        sbn.notification.extras.getString("android.text") ?: "",
//                        sbn.notification.visibility,
//                        sbn.notification.category
//                    )
//                )
//            }
//        }
//    }
//
//
//    override fun start() {
//        NotificationTrigger.instance?.let {
//            it.listener = listener
//            it.startListening()
//        }
//    }
//
//    override fun stop() {
//        NotificationTrigger.instance?.stopListening()
//    }
//}