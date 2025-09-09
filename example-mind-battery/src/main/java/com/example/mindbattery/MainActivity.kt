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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val appManager: AppManager by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContent {
                DutyCyclingApp(
                    appManager = appManager
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
fun DutyCyclingApp(appManager: AppManager) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DutyCyclingContent(appManager = appManager)
        }
    }
}


@Composable
fun DutyCyclingContent(appManager: AppManager) {
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
        ) {
            Text("View Logs")
        }
        
        // Description
        Text(
            text = "This app provide command state to the smartwatch:\n\n" +
                    "• When app is OPENED:\n" +
                    "State - Continuos Monitoring\n\n" +
                    "• When app is MINIMIZED:\n" +
                    "State - Monitoring in Duty Cycling\n\n" +
                    "• When screen is OFF:\n" +
                    "State - Monitoring Paused",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp)
        )
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