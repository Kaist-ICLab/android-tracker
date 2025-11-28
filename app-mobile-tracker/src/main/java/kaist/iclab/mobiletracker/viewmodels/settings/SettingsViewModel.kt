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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
    private val context: Context
) : ViewModel(), KoinComponent {
    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val sensors = backgroundController.sensors
    val phoneSensorDataService: PhoneSensorDataService by inject()

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    init {
        viewModelScope.launch(Dispatchers.IO) {
            backgroundController.controllerStateFlow.collect {
                if (it.flag == ControllerState.FLAG.RUNNING) {
                    phoneSensorDataService.startBackgroundCollection()
                } else {
                    phoneSensorDataService.stopBackgroundCollection()
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
}

