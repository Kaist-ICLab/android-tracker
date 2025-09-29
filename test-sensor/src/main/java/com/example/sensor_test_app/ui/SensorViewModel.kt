package com.example.sensor_test_app.ui

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
) : ViewModel() {
    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    // Cleanup listeners on pause setting
    private val _cleanupListenersOnPause = MutableStateFlow(true)
    val cleanupListenersOnPause: StateFlow<Boolean> = _cleanupListenersOnPause.asStateFlow()

    fun setCleanupListenersOnPause(cleanup: Boolean) {
        _cleanupListenersOnPause.value = cleanup
    }

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
                            Log.d("SensorViewModel", "$permissionMap")
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
        Log.d(SensorViewModel::class.simpleName, "StartLogging()")
        backgroundController.start()
    }

    fun stopLogging() {
        backgroundController.stop()
    }
}