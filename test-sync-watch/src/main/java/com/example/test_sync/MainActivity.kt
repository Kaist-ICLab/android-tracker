package com.example.test_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
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
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize BLE components after context is available
        initializeBLEComponents()

        // Set up listeners for the complete channel
        setupBLEChannelListeners()

        // Set up listeners for individual receiver
        setupBLEReceiverListeners()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(
                // Complete BLE Channel examples
                sendStringOverBLE = { key, value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d(
                            "WATCH_BLE_SEND",
                            "⌚ Sending message to phone - Key: '$key', Data: $value"
                        )
                        bleChannel.send(key, value)
                    }
                },
                sendTestDataOverBLE = { key, value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val jsonString = Json.encodeToString(value)
                        Log.d(
                            "WATCH_BLE_SEND",
                            "⌚ Sending structured data to phone - Key: '$key', Data: $jsonString"
                        )
                        bleChannel.send(key, jsonString)
                    }
                },
                sendUrgentBLE = { key, value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d(
                            "WATCH_BLE_SEND",
                            "🚨 Sending URGENT message to phone - Key: '$key', Data: $value"
                        )
                        bleChannel.send(key, value, isUrgent = true)
                    }
                },

                // Individual BLE Sender examples
                senderOnlyBLE = { key, value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d(
                            "WATCH_BLE_SEND",
                            "⌚ Sending data to phone (sender only) - Key: '$key', Data: $value"
                        )
                        bleSender.send(key, value)
                    }
                }
            )
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
            Log.d("WATCH_BLE_CHANNEL", "⌚ Received message from phone - Key: '$key', Data: $json")
        }

        // Listen for structured data
        bleChannel.addOnReceivedListener(setOf("structured_data")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.d(
                "WATCH_BLE_CHANNEL",
                "⌚ Received structured data from phone - Key: '$key', Data: $testData"
            )
        }

        // Listen for urgent messages
        bleChannel.addOnReceivedListener(setOf("urgent_message")) { key, json ->
            Log.d("WATCH_BLE_CHANNEL", "🚨 URGENT message from phone - Key: '$key', Data: $json")
        }
    }

    private fun setupBLEReceiverListeners() {
        // An individual receiver can listen to different keys
        bleReceiver.addOnReceivedListener(setOf("sensor_data")) { key, json ->
            Log.d(
                "WATCH_BLE_RECEIVER",
                "⌚ Received sensor data from phone - Key: '$key', Data: $json"
            )
        }

        bleReceiver.addOnReceivedListener(setOf("device_status")) { key, json ->
            Log.d(
                "WATCH_BLE_RECEIVER",
                "⌚ Received device status from phone - Key: '$key', Data: $json"
            )
        }
    }
}

@Composable
fun WearApp(
    sendStringOverBLE: (String, String) -> Unit,
    sendTestDataOverBLE: (String, TestData) -> Unit,
    sendUrgentBLE: (String, String) -> Unit,
    senderOnlyBLE: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⌚ Watch BLE Test",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Text(
                text = "Complete BLE Channel",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )

            Button(
                onClick = { sendStringOverBLE("message", "WATCH_HELLO_FROM_WATCH") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text("Send String")
            }

            Button(
                onClick = {
                    sendTestDataOverBLE(
                        "structured_data",
                        TestData(message = "WATCH_DATA_FROM_WATCH", value = 456)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text("Send TestData")
            }

            Button(
                onClick = { sendUrgentBLE("urgent_message", "WATCH_URGENT_FROM_WATCH") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text("Send Urgent")
            }

            Text(
                text = "BLE Sender Only",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )

            Button(
                onClick = { senderOnlyBLE("sensor_data", "WATCH_SENSOR_DATA") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text("Send Sensor Data")
            }

            Button(
                onClick = { senderOnlyBLE("device_status", "WATCH_STATUS_UPDATE") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text("Send Status")
            }

            Text(
                text = "Check logs for received messages",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}
