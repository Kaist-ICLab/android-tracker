package kaist.iclab.wearabletracker.dutycycling

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        sleepDurationMs = 30000     // 30 seconds sleep
    )

    // Aggressive duty cycling - battery saving (screen off)
    val AGGRESSIVE = DutyCyclingParams(
        sensingDurationMs = 30000,  // 30 seconds sensing
        sleepDurationMs = 120000    // 2 minutes sleep
    )
}

class DutyCyclingManager(
    private val sensorController: BackgroundController
) {
    companion object {
        private const val TAG = "DutyCyclingManager"
    }

    // Current sensing mode state
    private var currentSensingJob: kotlinx.coroutines.Job? = null
    private var isCurrentlySensing = false
    private var shouldContinueDutyCycling = true

    // Callbacks for duty cycling events
    private var onSensingStartedCallback: (() -> Unit)? = null
    private var onSensingStoppedCallback: (() -> Unit)? = null
    private var onModeChangedCallback: ((DutyCyclingParams) -> Unit)? = null

    /**
     * Start duty cycling with specified parameters
     */
    fun startDutyCycling(params: DutyCyclingParams) {

        // Stop any existing duty cycling
        shouldContinueDutyCycling = false
        currentSensingJob?.cancel()
        
        // Reset flag for new cycle
        shouldContinueDutyCycling = true
        
        // Notify mode change
        onModeChangedCallback?.invoke(params)
        
        // Start new sensing job
        currentSensingJob = CoroutineScope(Dispatchers.IO).launch {
            // Handle continuous sensing (no cycling)
            if (params.sensingDurationMs == Long.MAX_VALUE) {
                Log.d(TAG, "Starting continuous sensing mode")
                try {
                    startLogging()
                    isCurrentlySensing = true
                    onSensingStartedCallback?.invoke()
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission denied for continuous sensing", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting continuous sensing", e)
                }
                return@launch
            }

            // Handle duty cycling with safer transitions
            Log.d(TAG, "Starting duty cycling mode: ${params.sensingDurationMs}ms on, ${params.sleepDurationMs}ms off")
            
            while (shouldContinueDutyCycling) {
                try {
                    Log.d(TAG, "Duty cycle: Starting sensing for ${params.sensingDurationMs}ms")
                    startLogging()
                    isCurrentlySensing = true
                    onSensingStartedCallback?.invoke()
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission denied for duty cycle sensing", e)
                    break // Exit the loop if permission denied
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting sensing in duty cycle", e)
                    break // Exit the loop if there's an error
                }

                kotlinx.coroutines.delay(params.sensingDurationMs)

                // Stop sensing for configured duration  
                Log.d(TAG, "Duty cycle: Stopping sensing for ${params.sleepDurationMs}ms")
                try {
                    stopLogging()
                    isCurrentlySensing = false
                    onSensingStoppedCallback?.invoke()
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping sensing in duty cycle", e)
                    // Continue anyway, don't break the loop
                }

                kotlinx.coroutines.delay(params.sleepDurationMs)
            }
        }
    }

    /**
     * Stop all duty cycling and sensing
     */
    fun stopDutyCycling() {
        shouldContinueDutyCycling = false
        currentSensingJob?.cancel()
        currentSensingJob = null
        
        if (isCurrentlySensing) {
            stopLogging()
            isCurrentlySensing = false
            onSensingStoppedCallback?.invoke()
        }
    }

    /**
     * Check if currently sensing
     */
    fun isCurrentlySensing(): Boolean = isCurrentlySensing

    /**
     * Check if duty cycling is active
     */
    fun isDutyCyclingActive(): Boolean = currentSensingJob?.isActive == true

    /**
     * Set callback for when sensing starts
     */
    fun setOnSensingStartedCallback(callback: () -> Unit) {
        onSensingStartedCallback = callback
    }

    /**
     * Set callback for when sensing stops
     */
    fun setOnSensingStoppedCallback(callback: () -> Unit) {
        onSensingStoppedCallback = callback
    }

    /**
     * Set callback for when duty cycling mode changes
     */
    fun setOnModeChangedCallback(callback: (DutyCyclingParams) -> Unit) {
        onModeChangedCallback = callback
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        shouldContinueDutyCycling = false
        currentSensingJob?.cancel()
        currentSensingJob = null
        
        if (isCurrentlySensing) {
            stopLogging()
            isCurrentlySensing = false
        }
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
