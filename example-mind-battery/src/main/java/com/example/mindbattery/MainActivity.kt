package com.example.mindbattery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
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
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val appManager: AppManager by inject()
    private val syncManager = BLESyncManager(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup sync listeners like test-sync module
        syncManager.addOnReceivedListener(setOf("test")) { key, json ->
            Log.v("PHONE_RECEIVED", "Received From Watch: $json")
        }

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
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
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
        try {
            // App came to foreground
            appManager.onAppOpened()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    override fun onPause() {
        super.onPause()
        try {
            // App went to background
            appManager.onAppMinimized()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            appManager.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
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
    val lastStateChange by appManager.lastStateChangeFlow.collectAsState()
    var showLogs by remember { mutableStateOf(false) }
    
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
            AppManager.DutyState.APP_OPENED -> "🟢 APP OPENED - Continuous Monitoring"
            AppManager.DutyState.APP_MINIMIZED -> "🟡 APP MINIMIZED - Monitoring in Duty Cycling"
            AppManager.DutyState.SCREEN_OFF -> "🔴 SCREEN OFF - Monitoring Paused"
        }
        
        Text(
            text = statusText,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        // Last change time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = dateFormat.format(Date(lastStateChange))
        Text(
            text = "Last state change: $timeString",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Log button
        Button(
            onClick = { showLogs = true },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("View Logs")
        }
        
        // Test buttons like test-sync module
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    syncManager.send("test", "HELLO_FROM_PHONE")
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Send Test Text")
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
            Text("Send Test Data")
        }
    }
    
    // Logs dialog
    if (showLogs) {
        LogsDialog(
            appManager = appManager,
            onDismiss = { showLogs = false }
        )
    }
}

@Composable
fun LogsDialog(
    appManager: AppManager,
    onDismiss: () -> Unit
) {
    var logs by remember { mutableStateOf("Loading...") }
    
    LaunchedEffect(Unit) {
        try {
            // Get logs directly from the duty cycling manager
            logs = appManager.getFormattedLogs()
        } catch (e: Exception) {
            logs = "Error reading logs: ${e.message}"
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recorded State Logs") },
        text = { 
            Text(
                text = logs,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}