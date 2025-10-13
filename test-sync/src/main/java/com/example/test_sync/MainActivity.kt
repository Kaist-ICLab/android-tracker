package com.example.test_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.test_sync.ui.theme.AndroidtrackerTheme
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.tracker.sync.ble.BLEReceiver
import kaist.iclab.tracker.sync.ble.BLESender
import kaist.iclab.tracker.sync.core.DataReceiver
import kaist.iclab.tracker.sync.core.DataSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class TestData(
    val message: String,
    val value: Int,
)

class MainActivity : ComponentActivity() {
    // Complete BLE Channel (symmetric - both send and receive)
    private lateinit var bleChannel: BLEDataChannel

    // Individual BLE components (for demonstration)
    private lateinit var bleSender: DataSender<Unit>
    private lateinit var bleReceiver: DataReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize BLE components after context is available
        initializeBLEComponents()

        // Set up listeners for the complete channel
        setupBLEChannelListeners()

        // Set up listeners for individual receiver
        setupBLEReceiverListeners()

        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        // Complete BLE Channel examples
                        sendStringOverBLE = { key, value ->
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d(
                                    "PHONE_BLE_SEND",
                                    "📱 Sending message to watch - Key: '$key', Data: $value"
                                )
                                bleChannel.send(key, value)
                            }
                        },
                        sendTestDataOverBLE = { key, value ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val jsonString = Json.encodeToString(value)
                                Log.d(
                                    "PHONE_BLE_SEND",
                                    "📱 Sending structured data to watch - Key: '$key', Data: $jsonString"
                                )
                                bleChannel.send(key, jsonString)
                            }
                        },
                        sendUrgentBLE = { key, value ->
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d(
                                    "PHONE_BLE_SEND",
                                    "🚨 Sending URGENT message to watch - Key: '$key', Data: $value"
                                )
                                bleChannel.send(key, value, isUrgent = true)
                            }
                        },
                        // Individual BLE Sender examples
                        senderOnlyBLE = { key, value ->
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d(
                                    "PHONE_BLE_SEND",
                                    "📱 Sending data to watch (sender only) - Key: '$key', Data: $value"
                                )
                                bleSender.send(key, value)
                            }
                        },
                        // Style Modifier
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    private fun initializeBLEComponents() {
        // Initialize BLE components after context is available
        bleChannel = BLEDataChannel(this)
        bleSender = BLESender(this)
        bleReceiver = BLEReceiver()
    }

    private fun setupBLEChannelListeners() {
        // Listen for simple string messages
        bleChannel.addOnReceivedListener(setOf("message")) { key, json ->
            Log.d("PHONE_BLE_CHANNEL", "📱 Received message from watch - Key: '$key', Data: $json")
        }

        // Listen for structured data
        bleChannel.addOnReceivedListener(setOf("structured_data")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.d(
                "PHONE_BLE_CHANNEL",
                "📱 Received structured data from watch - Key: '$key', Data: $testData"
            )
        }

        // Listen for urgent messages
        bleChannel.addOnReceivedListener(setOf("urgent_message")) { key, json ->
            Log.d("PHONE_BLE_CHANNEL", "🚨 URGENT message from watch - Key: '$key', Data: $json")
        }
    }

    private fun setupBLEReceiverListeners() {
        // An individual receiver can listen to different keys
        bleReceiver.addOnReceivedListener(setOf("sensor_data")) { key, json ->
            Log.d(
                "PHONE_BLE_RECEIVER",
                "📱 Received sensor data from watch - Key: '$key', Data: $json"
            )
        }

        bleReceiver.addOnReceivedListener(setOf("device_status")) { key, json ->
            Log.d(
                "PHONE_BLE_RECEIVER",
                "📱 Received device status from watch - Key: '$key', Data: $json"
            )
        }
    }
}