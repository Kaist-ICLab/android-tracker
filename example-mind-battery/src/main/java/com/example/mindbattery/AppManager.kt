package com.example.mindbattery

import android.content.Context
import android.content.Intent
import android.util.Log
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppManager(
    private val context: Context,
    private val screenSensor: ScreenSensor  // Inject ScreenSensor
) {

    // Callback to send commands to watch
    private var sendCommandCallback: ((String) -> Unit)? = null

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
    
    // Track app foreground state separately from screen state
    private var isAppInForeground = true
    private var isScreenOn = true

    // In-memory log storage
    private val logEntries = mutableListOf<String>()

    init {
        setupScreenSensorListener()
        updateDutyState(DutyState.APP_OPENED)
    }

    private fun setupScreenSensorListener() {

        // Add listener to ScreenSensor
        screenSensor.addListener { entity ->
            when (entity.type) {
                Intent.ACTION_SCREEN_ON -> {
                    Log.d(TAG, "Screen Sensor: Screen turned ON via ScreenSensor")
                    isScreenOn = true
                    // When screen turns on, determine state based on app foreground status
                    val newState = if (isAppInForeground) {
                        DutyState.APP_OPENED
                    } else {
                        DutyState.APP_MINIMIZED
                    }
                    updateDutyState(newState)
                }

                Intent.ACTION_SCREEN_OFF -> {
                    Log.d(TAG, "Screen Sensor: Screen turned OFF via ScreenSensor")
                    isScreenOn = false
                    updateDutyState(DutyState.SCREEN_OFF)
                }

                Intent.ACTION_USER_PRESENT -> {
                    Log.d(TAG, "Screen Sensor: User present via ScreenSensor")
                    // User present means they're actively using the device
                    isAppInForeground = true
                    isScreenOn = true
                    updateDutyState(DutyState.APP_OPENED)
                }
            }
        }
    }

    fun start() {
        // For now, it just start the screen sensor
        screenSensor.start()
    }

    fun stop() {
        // For now, it just stops the screen sensor
        screenSensor.stop()
    }

    // Method to be called when app goes to background
    fun onAppMinimized() {
        isAppInForeground = false
        // Only update state if screen is on (if screen is off, keep SCREEN_OFF state)
        if (isScreenOn) {
            updateDutyState(DutyState.APP_MINIMIZED)
        }
    }

    // Method to be called when app comes to foreground
    fun onAppOpened() {
        isAppInForeground = true
        // Only update state if screen is on (if screen is off, keep SCREEN_OFF state)
        if (isScreenOn) {
            updateDutyState(DutyState.APP_OPENED)
        }
    }

    private fun updateDutyState(newState: DutyState) {
        if (_dutyStateFlow.value != newState) {
            val oldState = _dutyStateFlow.value
            _dutyStateFlow.value = newState
            _lastStateChangeFlow.value = System.currentTimeMillis()

            Log.d(TAG, "Duty state changed from $oldState to $newState")

            // Send command to watch based on new state
            sendCommandToWatch(newState)

            // Log state change. TODO: Remove this function
            logStateChange(oldState, newState)
        }

    }

    private fun sendCommandToWatch(state: DutyState) {
        try {
            val command = when (state) {
                DutyState.APP_OPENED -> "START_CONTINUOUS"
                DutyState.APP_MINIMIZED -> "START_SMART_DUTY_CYCLING"  // Balanced approach
                DutyState.SCREEN_OFF -> "START_AGGRESSIVE_DUTY_CYCLING"  // Battery saving
            }

            // Send command using callback
            sendCommandCallback?.invoke(command)
            Log.d(TAG, "Sent command to watch: $command for state: $state")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending command to watch", e)
        }
    }

    // Method to send stop command to watch
    fun sendStopCommandToWatch() {
        try {
            val command = "STOP_MONITORING"
            sendCommandCallback?.invoke(command)
            Log.d(TAG, "Sent stop command to watch: $command")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending stop command to watch", e)
        }
    }

    private fun logStateChange(oldState: DutyState, newState: DutyState) {
        try {
            val timestamp =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "[$timestamp] Duty state changed: $oldState -> $newState"

            // Store log entry in memory
            logEntries.add(logEntry)

            // Keep only last 100 entries to prevent memory issues
            if (logEntries.size > 10) {
                logEntries.removeAt(0)
            }

            Log.d(TAG, "Log entry added: $logEntry")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add log entry", e)
        }
    }

    // Method to get all log entries for display
    fun getLogEntries(): List<String> = logEntries.toList()

    // Method to get formatted logs for display. TODO: Remove this function
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

    // Set callback for sending commands to watch
    fun setSendCommandCallback(callback: (String) -> Unit) {
        sendCommandCallback = callback
    }
}
