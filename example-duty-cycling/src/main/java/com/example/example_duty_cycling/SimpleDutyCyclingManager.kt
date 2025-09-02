package com.example.example_duty_cycling

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        ACTIVE,     // App is in foreground, continuous monitoring
        PAUSED      // App is minimized, monitoring paused
    }
    
    // State flows
    private val _dutyStateFlow = MutableStateFlow(DutyState.PAUSED)
    val dutyStateFlow: StateFlow<DutyState> = _dutyStateFlow
    
    private val _lastStateChangeFlow = MutableStateFlow(System.currentTimeMillis())
    val lastStateChangeFlow: StateFlow<Long> = _lastStateChangeFlow
    
    // Monitoring job
    private var monitoringJob: Job? = null
    
    // Screen state receiver
    private val screenReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    Log.d(TAG, "Screen turned ON")
                    updateDutyState(DutyState.ACTIVE)
                }
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d(TAG, "Screen turned OFF")
                    updateDutyState(DutyState.PAUSED)
                }
                Intent.ACTION_USER_PRESENT -> {
                    Log.d(TAG, "User present")
                    updateDutyState(DutyState.ACTIVE)
                }
            }
        }
    }
    
    init {
        Log.d(TAG, "SimpleDutyCyclingManager initialized")
        
        // Register screen state receiver
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        context.registerReceiver(screenReceiver, filter)
        
        // Start as paused initially
        updateDutyState(DutyState.PAUSED)
    }
    
    fun start() {
        Log.d(TAG, "Starting simple duty cycling manager")
    }
    
    fun stop() {
        Log.d(TAG, "Stopping simple duty cycling manager")
        stopMonitoring()
        
        try {
            context.unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    private fun updateDutyState(newState: DutyState) {
        if (_dutyStateFlow.value != newState) {
            val oldState = _dutyStateFlow.value
            _dutyStateFlow.value = newState
            _lastStateChangeFlow.value = System.currentTimeMillis()
            
            Log.d(TAG, "Duty state changed from $oldState to $newState")
            
            // Log state change
            logStateChange(oldState, newState)
            
            // Send state data
            sendStateData()
            
            // Manage monitoring
            when (newState) {
                DutyState.ACTIVE -> startMonitoring()
                DutyState.PAUSED -> stopMonitoring()
            }
        }
    }
    
    private fun startMonitoring() {
        if (monitoringJob?.isActive == true) return
        
        monitoringJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Starting continuous monitoring")
            
            while (monitoringJob?.isActive == true && _dutyStateFlow.value == DutyState.ACTIVE) {
                // Simulate continuous monitoring
                Log.d(TAG, "Monitoring active - collecting data...")
                
                // Here you would integrate with sensors from tracker library
                // For example: step sensor, location sensor, etc.
                
                // Wait before next check
                delay(5000) // Check every 5 seconds
            }
        }
    }
    
    private fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        Log.d(TAG, "Continuous monitoring stopped")
    }
    
    private fun logStateChange(oldState: DutyState, newState: DutyState) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "[$timestamp] Duty state changed: $oldState -> $newState\n"
        
        // Write to log file
        try {
            val logFile = File(context.filesDir, "duty_cycling.log")
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    private fun sendStateData() {
        val jsonData = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("duty_state", _dutyStateFlow.value.name)
            put("state_change_time", _lastStateChangeFlow.value)
            put("device_id", android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ))
        }
        
        Log.d(TAG, "Duty cycling data: ${jsonData.toString()}")
        
        // Save to file
        try {
            val dataFile = File(context.filesDir, "duty_cycling_data.json")
            dataFile.writeText(jsonData.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save duty cycling data", e)
        }
        
        // Here you could also send to server, database, etc.
    }
    
    fun getCurrentState(): DutyState = _dutyStateFlow.value
    
    fun getLastStateChange(): Long = _lastStateChangeFlow.value
}
