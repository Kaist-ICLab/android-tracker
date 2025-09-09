package kaist.iclab.wearabletracker.sync

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.sync.BLESyncManager
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

class WearableSyncManager(context: Context) {

    companion object {
        private const val TAG_RECEIVED_FROM_WATCH = "WATCH_RECEIVED"
    }

    private val syncManager = BLESyncManager(context)
    
    // Callbacks for duty cycling commands
    private var onContinuousSensingCallback: (() -> Unit)? = null
    private var onDutyCyclingCallback: (() -> Unit)? = null

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
                "CONTINUOUS_SENSING" -> {
                    Log.d(TAG_RECEIVED_FROM_WATCH, "Starting continuous sensing")
                    onContinuousSensingCallback?.invoke()
                    sendResponse("CONTINUOUS_SENSING_STARTED")
                }
                "DUTY_CYCLING" -> {
                    Log.d(TAG_RECEIVED_FROM_WATCH, "Starting duty cycling")
                    onDutyCyclingCallback?.invoke()
                    sendResponse("DUTY_CYCLING_STARTED")
                }
                else -> {
                    Log.w(TAG_RECEIVED_FROM_WATCH, "Unknown duty command: $command")
                    sendResponse("UNKNOWN_COMMAND")
                }
            }
        }
    }

    fun sendTestMessage() {
        CoroutineScope(Dispatchers.IO).launch { syncManager.send("test", "HELLO_FROM_WEARABLE") }
    }

    fun sendTestData() {
        CoroutineScope(Dispatchers.IO).launch {
            val testData = TestData(test = "HELLO_FROM_WEARABLE", test2 = 789)
            syncManager.send("test2", testData)
        }
    }
    
    // Set callbacks for duty cycling commands
    fun setOnContinuousSensingCallback(callback: () -> Unit) {
        onContinuousSensingCallback = callback
    }
    
    fun setOnDutyCyclingCallback(callback: () -> Unit) {
        onDutyCyclingCallback = callback
    }
    
    // Send response back to phone
    private fun sendResponse(response: String) {
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.send("duty_response", response)
            Log.d(TAG_RECEIVED_FROM_WATCH, "Sent response to phone: $response")
        }
    }
}
