package com.example.mindbattery

import android.content.Context
import android.content.Intent
import android.util.Log
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppManager(
    private val context: Context,
    private val screenSensor: ScreenSensor
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

    // Track app foreground state separately from screen state
    private var isAppInForeground = true
    private var isScreenOn = true

    init {
        setupScreenSensorListener()
        updateDutyState(DutyState.APP_OPENED)
    }

    private fun setupScreenSensorListener() {

        // Add listener to ScreenSensor
        screenSensor.addListener { entity ->
            when (entity.type) {
                Intent.ACTION_SCREEN_ON -> {
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
                    isScreenOn = false
                    updateDutyState(DutyState.SCREEN_OFF)
                }

                Intent.ACTION_USER_PRESENT -> {
                    // User present means they're actively using the device
                    isAppInForeground = true
                    isScreenOn = true
                    updateDutyState(DutyState.APP_OPENED)
                }
            }
        }
    }

    fun start() {
        screenSensor.start()
    }

    fun stop() {
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
            _dutyStateFlow.value = newState

            // Send command to watch based on new state
            sendCommandToWatch(newState)
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

    // Set callback for sending commands to watch
    fun setSendCommandCallback(callback: (String) -> Unit) {
        sendCommandCallback = callback
        // Send initial command now that callback is set, especially for continuous monitoring mode
        sendCommandToWatch(_dutyStateFlow.value)
    }
}
