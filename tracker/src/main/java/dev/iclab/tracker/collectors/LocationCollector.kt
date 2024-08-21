package dev.iclab.tracker.collectors

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.filters.applyFilters
import dev.iclab.tracker.triggers.SystemBroadcastTrigger
import java.util.concurrent.TimeUnit

class LocationCollector(
    override val context: Context,
    override val database: DatabaseInterface
) : AbstractCollector(
    context, database
) {
    companion object {
        const val TAG = "LocationCollector"
        const val event = "location"
        val action=
            "android.intent.action.LOCATION_CHANGED"

    }

    override val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var trigger: SystemBroadcastTrigger

    // This collector might be supported for all android smartphone
    override fun isAvailable(): Boolean = true

    // Collector requires permissions, but they should requested on Activity
    override suspend fun enable(){}

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
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun start() {
        trigger = SystemBroadcastTrigger(
            context,
            arrayOf(action)
        ) {
            database.insert(event, listener(it).applyFilters(filters))
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

    override fun flush() {
        TODO("Not yet implemented")
    }
}