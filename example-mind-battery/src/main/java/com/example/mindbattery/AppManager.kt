package com.example.mindbattery

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kaist.iclab.tracker.sensor.phone.ScreenSensor

class AppManager(
    private val context: Context,
    private val screenSensor: ScreenSensor  // Inject ScreenSensor
) {
    
    companion object {
        private const val TAG = "MindBatteryDummyApp"
    }
    
    // Duty cycling states
    enum class DutyState {
        APP_OPENED,      // App is opened and visible
        APP_MINIMIZED,   // App is minimized but screen is on
        SCREEN_OFF       // Screen is off (no matter app state)
    }
    
    // State flows
    private val _dutyStateFlow = MutableStateFlow(DutyState.APP_OPENED)
    val dutyStateFlow: StateFlow<DutyState> = _dutyStateFlow
    
    private val _lastStateChangeFlow = MutableStateFlow(System.currentTimeMillis())
    val lastStateChangeFlow: StateFlow<Long> = _lastStateChangeFlow
    
    // In-memory log storage
    private val logEntries = mutableListOf<String>()
    
    init {
        updateDutyState(DutyState.APP_OPENED)
        setupScreenSensorListener()
    }
    
    private fun setupScreenSensorListener() {
        try {
            // Add listener to ScreenSensor
            screenSensor.addListener { entity ->
                when (entity.type) {
                    Intent.ACTION_SCREEN_ON -> {
                        Log.d(TAG, "Screen turned ON via ScreenSensor")
                        updateDutyState(DutyState.APP_OPENED)
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        Log.d(TAG, "Screen turned OFF via ScreenSensor")
                        updateDutyState(DutyState.SCREEN_OFF)
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        Log.d(TAG, "User present via ScreenSensor")
                        updateDutyState(DutyState.APP_OPENED)
                    }
                }
            }
            
            Log.d(TAG, "ScreenSensor listener setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ScreenSensor listener", e)
        }
    }
    
    fun start() {
        try {
            Log.d(TAG, "onStart() function called")
            // Start the ScreenSensor
            screenSensor.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStart()", e)
        }
    }
    
    fun stop() {
        try {
            Log.d(TAG, "onStop() function called")
            // Stop the ScreenSensor
            screenSensor.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStop()", e)
        }
    }
    
    // Method to be called when app goes to background
    fun onAppMinimized() {
        try {
            updateDutyState(DutyState.APP_MINIMIZED)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onAppMinimized()", e)
        }
    }
    
    // Method to be called when app comes to foreground
    fun onAppOpened() {
        try {
            updateDutyState(DutyState.APP_OPENED)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onAppOpened()", e)
        }
    }
    
    private fun updateDutyState(newState: DutyState) {
        try {
            if (_dutyStateFlow.value != newState) {
                val oldState = _dutyStateFlow.value
                _dutyStateFlow.value = newState
                _lastStateChangeFlow.value = System.currentTimeMillis()
                
                Log.d(TAG, "Duty state changed from $oldState to $newState")
                
                // Log state change
                logStateChange(oldState, newState)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating duty state", e)
        }
    }
    
    private fun logStateChange(oldState: DutyState, newState: DutyState) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "[$timestamp] Duty state changed: $oldState -> $newState"
            
            // Store log entry in memory
            logEntries.add(logEntry)
            
            // Keep only last 100 entries to prevent memory issues
            if (logEntries.size > 100) {
                logEntries.removeAt(0)
            }
            
            Log.d(TAG, "Log entry added: $logEntry")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add log entry", e)
        }
    }
    
    // Method to get all log entries for display
    fun getLogEntries(): List<String> = logEntries.toList()
    
    // Method to get formatted logs for display
    fun getFormattedLogs(): String {
        return if (logEntries.isEmpty()) {
            "No logs available yet.\n\nTry changing the app state (open/minimize) or turning the screen on/off to generate logs."
        } else {
            buildString {
                appendLine("=== Duty Cycling Logs ===")
                appendLine("Total entries: ${logEntries.size}")
                appendLine()
                logEntries.forEach { entry ->
                    appendLine(entry)
                }
            }
        }
    }
    
    fun getCurrentState(): DutyState = _dutyStateFlow.value
    
    fun getLastStateChange(): Long = _lastStateChangeFlow.value
}
