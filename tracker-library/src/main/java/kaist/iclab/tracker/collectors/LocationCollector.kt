package kaist.iclab.tracker.collectors

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.filters.applyFilters
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
import java.util.concurrent.TimeUnit

class LocationCollector(
    override val context: Context,
    override val database: DatabaseInterface
) : AbstractCollector(
    context, database
) {
    companion object {
        const val NAME= "LOCATION"
        val action= "android.intent.action.LOCATION_CHANGED"
    }

    override val NAME: String
        get() = Companion.NAME

    override val permissions = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).toTypedArray()

    lateinit var trigger: SystemBroadcastTrigger

    // Check whether there is at least one location provider
    override fun isAvailable(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val pm = context.packageManager

        // Check if the device has GPS hardware
        val hasGpsHardware = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

        // Check if any location provider is enabled (GPS or Network)
        val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // Return true if the device has GPS hardware and location providers are enabled
        return hasGpsHardware && locationEnabled
    }


    fun listener(intent: Intent): Map<String, Any> {
        if (action != intent.action) {
            Log.e(TAG, "Invalid action: ${intent.action}")
            return emptyMap()
        }
        val location = LocationResult.extractResult(intent)?.lastLocation ?: return emptyMap()
        val timestamp = System.currentTimeMillis()

        return mapOf(
            "timestamp" to timestamp,
            "time" to location.time,
            "longitude" to location.longitude,
            "latitude" to location.latitude,
            "altitude" to location.altitude,
            "speed" to location.speed,
            "accuracy" to location.accuracy,
        )
    }

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val intent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0xFF,
            Intent(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun start() {
        trigger = SystemBroadcastTrigger(
            context,
            arrayOf(action)
        ) {
            database.insert(NAME, listener(it).applyFilters(filters))
        }
        trigger.register()

        val request = LocationRequest.Builder(TimeUnit.MINUTES.toMillis(3))
            .setMinUpdateDistanceMeters(5F)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        client.requestLocationUpdates(request, intent)
    }

    override fun stop() {
        trigger.unregister()
        client.removeLocationUpdates(intent)
    }
}