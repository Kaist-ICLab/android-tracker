package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.sensor.controller.BackgroundController

class SettingsViewModel(
    private val sensorController: BackgroundController,
): ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    private val sensorMap = sensorController.sensors.associateBy { it.name }
    val sensorState = sensorController.sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = sensorController.controllerStateFlow

    fun update(sensorName: String, status: Boolean) {
        val sensor = sensorMap[sensorName]!!
        if(status) sensor.enable()
        else sensor.disable()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging(){
        sensorController.start()
    }

    fun stopLogging(){
        sensorController.stop()
    }

    fun upload(){
        Log.d(TAG, "UPLOAD")
    }

    fun flush(){
        Log.d(TAG, "FLUSH")
    }
}