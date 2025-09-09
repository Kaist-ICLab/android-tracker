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
}
