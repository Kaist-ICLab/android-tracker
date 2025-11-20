package kaist.iclab.mobiletracker.viewmodels

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.phone.DeviceModeSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
) : ViewModel() {
    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    // DeviceModeSensor specific properties
    private val deviceModeSensor = sensors.find { it is DeviceModeSensor } as? DeviceModeSensor

    fun toggleSensor(sensorName: String) {
        val status = sensorState[sensorName]!!.value.flag
        Log.d(sensorName, "Previous Status: ${status.toString()}")
        val sensor = sensorMap[sensorName]!!

        when (status) {
            SensorState.FLAG.DISABLED -> {
                permissionManager.request(sensor.permissions)
                CoroutineScope(Dispatchers.IO).launch {
                    permissionManager.getPermissionFlow(sensor.permissions)
                        .collect { permissionMap ->
                            Log.d("SettingsViewModel", "$permissionMap")
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging() {
        Log.d(SettingsViewModel::class.simpleName, "StartLogging()")
        backgroundController.start()
    }

    fun stopLogging() {
        backgroundController.stop()
    }
}

