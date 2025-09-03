package kaist.iclab.tracker.sensor.phone

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
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.Executors

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
        val priority: Int,
        val waitForAccurateLocation: Boolean,
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
                    received = System.currentTimeMillis(),
                    timestamp = p0.time,
                    latitude = p0.latitude,
                    longitude = p0.longitude,
                    altitude = p0.altitude,
                    speed = p0.speed,
                    accuracy = p0.accuracy,
                )
            )
        }
    }

    override fun init() {
        super.init()

        try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val pm = context.packageManager

            // Check if the device has GPS hardware
            val hasGpsHardware = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
            if (!hasGpsHardware) {
                stateStorage.set(
                    SensorState(
                        SensorState.FLAG.UNAVAILABLE,
                        "No GPS hardware available"
                    )
                )
                return
            }

            // Check if any location provider is enabled (GPS or Network)
            val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!locationEnabled) {
                stateStorage.set(
                    SensorState(
                        SensorState.FLAG.UNAVAILABLE,
                        "Location providers are disabled"
                    )
                )
                return
            }

            // Check if Google Play Services are available
            if (!isGooglePlayServicesAvailable()) {
                stateStorage.set(
                    SensorState(
                        SensorState.FLAG.UNAVAILABLE,
                        "Google Play Services not available"
                    )
                )
                return
            }

            // If all checks pass, set as enabled
            stateStorage.set(SensorState(SensorState.FLAG.ENABLED))

        } catch (e: Exception) {
            stateStorage.set(
                SensorState(
                    SensorState.FLAG.UNAVAILABLE,
                    "Error initializing location sensor: ${e.message}"
                )
            )
        }
    }

    override fun onStart() {
        val config = configStateFlow.value
        val request = LocationRequest.Builder(config.interval)
            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
            .setMinUpdateDistanceMeters(config.minUpdateDistance)
            .setMaxUpdateAgeMillis(config.maxUpdateAge)
            .setMaxUpdateDelayMillis(config.maxUpdateDelay)
            .setPriority(config.priority)
            .setWaitForAccurateLocation(config.waitForAccurateLocation)
            .build()
        try {
            client.requestLocationUpdates(
                request,
                Executors.newSingleThreadExecutor(),
                locationListener
            )
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

    /**
     * FusedLocationProviderClient API requires Google Play Services installed.
     * Thus we need to check the availability before getting the location
     */
    private fun isGooglePlayServicesAvailable(): Boolean {
        return try {
            val googleApiAvailability =
                com.google.android.gms.common.GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Developers writing methods that return a Task should take a CancellationToken as a parameter if they wish to make the Task cancelable
     */
    private fun createCancellationToken(): CancellationToken {
        return object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }

            override fun isCancellationRequested(): Boolean {
                return false
            }
        }
    }
}
