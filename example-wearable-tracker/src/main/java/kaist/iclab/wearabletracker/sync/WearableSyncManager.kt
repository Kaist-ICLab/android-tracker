package kaist.iclab.wearabletracker.sync

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.sync.BLEDataChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

// TODO: Remove this class
@Serializable
data class TestData(
    val test: String,
    val test2: Int,
)

class WearaBLEDataChannel(context: Context) {

    companion object {
        private const val TAG_RECEIVED_FROM_WATCH = "WATCH_RECEIVED"
    }

    private val syncManager = BLEDataChannel(context)
    
    // Callbacks for duty cycling commands
    private var onContinuousSensingCallback: (() -> Unit)? = null
    private var onSmartDutyCyclingCallback: (() -> Unit)? = null
    private var onAggressiveDutyCyclingCallback: (() -> Unit)? = null
    private var onStopMonitoringCallback: (() -> Unit)? = null

    init {
        setupSyncListeners()
    }

    private fun setupSyncListeners() {
        syncManager.addOnReceivedListener(setOf("test")) { key, json ->
            Log.v(TAG_RECEIVED_FROM_WATCH, "Received From Phone - Key: $key, Data: $json")
        }
        syncManager.addOnReceivedListener(setOf("test2")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.v(TAG_RECEIVED_FROM_WATCH, "Received TestData From Phone - Key: $key, Data: $testData")
        }
        
        // Listen for duty cycling commands from phone
        syncManager.addOnReceivedListener(setOf("duty_command")) { key, json ->
            val command = json.toString().trim('"')
            Log.v(TAG_RECEIVED_FROM_WATCH, "Received duty command from phone: $command")
            
            when (command) {
                "START_CONTINUOUS" -> {
                    Log.v(TAG_RECEIVED_FROM_WATCH, "Sensing State: Starting continuous sensing")
                    onContinuousSensingCallback?.invoke()
                    sendResponse("CONTINUOUS_SENSING_STARTED")
                }
                "START_SMART_DUTY_CYCLING" -> {
                    Log.v(TAG_RECEIVED_FROM_WATCH, "Sensing State: Starting smart duty cycling (balanced)")
                    onSmartDutyCyclingCallback?.invoke()
                    sendResponse("SMART_DUTY_CYCLING_STARTED")
                }
                "START_AGGRESSIVE_DUTY_CYCLING" -> {
                    Log.v(TAG_RECEIVED_FROM_WATCH, "Sensing State: Starting aggressive duty cycling (battery saving)")
                    onAggressiveDutyCyclingCallback?.invoke()
                    sendResponse("AGGRESSIVE_DUTY_CYCLING_STARTED")
                }
                "STOP_MONITORING" -> {
                    Log.v(TAG_RECEIVED_FROM_WATCH, "Sensing State: Stopping monitoring")
                    onStopMonitoringCallback?.invoke()
                    sendResponse("MONITORING_STOPPED")
                }
                else -> {
                    Log.e(TAG_RECEIVED_FROM_WATCH, "Sensing State: Unknown duty command: $command")
                    sendResponse("UNKNOWN_COMMAND")
                }
            }
        }
    }

    // TODO: Remove this function
    fun sendTestMessage() {
        CoroutineScope(Dispatchers.IO).launch { syncManager.send("test", "HELLO_FROM_WEARABLE") }
    }

    // TODO: Remove this function
    fun sendTestData() {
        CoroutineScope(Dispatchers.IO).launch {
            val testData = TestData(test = "HELLO_FROM_WEARABLE", test2 = 789)
            syncManager.send("test2", testData)
        }
    }
    
    // Set callbacks for duty cycling commands. Those functions are called from SettingsViewModel
    fun setOnContinuousSensingCallback(callback: () -> Unit) {
        onContinuousSensingCallback = callback
    }
    
    fun setOnSmartDutyCyclingCallback(callback: () -> Unit) {
        onSmartDutyCyclingCallback = callback
    }
    
    fun setOnAggressiveDutyCyclingCallback(callback: () -> Unit) {
        onAggressiveDutyCyclingCallback = callback
    }
    
    fun setOnStopMonitoringCallback(callback: () -> Unit) {
        onStopMonitoringCallback = callback
    }
    
    // Send response back to phone
    private fun sendResponse(response: String) {
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.send("duty_response", response)
            Log.v(TAG_RECEIVED_FROM_WATCH, "Sensing State: Sent response to phone: $response")
        }
    }
}
