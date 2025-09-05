package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val sensorController: BackgroundController
) : ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    val sensorDataReceiver by inject<SensorDataReceiver>(clazz = SensorDataReceiver::class.java)

    init {
        Log.v(SensorDataReceiver::class.simpleName, "init()")

        CoroutineScope(Dispatchers.IO).launch {

            sensorController.controllerStateFlow.collect {
                Log.v(SensorDataReceiver::class.simpleName, it.toString())
                if (it.flag == ControllerState.FLAG.RUNNING) sensorDataReceiver.startBackgroundCollection()
                else sensorDataReceiver.stopBackgroundCollection()
            }
        }
    }

    val sensorMap = sensorController.sensors.associateBy { it.name }
    val sensorState = sensorController.sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = sensorController.controllerStateFlow

    fun update(sensorName: String, status: Boolean) {
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!
        if (status) sensor.enable()
        else sensor.disable()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging() {
        sensorController.start()
    }

    fun stopLogging() {
        Log.d(TAG, "stopLogging()")
        sensorController.stop()
    }

    fun upload(context: android.content.Context) {
        Log.d(TAG, "UPLOAD")
        getCoordinate(
            context = context,
            onLocationReceived = { lat, lon ->
                Log.d(TAG, "Location received - Latitude: $lat, Longitude: $lon")
            },
            onError = { error ->
                Log.e(TAG, "Location error: $error")
            }
        )
    }

    fun flush() {
        Log.d(TAG, "FLUSH")
    }

    fun getCoordinate(
        context: android.content.Context,
        onLocationReceived: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            onError("Location permission not granted")
            return
        }

        try {
            val locationManager =
                context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager

            // 1. getLastKnownLocation using LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                Log.d("pradipta", "getLastKnownLocation - latitude : ${location.latitude}")
                Log.d("pradipta", "getLastKnownLocation - longitude : ${location.longitude}")
                onLocationReceived(
                    String.format("%.6f", location.latitude),
                    String.format("%.6f", location.longitude)
                )
            }

            // 2. getCurrentLocation using FusedLocationProviderClient
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(100, createCancellationToken())
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("pradipta", "getCurrentLocation - latitude : ${location.latitude}")
                        Log.d("pradipta", "getCurrentLocation - longitude : ${location.longitude}")
                        onLocationReceived(
                            String.format("%.6f", location.latitude),
                            String.format("%.6f", location.longitude)
                        )
                    }
                }
                .addOnFailureListener {
                    Log.d("pradipta", "실패 : ${it.message}")
                    onError("Failed to get current location: ${it.message}")
                }

        } catch (e: Exception) {
            Log.e("pradipta", "Error getting location", e)
            onError("Exception: ${e.message}")
        }
    }

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