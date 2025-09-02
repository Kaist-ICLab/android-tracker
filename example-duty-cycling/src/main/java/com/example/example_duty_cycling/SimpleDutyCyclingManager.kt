package com.example.example_duty_cycling

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SimpleDutyCyclingManager(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "SimpleDutyCyclingManager"
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
    
    // Screen state receiver
    private val screenReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        Log.d(TAG, "Screen turned ON")
                        // Check if app is in foreground or background
                        updateDutyState(DutyState.APP_OPENED)
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        Log.d(TAG, "Screen turned OFF")
                        updateDutyState(DutyState.SCREEN_OFF)
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        Log.d(TAG, "User present")
                        updateDutyState(DutyState.APP_OPENED)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in screen receiver", e)
            }
        }
    }
    
    init {
        try {
            Log.d(TAG, "SimpleDutyCyclingManager initialized")
            
            // Register screen state receiver
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            context.registerReceiver(screenReceiver, filter)
            
            // Start as app opened initially
            updateDutyState(DutyState.APP_OPENED)
            
            Log.d(TAG, "SimpleDutyCyclingManager initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing SimpleDutyCyclingManager", e)
        }
    }
    
    fun start() {
        try {
            Log.d(TAG, "Starting simple duty cycling manager")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting duty cycling manager", e)
        }
    }
    
    fun stop() {
        try {
            Log.d(TAG, "Stopping simple duty cycling manager")
            context.unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping duty cycling manager", e)
        }
    }
    
    // Method to be called when app goes to background
    fun onAppMinimized() {
        try {
            updateDutyState(DutyState.APP_MINIMIZED)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onAppMinimized", e)
        }
    }
    
    // Method to be called when app comes to foreground
    fun onAppOpened() {
        try {
            updateDutyState(DutyState.APP_OPENED)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onAppOpened", e)
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
                
                // Send JSON command
                sendJSONCommand(newState)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating duty state", e)
        }
    }
    
    private fun logStateChange(oldState: DutyState, newState: DutyState) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "[$timestamp] Duty state changed: $oldState -> $newState\n"
            
            // Write to log file
            val logFile = File(context.filesDir, "duty_cycling.log")
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    private fun sendJSONCommand(state: DutyState) {
        try {
            val message = when (state) {
                DutyState.APP_OPENED -> "Continuous Monitoring Started - App Opened"
                DutyState.APP_MINIMIZED -> "Continuous Monitoring Started - App Minimized"
                DutyState.SCREEN_OFF -> "Continuous Monitoring Paused"
            }
            
            val jsonData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("command", message)
                put("state", state.name)
                put("state_change_time", _lastStateChangeFlow.value)
                put("device_id", android.provider.Settings.Secure.getString(
                    context.contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                ))
            }
            
            Log.d(TAG, "JSON Command: ${jsonData.toString()}")
            
            // Save to file
            val dataFile = File(context.filesDir, "duty_cycling_commands.json")
            dataFile.writeText(jsonData.toString())
            
            // Here you could also send to server, other devices, etc.
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send JSON command", e)
        }
    }
    
    fun getCurrentState(): DutyState = _dutyStateFlow.value
    
    fun getLastStateChange(): Long = _lastStateChangeFlow.value
}
