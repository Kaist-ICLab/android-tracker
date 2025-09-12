package kaist.iclab.wearabletracker.dutycycling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        sleepDurationMs = 180000     // 3 minutes sleep
    )

    // Aggressive duty cycling - battery saving (screen off)
    val AGGRESSIVE = DutyCyclingParams(
        sensingDurationMs = 60000,  // 1 minute sensing
        sleepDurationMs = 300000    // 5 minutes sleep
    )
}

class DutyCyclingManager(
    private val context: Context,
    private val sensorController: BackgroundController
) {
    companion object {
        private const val TAG = "DutyCyclingManager"
    }

    // Current sensing mode state
    private var isCurrentlySensing = false
    private var currentParams: DutyCyclingParams? = null

    // Callbacks for duty cycling events
    private var onSensingStartedCallback: (() -> Unit)? = null
    private var onSensingStoppedCallback: (() -> Unit)? = null
    private var onModeChangedCallback: ((DutyCyclingParams) -> Unit)? = null

    init {
        // Set up service callbacks to be invoked from the service
        DutyCyclingService.sensorController = sensorController
        DutyCyclingService.onSensingStartedCallback = {
            isCurrentlySensing = true
            onSensingStartedCallback?.invoke() 
        }
        DutyCyclingService.onSensingStoppedCallback = {
            isCurrentlySensing = false
            onSensingStoppedCallback?.invoke() 
        }
    }

    // Function to turn on the sensing capabilities of the sensor controller
    fun enableSensing() {
        startLogging()
        isCurrentlySensing = true
        onSensingStartedCallback?.invoke()
    }

    // Function to turn off the sensing capabilities of the sensor controller
    fun disableSensing() {
        stopLogging()
        isCurrentlySensing = false
        onSensingStoppedCallback?.invoke()
    }

    /**
     * Start duty cycling with specified parameters using simple foreground service
     */
    fun startDutyCycling(params: DutyCyclingParams) {
        Log.d(TAG, "Starting duty cycling with params: sensing=${params.sensingDurationMs}ms, sleep=${params.sleepDurationMs}ms")
        
        // Stop any existing / active duty cycling first (but don't send stop command to service)
        if (isCurrentlySensing && currentParams != null) {
            Log.d(TAG, "Stopping existing sensing before starting new mode")
            disableSensing();
            
            // For continuous sensing, add a small delay to ensure proper cleanup before restarting
            // This prevents race conditions with HealthTracker listener management
            if (params.sensingDurationMs == Long.MAX_VALUE) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(1000) // Small delay to ensure HealthTracker cleanup is complete
                    currentParams = params
                    onModeChangedCallback?.invoke(params)
                    
                    try {
                        enableSensing()
                        Log.d(TAG, "Continuous sensing started successfully")
                    } catch (e: SecurityException) {
                        Log.e(TAG, "Permission denied for continuous sensing", e)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting continuous sensing", e)
                    }
                }
                return
            }
        }
        
        currentParams = params
        onModeChangedCallback?.invoke(params)

        // Handle continuous sensing (no cycling) -> Duration time is unlimited
        if (params.sensingDurationMs == Long.MAX_VALUE) {
            try {
                enableSensing()
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied for continuous sensing", e)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting continuous sensing", e)
            }
            return
        }

        // Start duty cycling service using the foreground service and defined parameters
        val intent = Intent(context, DutyCyclingService::class.java).apply {
            action = DutyCyclingService.ACTION_START_DUTY_CYCLING
            putExtra(DutyCyclingService.EXTRA_SENSING_DURATION, params.sensingDurationMs)
            putExtra(DutyCyclingService.EXTRA_SLEEP_DURATION, params.sleepDurationMs)
        }
        context.startForegroundService(intent)
    }

    /**
     * Stop all duty cycling and sensing
     */
    fun stopDutyCycling() {
        // Stop service
        val intent = Intent(context, DutyCyclingService::class.java).apply {
            action = DutyCyclingService.ACTION_STOP_DUTY_CYCLING
        }
        context.startService(intent)
        
        // Stop continuous sensing if active (no cycling)
        if (isCurrentlySensing && currentParams?.sensingDurationMs == Long.MAX_VALUE) {
            disableSensing()
        }
        currentParams = null
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopDutyCycling()
    }

    // Private methods for sensor control
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startLogging() {
        sensorController.start()
    }

    private fun stopLogging() {
        sensorController.stop()
    }
}
