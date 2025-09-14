package com.example.test_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.test_sync.ui.theme.AndroidtrackerTheme
import kaist.iclab.tracker.sync.BLEDataChannel
import kaist.iclab.tracker.sync.InternetDataChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class TestData(
    val test: String,
    val test2: Int,
)

class MainActivity : ComponentActivity() {
    private val bleChannel = BLEDataChannel(this)
    private val internetChannel = InternetDataChannel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bleChannel.addOnReceivedListener(setOf("test")) { key, json ->
            Log.v("PHONE_RECEIVED", "Received From Watch: $json")
        }

        bleChannel.addOnReceivedListener(setOf("test2")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.v("PHONE_RECEIVED", "Received TestData From Watch: $testData")
        }

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        sendStringOverBLE = { key, value -> CoroutineScope(Dispatchers.IO).launch { bleChannel.send(key, value) }},
                        sendTestDataOverBLE = { key, value -> CoroutineScope(Dispatchers.IO).launch { bleChannel.send(key, value) }},
                        sendStringOverInternet = { key, value -> CoroutineScope(Dispatchers.IO).launch { internetChannel.send(key, value) }},
                        sendTestDataOverInternet = { key, value -> CoroutineScope(Dispatchers.IO).launch { internetChannel.send(key, value) }},
                        modifier = Modifier.padding(innerPadding).fillMaxSize()
                    )
                }
            }
        }
    }
}