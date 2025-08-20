package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val sensorController: BackgroundController
): ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    val sensorDataReceiver by inject<SensorDataReceiver>(clazz = SensorDataReceiver::class.java)

    init {
        Log.v(SensorDataReceiver::class.simpleName, "init()")

        CoroutineScope(Dispatchers.IO).launch {

            sensorController.controllerStateFlow.collect {
                Log.v(SensorDataReceiver::class.simpleName, it.toString())
                if(it.flag == ControllerState.FLAG.RUNNING) sensorDataReceiver.startBackgroundCollection()
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
        if(status) sensor.enable()
        else sensor.disable()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging(){
        sensorController.start()
    }

    fun stopLogging(){
        Log.d(TAG, "stopLogging()")
        sensorController.stop()
    }

    fun upload(){
        Log.d(TAG, "UPLOAD")
    }

    fun flush(){
        Log.d(TAG, "FLUSH")
    }
}