package com.example.test_sync.helpers

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import com.example.test_sync.TestData
import com.example.test_sync.config.AppConfig

class BLEHelper(private val context: Context) {
    private lateinit var bleChannel: BLEDataChannel

    fun initialize() {
        bleChannel = BLEDataChannel(context)
        setupListeners()
    }

    fun sendString(key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(AppConfig.LogTags.PHONE_BLE, "📱 Sending message to watch - Key: '$key', Data: $value")
            bleChannel.send(key, value)
        }
    }

    fun sendTestData(key: String, value: TestData) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = Json.encodeToString(value)
            Log.d(AppConfig.LogTags.PHONE_BLE, "📱 Sending structured data to watch - Key: '$key', Data: $jsonString")
            bleChannel.send(key, jsonString)
        }
    }

    fun sendUrgent(key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(AppConfig.LogTags.PHONE_BLE, "🚨 Sending urgent message to watch - Key: '$key', Data: $value")
            bleChannel.send(key, value, isUrgent = true)
        }
    }

    private fun setupListeners() {
        // Listen for simple string messages
        bleChannel.addOnReceivedListener(setOf(AppConfig.BLEKeys.MESSAGE)) { key, json ->
            val message = when {
                json is kotlinx.serialization.json.JsonPrimitive -> json.content
                else -> json.toString()
            }
            Log.d(AppConfig.LogTags.PHONE_BLE, "📱 Received message from watch - Key: '$key', Data: $message")
        }

        // Listen for structured data
        bleChannel.addOnReceivedListener(setOf(AppConfig.BLEKeys.STRUCTURED_DATA)) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.d(AppConfig.LogTags.PHONE_BLE, "📱 Received structured data from watch - Key: '$key', Data: $testData")
        }

        // Listen for urgent messages
        bleChannel.addOnReceivedListener(setOf(AppConfig.BLEKeys.URGENT_MESSAGE)) { key, json ->
            val message = when {
                json is kotlinx.serialization.json.JsonPrimitive -> json.content
                else -> json.toString()
            }
            Log.d(AppConfig.LogTags.PHONE_BLE, "🚨 Urgent message from watch - Key: '$key', Data: $message")
        }
    }
}
