//package kaist.iclab.tracker.collectors
//
//import android.Manifest
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.pm.ServiceInfo
//import android.location.LocationManager
//import android.os.Build
//import android.util.Log
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
//import java.util.concurrent.TimeUnit
//
//class LocationCollector(
//    override val context: Context
//) : AbstractCollector(context) {
//
//    val ACTION = "android.intent.action.LOCATION_CHANGED"
//
//    var config: Config = Config(
//        TimeUnit.MINUTES.toMillis(3),
//        0,
//        TimeUnit.MINUTES.toMillis(10),
//        0.0f,
//        0,
//        Priority.PRIORITY_HIGH_ACCURACY
//    )
//
//    data class DataEntity(
//        val timestamp: Long,
//        val latitude: Double,
//        val longitude: Double,
//        val altitude: Double,
//        val speed: Float,
//        val accuracy: Float
//    ) : AbstractCollector.DataEntity()
//
//
//    data class Config(
//        val interval: Long,
//        val maxUpdateAge: Long,
//        val maxUpdateDelay: Long,
//        val minUpdateDistance: Float,
//        val minUpdateInterval: Long,
//        val priority: Int
//    ) : AbstractCollector.Config()
//
//
//    override val permissions = listOfNotNull(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else null
//    ).toTypedArray()
//
//    lateinit var trigger: SystemBroadcastTrigger
//
//    // Check whether there is at least one location provider
//    override fun isAvailable(): Boolean {
//        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val pm = context.packageManager
//
//        // Check if the device has GPS hardware
//        val hasGpsHardware = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
//
//        // Check if any location provider is enabled (GPS or Network)
//        val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//        // Return true if the device has GPS hardware and location providers are enabled
//        return hasGpsHardware && locationEnabled
//    }
//
//    private val client: FusedLocationProviderClient by lazy {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//
//    private val intent: PendingIntent by lazy {
//        PendingIntent.getBroadcast(
//            context,
//            0xFF,
//            Intent(ACTION),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
//
//    override fun start() {
//        trigger = SystemBroadcastTrigger(
//            context,
//            arrayOf(ACTION)
//        ) { intent ->
//            if (ACTION != intent.action) {
//                Log.e(TAG, "Invalid action: ${intent.action}")
//            }
//            val location = LocationResult.extractResult(intent)?.lastLocation
//                ?: return@SystemBroadcastTrigger
//            listener?.invoke(
//                DataEntity(
//                    location.time,
//                    location.longitude,
//                    location.latitude,
//                    location.altitude,
//                    location.speed,
//                    location.accuracy,
//                )
//            )
//        }
//        trigger.register()
//
//        val request = LocationRequest.Builder(config.interval)
//            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
//            .setMinUpdateDistanceMeters(config.minUpdateDistance)
//            .setMaxUpdateAgeMillis(config.maxUpdateAge)
//            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            .build()
//        client.requestLocationUpdates(request, intent)
//    }
//
//    override fun stop() {
//        trigger.unregister()
//        client.removeLocationUpdates(intent)
//    }
//}