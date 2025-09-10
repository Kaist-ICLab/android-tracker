package com.example.mindbattery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.tracker.sync.BLESyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.android.ext.android.inject

@Serializable
data class TestData(
    val test: String,
    val test2: Int,
)

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val appManager: AppManager by inject()
    private val syncManager = BLESyncManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Remove this function
        syncManager.addOnReceivedListener(setOf("test")) { key, json ->
            Log.v("PHONE_RECEIVED", "Received From Watch: $json")
        }

        // TODO: Remove this function
        syncManager.addOnReceivedListener(setOf("test2")) { key, json ->
            val testData: TestData = Json.decodeFromJsonElement(json)
            Log.v("PHONE_RECEIVED", "Received TestData From Watch: $testData")
        }

        // Listen for duty cycling responses from watch
        syncManager.addOnReceivedListener(setOf("duty_response")) { key, json ->
            val response = json.toString().trim('"')
            Log.v("PHONE_RECEIVED", "Received duty response from watch: $response")
        }

        // Set up callback for sending commands to watch
        appManager.setSendCommandCallback { command ->
            CoroutineScope(Dispatchers.IO).launch {
                syncManager.send("duty_command", command)
            }
        }

        try {
            setContent {
                DummyMindBattery(
                    appManager = appManager,
                    syncManager = syncManager
                )
            }
            appManager.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            // Show a simple error message
            setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "Error: ${e.message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appManager.onAppOpened()
    }

    override fun onPause() {
        super.onPause()
        appManager.onAppMinimized()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        appManager.sendStopCommandToWatch()
        appManager.stop()
    }
}

@Composable
fun DummyMindBattery(appManager: AppManager, syncManager: BLESyncManager) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppContent(appManager = appManager, syncManager = syncManager)
        }
    }
}


@Composable
fun AppContent(appManager: AppManager, syncManager: BLESyncManager) {
    val dutyState by appManager.dutyStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Mind Battery Dummy App",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
        )

        // Status
        val statusText = when (dutyState) {
            AppManager.DutyState.APP_OPENED -> "🟢 APP OPENED"
            AppManager.DutyState.APP_MINIMIZED -> "🟡 APP MINIMIZED"
            AppManager.DutyState.SCREEN_OFF -> "🔴 SCREEN OFF"
        }

        Text(
            text = statusText,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Test buttons like test-sync module. TODO: Remove those later
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    syncManager.send("test", "HELLO_FROM_PHONE")
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Command - Send Test Text")
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val testData = TestData(test = "HELLO_FROM_PHONE", test2 = 123)
                    syncManager.send("test2", testData)
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Command -Send Test Data")
        }

        Button(
            onClick = {
                appManager.sendStopCommandToWatch()
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Command - Stop Monitoring")
        }
    }
}
