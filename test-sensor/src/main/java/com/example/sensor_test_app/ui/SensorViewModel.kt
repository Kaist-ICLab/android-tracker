package com.example.sensor_test_app.ui

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.flow.combine

class SensorViewModel(
    private val backgroundController: BackgroundController
): ViewModel() {
    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    fun update(sensorName: String, status: Boolean) {
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!
        if(status) sensor.enable()
        else sensor.disable()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging(){
        backgroundController.start()
    }

    fun stopLogging(){
        backgroundController.stop()
    }
}