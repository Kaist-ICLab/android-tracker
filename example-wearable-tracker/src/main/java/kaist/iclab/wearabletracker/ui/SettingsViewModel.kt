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

// Duty cycling parameters for different modes
data class DutyCyclingParams(
    val sensingDurationMs: Long = 0,
    val sleepDurationMs: Long = 0
)

/*
* Predefined duty cycling modes for different scenarios.
* Currently these are for testing purposes only, and can be modified by the user.
*/
object DutyCyclingModes {
    // Continuous sensing - maximum data collection
    val CONTINUOUS = DutyCyclingParams(
        sensingDurationMs = Long.MAX_VALUE,  // Never stop
        sleepDurationMs = 0
    )

    // Smart duty cycling - balanced approach (app minimized)
    val SMART = DutyCyclingParams(
        sensingDurationMs = 60000,  // 1 minute sensing
        sleepDurationMs = 30000     // 30 seconds sleep
    )

    // Aggressive duty cycling - battery saving (screen off)
    val AGGRESSIVE = DutyCyclingParams(
        sensingDurationMs = 30000,  // 30 seconds sensing
        sleepDurationMs = 120000    // 2 minutes sleep
    )
}

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
        syncManager.setOnContinuousSensingCallback {
            startDutyCycling(DutyCyclingModes.CONTINUOUS)
        }
        syncManager.setOnSmartDutyCyclingCallback {
            startDutyCycling(DutyCyclingModes.SMART)
        }
        syncManager.setOnAggressiveDutyCyclingCallback {
            startDutyCycling(DutyCyclingModes.AGGRESSIVE)
        }
        syncManager.setOnStopMonitoringCallback {
            stopLogging()
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

    // Parameterized duty cycling implementation
    private fun startDutyCycling(params: DutyCyclingParams) {
        // Stop any existing duty cycling schedule to make it not interfere with the new one
        stopLogging()

        CoroutineScope(Dispatchers.IO).launch {
            // Handle continuous sensing (no cycling)
            if (params.sensingDurationMs == Long.MAX_VALUE) {
                try {
                    startLogging()
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission denied for continuous sensing", e)
                }
                return@launch
            }

            // Handle duty cycling
            while (true) {
                try {
                    startLogging()
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission denied for duty cycle sensing", e)
                }

                kotlinx.coroutines.delay(params.sensingDurationMs)

                // Stop sensing for configured duration  
                Log.d(TAG, "Duty cycle: Stopping sensing for ${params.sleepDurationMs}ms")
                stopLogging()

                kotlinx.coroutines.delay(params.sleepDurationMs)
            }
        }
    }

    // Method to update duty cycling parameters (Only if needed)
    fun updateDutyCyclingParams(sensingDurationMs: Long, sleepDurationMs: Long) {
        dutyCyclingParams = DutyCyclingParams(sensingDurationMs, sleepDurationMs)
        Log.d(TAG, "Updated duty cycling params: $dutyCyclingParams")
    }
}