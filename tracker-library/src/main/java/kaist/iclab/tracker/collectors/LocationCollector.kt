package kaist.iclab.tracker.collectors

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LocationCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<LocationCollector.Config, LocationCollector.Entity>(permissionManager) {

    override val permissions = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else null
    ).toTypedArray()

    data class Config(
        val interval: Long,
        val maxUpdateAge: Long,
        val maxUpdateDelay: Long,
        val minUpdateDistance: Float,
        val minUpdateInterval: Long,
        val priority: Int
    ) : CollectorConfig()

    override val defaultConfig: Config = Config(
        TimeUnit.SECONDS.toMillis(15),
        0,
        0,
        0.0f,
        0,
        Priority.PRIORITY_HIGH_ACCURACY
    )

    override fun start() {
        Log.d(TAG, "Start location collection")
        super.start()


        val request = LocationRequest.Builder(configFlow.value.interval)
            .setMaxUpdateDelayMillis(configFlow.value.maxUpdateDelay)
            .setMinUpdateDistanceMeters(configFlow.value.minUpdateDistance)
            .setMaxUpdateAgeMillis(configFlow.value.maxUpdateAge)
            .setMaxUpdateDelayMillis(configFlow.value.maxUpdateDelay)
            .setPriority(configFlow.value.priority)
            .build()
//        trigger.register()
//        client.requestLocationUpdates(request, intent)
        client.requestLocationUpdates(request, Executors.newSingleThreadExecutor(),locationListener)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            Log.d(TAG, "Received location update")
            listener?.invoke(
                Entity(
                    System.currentTimeMillis(),
                    p0.time,
                    p0.latitude,
                    p0.longitude,
                    p0.altitude,
                    p0.speed,
                    p0.accuracy,
                )
            )
        }
    }

    override fun stop() {
//        trigger.unregister()
        client.removeLocationUpdates(locationListener)
//        client.removeLocationUpdates(intent)
        super.stop()
    }

    // Check whether there is at least one location provider
    override fun isAvailable(): Availability {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val pm = context.packageManager

        // Check if the device has GPS hardware
        val hasGpsHardware = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        if(!hasGpsHardware) return Availability(false, "No GPS hardware")
        // Check if any location provider is enabled (GPS or Network)
        val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(!locationEnabled) return Availability(false, "Location providers are disabled")

        // Return true if the device has GPS hardware and location providers are enabled
        return Availability(true)
    }

//    private val ACTION = "kaist.iclab.tracker.LOCATION_CHANGED"

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

//    private val intent: PendingIntent by lazy {
//        PendingIntent.getBroadcast(
//            context,
//            0,
//            Intent(ACTION),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }

//    val trigger= SystemBroadcastTrigger(
//        context,
//        arrayOf(ACTION)
//    ) { intent ->
//        Log.d(TAG, "Received location update")
//        if (ACTION != intent.action) {
//            Log.e(TAG, "Invalid action: ${intent.action}")
//        }
//        val location = LocationResult.extractResult(intent)?.lastLocation
//            ?: return@SystemBroadcastTrigger
//        listener?.invoke(
//            Entity(
//                System.currentTimeMillis(),
//                location.time,
//                location.longitude,
//                location.latitude,
//                location.altitude,
//                location.speed,
//                location.accuracy,
//            )
//        )
//    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val speed: Float,
        val accuracy: Float
    ) : DataEntity(received)
}