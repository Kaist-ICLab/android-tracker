//package kaist.iclab.tracker.collectors
//
//import android.Manifest
//import android.app.PendingIntent
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
//import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
//import java.util.concurrent.TimeUnit
//
//class ActivityTransitionCollector(
//    override val context: Context
//) : AbstractCollector(context) {
//
//    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"
//    val CODE = 0xF1
//
//    data class DataEntity(
//        val timestamp: Long,
//        val activityType: Int,
//        val transitionType: Int
//    ) : AbstractCollector.DataEntity()
//
//    override val permissions = listOfNotNull(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//
//    override fun isAvailable(): Boolean = true
//
//    private val client: ActivityRecognitionClient by lazy {
//        ActivityRecognition.getClient(context)
//    }
//
//    private val activityTransitionIntent by lazy {
//        PendingIntent.getBroadcast(
//            context, CODE,
//            Intent(ACTION),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//
//    private val broadcastTrigger = SystemBroadcastTrigger(
//        context,
//        arrayOf(
//            ACTION
//        )
//    ) {
//        val result = ActivityTransitionResult.extractResult(it)
//        result?.transitionEvents?.forEach { event ->
//            listener?.invoke(
//                DataEntity(
//                    System.currentTimeMillis(),
//                    event.activityType,
//                    event.transitionType
//                )
//            )
//        }
//    }
//
//
//    override fun start() {
//        val request = listOf(
//            DetectedActivity.IN_VEHICLE,
//            DetectedActivity.ON_BICYCLE,
//            DetectedActivity.RUNNING,
//            DetectedActivity.ON_FOOT,
//            DetectedActivity.STILL,
//            DetectedActivity.WALKING
//        ).map { activity ->
//            listOf(
//                ActivityTransition.Builder()
//                    .setActivityType(activity)
//                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//                    .build(),
//                ActivityTransition.Builder()
//                    .setActivityType(activity)
//                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
//                    .build()
//            )
//        }.flatten().let { ActivityTransitionRequest(it) }
//        client.requestActivityTransitionUpdates(request, activityTransitionIntent)
//        broadcastTrigger.register()
//    }
//
//    override fun stop() {
//        client.removeActivityTransitionUpdates(activityTransitionIntent)
//        broadcastTrigger.unregister()
//    }
//}