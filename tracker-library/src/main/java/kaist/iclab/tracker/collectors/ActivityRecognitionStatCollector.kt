package kaist.iclab.tracker.collectors

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

class ActivityRecognitionStatCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<ActivityRecognitionStatCollector.Config, ActivityRecognitionStatCollector.Entity>(
    permissionManager
) {
    override val permissions = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH else null
    ).toTypedArray()

    data class Config(
        val interval: Long
    ) : CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.SECONDS.toMillis(15)
    )

    override fun isAvailable(): Availability {
        val status = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        } else {
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isLocationEnabled
        }
        if (!status) return Availability(false, "Location service is disabled")
        else return Availability(true)
    }


    override fun start() {
        super.start()
        Log.d("AR", "started ${configFlow.value.interval}")
        client.requestActivityUpdates(configFlow.value.interval, activityRecognitionIntent)
//            .addOnFailureListener {
//            Log.e("AR", "failed to request activity updates", it)
//        }.addOnSuccessListener {
//            Log.d("AR", "succeeded to request activity updates")
//        }.addOnCanceledListener {
//            Log.e("AR", "canceled to request activity updates" )
//        }
        broadcastTrigger.register()
//        CoroutineScope(Dispatchers.IO).launch {
//            sleep(10000)
//            Log.d("AR", "send")
//            activityRecognitionIntent.send(context, 0, Intent(ACTION))
//        }
    }

    override fun stop() {
        client.removeActivityUpdates(activityRecognitionIntent)
        broadcastTrigger.unregister()
        super.stop()
    }

    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"

    private val client: ActivityRecognitionClient by lazy {
        ActivityRecognition.getClient(context)
    }

    private val activityRecognitionIntent by lazy {
        PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION),
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val broadcastTrigger = SystemBroadcastTrigger(
        context,
        arrayOf(
            ACTION
        )
    ) {
        val result = ActivityRecognitionResult.extractResult(it)
        result?.let {
            val timestamp = System.currentTimeMillis()
            listener?.invoke(
                Entity(
                    timestamp,
                    it.time,
                    confidenceInVehicle = getConfidence(it, DetectedActivity.IN_VEHICLE),
                    confidenceOnBicycle = getConfidence(it, DetectedActivity.ON_BICYCLE),
                    confidenceOnFoot = getConfidence(it, DetectedActivity.ON_FOOT),
                    confidenceRunning = getConfidence(it, DetectedActivity.RUNNING),
                    confidenceStill = getConfidence(it, DetectedActivity.STILL),
                    confidenceTilting = getConfidence(it, DetectedActivity.TILTING),
                    confidenceUnknown = getConfidence(it, DetectedActivity.UNKNOWN),
                    confidenceWalking = getConfidence(it, DetectedActivity.WALKING)
                )
            )
        }
    }
    private fun getConfidence(result: ActivityRecognitionResult, activityType: Int): Int {
        return result.probableActivities
            .find { it.type == activityType }
            ?.confidence ?: 0
    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val confidenceInVehicle: Int,
        val confidenceOnBicycle: Int,
        val confidenceOnFoot: Int,
        val confidenceRunning: Int,
        val confidenceStill: Int,
        val confidenceTilting: Int,
        val confidenceUnknown: Int,
        val confidenceWalking: Int
    ) : DataEntity(received)
}