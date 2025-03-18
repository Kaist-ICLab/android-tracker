package kaist.iclab.tracker.sensor.phone//package kaist.iclab.tracker.collector.phone

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LocationSensor(
    private val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
) : BaseSensor<LocationSensor.Config, LocationSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long,
        val maxUpdateAge: Long,
        val maxUpdateDelay: Long,
        val minUpdateDistance: Float,
        val minUpdateInterval: Long,
        val priority: Int
    ) : SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val speed: Float,
        val accuracy: Float
    ) : SensorEntity

    override val permissions = listOfNotNull(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else null
    ).toTypedArray()

    private val locationListener = LocationListener { p0 ->
        listeners.forEach { listener ->
            listener.invoke(
                Entity(
                    System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS.toNanos(p0.time),
                    p0.latitude,
                    p0.longitude,
                    p0.altitude,
                    p0.speed,
                    p0.accuracy,
                )
            )
        }
    }

    override fun init() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val pm = context.packageManager

        // Check if the device has GPS hardware
        val hasGpsHardware = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        if(!hasGpsHardware) {
            stateStorage.set(SensorState(SensorState.FLAG.UNAVAILABLE, "No GPS hardware"))
            return
        }

        // Check if any location provider is enabled (GPS or Network)
        val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(!locationEnabled){
            stateStorage.set(SensorState(SensorState.FLAG.UNAVAILABLE, "Location providers are disabled"))
            return
        }

        stateStorage.set(SensorState(SensorState.FLAG.DISABLED, ""))
    }

    override fun onStart() {
        val config = configStateFlow.value
        val request = LocationRequest.Builder(config.interval)
            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
            .setMinUpdateDistanceMeters(config.minUpdateDistance)
            .setMaxUpdateAgeMillis(config.maxUpdateAge)
            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
            .setPriority(config.priority)
            .build()
        try {
            client.requestLocationUpdates(request, Executors.newSingleThreadExecutor(),locationListener)
        } catch (e: SecurityException) {
            throw e
        }
    }

    override fun onStop() {
        client.removeLocationUpdates(locationListener)
    }

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
}