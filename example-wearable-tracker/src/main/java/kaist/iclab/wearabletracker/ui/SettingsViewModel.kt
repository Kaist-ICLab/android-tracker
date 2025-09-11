package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.data.DeviceInfo
import kaist.iclab.wearabletracker.dutycycling.DutyCyclingManager
import kaist.iclab.wearabletracker.dutycycling.DutyCyclingModes
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kaist.iclab.wearabletracker.sync.WearableSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val context: Context,
    private val sensorController: BackgroundController
) : ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    val sensorDataReceiver by inject<SensorDataReceiver>(clazz = SensorDataReceiver::class.java)
    private val syncManager by inject<WearableSyncManager>(clazz = WearableSyncManager::class.java)
    private val dutyCyclingManager = DutyCyclingManager(context, sensorController)

    init {
        Log.v(SensorDataReceiver::class.simpleName, "init()")

        CoroutineScope(Dispatchers.IO).launch {

            sensorController.controllerStateFlow.collect {
                Log.v(SensorDataReceiver::class.simpleName, it.toString())
                if (it.flag == ControllerState.FLAG.RUNNING) sensorDataReceiver.startBackgroundCollection()
                else sensorDataReceiver.stopBackgroundCollection()
            }
        }

        // Set up duty cycling callbacks
        setupDutyCyclingCallbacks()
    }

    private fun setupDutyCyclingCallbacks() {
        syncManager.setOnContinuousSensingCallback {
            dutyCyclingManager.startDutyCycling(DutyCyclingModes.CONTINUOUS)
        }
        syncManager.setOnSmartDutyCyclingCallback {
            dutyCyclingManager.startDutyCycling(DutyCyclingModes.SMART)
        }
        syncManager.setOnAggressiveDutyCyclingCallback {
            dutyCyclingManager.startDutyCycling(DutyCyclingModes.AGGRESSIVE)
        }
        syncManager.setOnStopMonitoringCallback {
            Log.d(TAG, "Stopping monitoring from phone command")
            dutyCyclingManager.stopDutyCycling()
        }
    }

    val sensorMap = sensorController.sensors.associateBy { it.name }
    val sensorState = sensorController.sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = sensorController.controllerStateFlow

    fun update(sensorName: String, status: Boolean) {
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!
        if (status) sensor.enable()
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
            .addOnFailureListener { _ ->
                Log.e(TAG, "Error getting device information from getDeviceInfo()")
            }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging() {
        sensorController.start()
    }

    fun stopLogging() {
        Log.d(TAG, "stopLogging()")
        sensorController.stop()
    }

    fun upload() {
        Log.d(TAG, "UPLOAD")
    }

    fun flush() {
        Log.d(TAG, "FLUSH")
    }
    
    // Cleanup when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel being cleared, stopping all sensing")
        dutyCyclingManager.cleanup()
    }
}