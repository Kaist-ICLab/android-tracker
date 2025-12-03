package kaist.iclab.mobiletracker.viewmodels.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.services.PhoneSensorDataService
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            backgroundController.controllerStateFlow
                .catch { e ->
                    Log.e(TAG, "Error observing controller state: ${e.message}", e)
                }
                .collect { state ->
                    try {
                        if (state.flag == ControllerState.FLAG.RUNNING) {
                            PhoneSensorDataService.start(context)
                        } else {
                            PhoneSensorDataService.stop(context)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting/stopping phone sensor service: ${e.message}", e)
                    }
                }
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
                var collectionJob: Job? = null
                collectionJob = viewModelScope.launch {
                    permissionManager.getPermissionFlow(sensor.permissions)
                        .catch { e ->
                            Log.e(TAG, "Error observing permission flow for $sensorName: ${e.message}", e)
                            collectionJob?.cancel()
                        }
                        .collect { permissionMap ->
                            try {
                                if (permissionMap.values.all { it == PermissionState.GRANTED }) {
                                    sensor.enable()
                                    collectionJob?.cancel()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error enabling sensor $sensorName: ${e.message}", e)
                                collectionJob?.cancel()
                            }
                        }
                }
            }

            SensorState.FLAG.ENABLED -> {
                try {
                    sensor.disable()
                } catch (e: Exception) {
                    Log.e(TAG, "Error disabling sensor $sensorName: ${e.message}", e)
                }
            }
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
        try {
            backgroundController.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting logging: ${e.message}", e)
        }
    }

    fun stopLogging() {
        try {
            backgroundController.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping logging: ${e.message}", e)
        }
    }
}

