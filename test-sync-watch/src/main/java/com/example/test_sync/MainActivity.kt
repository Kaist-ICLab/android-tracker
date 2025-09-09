package com.example.test_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kaist.iclab.tracker.sync.BLESyncManager
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
    private val syncManager = BLESyncManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        syncManager.addOnReceivedListener(setOf("test")) { key, json ->
            Log.v("WATCH_RECEIVED", "Received from phone: $json")
        }

        syncManager.addOnReceivedListener(setOf("test2")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.v("WATCH_RECEIVED", "Received TestData from phone: $testData")
        }

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(
                sendText = {
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("WATCH_SENDING", "Sending text to phone: HELLO")
                        syncManager.send(
                            "test",
                            "HELLO"
                        )
                    }
                },
                sendData = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val testData = TestData(test = "HELLO-FROM-WATCH", test2 = 123)
                        Log.d("WATCH_SENDING", "Sending TestData to phone: $testData")
                        syncManager.send(
                            "test2",
                            testData
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun WearApp(
    sendText: () -> Unit,
    sendData: () -> Unit,
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
        ) {
            Button(
                onClick = sendText,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Text")
            }

            Button(
                onClick = sendData,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Data")
            }
        }
    }
}