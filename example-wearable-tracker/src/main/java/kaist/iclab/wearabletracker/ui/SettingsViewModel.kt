package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.MyBackgroundController
import kaist.iclab.wearabletracker.data.DeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val sensorController: MyBackgroundController
): ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
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

    fun getDeviceInfo(context: Context, callback: (DeviceInfo) -> Unit) {
        Wearable.getNodeClient(context).localNode
            .addOnSuccessListener { localNode ->
                val deviceInfo = DeviceInfo(
                    name = localNode.displayName,
                    id = localNode.id
                )
                callback(deviceInfo)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting device information from getDeviceInfo()")
            }
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