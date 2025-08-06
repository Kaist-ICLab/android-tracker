package com.example.sensor_test_app.ui

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SensorViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
): ViewModel() {
    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    fun toggleSensor(sensorName: String) {
        val status = sensorState[sensorName]!!.value.flag
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!

        when(status) {
            SensorState.FLAG.DISABLED -> {
                permissionManager.request(sensor.permissions)
                CoroutineScope(Dispatchers.IO).launch {
                    permissionManager.getPermissionFlow(sensor.permissions).collect { permissionMap ->
                        Log.d("SensorViewModel", "$permissionMap")
                        if(permissionMap.values.all { it == PermissionState.GRANTED }) {
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
    fun startLogging(){
        Log.d(SensorViewModel::class.simpleName, "StartLogging()")
        backgroundController.start()
    }

    fun stopLogging(){
        backgroundController.stop()
    }
}