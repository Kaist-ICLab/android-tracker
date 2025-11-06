package kaist.iclab.mobiletracker.helpers

import android.content.Context
import android.util.Log
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.tracker.sync.ble.BLEDataChannel

class BLEHelper(private val context: Context) {
    private lateinit var bleChannel: BLEDataChannel

    fun initialize() {
        bleChannel = BLEDataChannel(context)
        setupListeners()
    }

    private fun setupListeners() {
        // Listen for sensor CSV data from watch
        bleChannel.addOnReceivedListener(setOf(AppConfig.BLEKeys.SENSOR_DATA_CSV)) { key, json ->
            val csvData = when {
                json is kotlinx.serialization.json.JsonPrimitive -> json.content
                else -> json.toString()
            }
            Log.d(
                AppConfig.LogTags.PHONE_BLE,
                "📱 Received sensor CSV data from watch"
            )
            Log.d(AppConfig.LogTags.PHONE_BLE, "📊 CSV Data (${csvData.length} chars):\n$csvData")
        }
    }
}
