package kaist.iclab.mobiletracker.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
    private val context: Context
) : ViewModel() {
    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    // Sensor listeners management
    private var listenersAdded = false
    private val sensorListeners: List<(SensorEntity) -> Unit> = sensors.map { sensor ->
        { data: SensorEntity ->
            // Sensor data received - listener active
        }
    }

    fun toggleSensor(sensorName: String) {
        val sensorStateFlow = sensorState[sensorName] ?: run {
            Log.e(TAG, "Sensor not found: $sensorName")
            return
        }
        val sensor = sensorMap[sensorName] ?: run {
            Log.e(TAG, "Sensor map entry not found: $sensorName")
            return
        }
        
        val status = sensorStateFlow.value.flag

        when (status) {
            SensorState.FLAG.DISABLED -> {
                permissionManager.request(sensor.permissions)
                viewModelScope.launch {
                    permissionManager.getPermissionFlow(sensor.permissions)
                        .collect { permissionMap ->
                            if (permissionMap.values.all { it == PermissionState.GRANTED }) {
                                sensor.enable()
                                this.cancel()
                            }
                        }
                }
            }

            SensorState.FLAG.ENABLED -> sensor.disable()
            else -> Unit
        }
    }

    fun hasNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging() {
        backgroundController.start()
    }

    fun stopLogging() {
        backgroundController.stop()
    }

    /**
     * Setup sensor listeners for logging sensor data
     * Should be called when activity resumes
     */
    fun setupSensorListeners() {
        if (listenersAdded) return
        for (sensorIdx in sensors.indices) {
            val currentSensor = sensors[sensorIdx]
            currentSensor.addListener(sensorListeners[sensorIdx])
        }
        listenersAdded = true
    }

    /**
     * Cleanup sensor listeners
     * Should be called when activity pauses or destroys
     */
    fun cleanupSensorListeners() {
        if (!listenersAdded) return
        for (sensorIdx in sensors.indices) {
            sensors[sensorIdx].removeListener(sensorListeners[sensorIdx])
        }
        listenersAdded = false
    }
}

