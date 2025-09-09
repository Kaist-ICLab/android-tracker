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
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kaist.iclab.wearabletracker.sync.WearableSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

// Simple duty cycling parameters
data class DutyCyclingParams(
    val sensingDurationMs: Long = 30000,  // 30 seconds
    val sleepDurationMs: Long = 30000     // 30 seconds
)

class SettingsViewModel(
    private val sensorController: BackgroundController
) : ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    val sensorDataReceiver by inject<SensorDataReceiver>(clazz = SensorDataReceiver::class.java)
    private val syncManager by inject<WearableSyncManager>(clazz = WearableSyncManager::class.java)
    
    // Duty cycling parameters (customizable)
    private var dutyCyclingParams = DutyCyclingParams()

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
        // Continuous sensing callback
        syncManager.setOnContinuousSensingCallback {
            Log.d(TAG, "Starting continuous sensing from phone command")
            try {
                startLogging()
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied for continuous sensing", e)
            }
        }
        
        // Duty cycling callback
        syncManager.setOnDutyCyclingCallback {
            Log.d(TAG, "Starting duty cycling from phone command")
            try {
                startDutyCycling()
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied for duty cycling", e)
            }
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
    
    // Simple duty cycling implementation
    private fun startDutyCycling() {
        Log.d(TAG, "Starting duty cycling mode with params: $dutyCyclingParams")
        
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                // Start sensing for configured duration
                Log.d(TAG, "Duty cycle: Starting sensing for ${dutyCyclingParams.sensingDurationMs}ms")
                try {
                    startLogging()
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission denied for duty cycle sensing", e)
                }
                
                kotlinx.coroutines.delay(dutyCyclingParams.sensingDurationMs)
                
                // Stop sensing for configured duration  
                Log.d(TAG, "Duty cycle: Stopping sensing for ${dutyCyclingParams.sleepDurationMs}ms")
                stopLogging()
                
                kotlinx.coroutines.delay(dutyCyclingParams.sleepDurationMs)
            }
        }
    }
    
    // Method to update duty cycling parameters
    fun updateDutyCyclingParams(sensingDurationMs: Long, sleepDurationMs: Long) {
        dutyCyclingParams = DutyCyclingParams(sensingDurationMs, sleepDurationMs)
        Log.d(TAG, "Updated duty cycling params: $dutyCyclingParams")
    }
}