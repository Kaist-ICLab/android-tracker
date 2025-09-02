package com.example.example_duty_cycling

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
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), KoinComponent {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val dutyCyclingManager: SimpleDutyCyclingManager by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "MainActivity onCreate started")
            
            setContent {
                DutyCyclingApp(
                    dutyCyclingManager = dutyCyclingManager
                )
            }
            
            // Start duty cycling manager
            dutyCyclingManager.start()
            
            Log.d(TAG, "MainActivity onCreate completed successfully")
            
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
            dutyCyclingManager.onAppOpened()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    override fun onPause() {
        super.onPause()
        try {
            // App went to background
            dutyCyclingManager.onAppMinimized()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            dutyCyclingManager.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
}

@Composable
fun DutyCyclingApp(dutyCyclingManager: SimpleDutyCyclingManager) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DutyCyclingContent(dutyCyclingManager = dutyCyclingManager)
        }
    }
}

@Composable
fun DutyCyclingContent(dutyCyclingManager: SimpleDutyCyclingManager) {
    val dutyState by dutyCyclingManager.dutyStateFlow.collectAsState()
    val lastStateChange by dutyCyclingManager.lastStateChangeFlow.collectAsState()
    var showLogs by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Duty Cycling Commands",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Status
        val statusText = when (dutyState) {
            SimpleDutyCyclingManager.DutyState.APP_OPENED -> "🟢 APP OPENED - Continuous Monitoring Started"
            SimpleDutyCyclingManager.DutyState.APP_MINIMIZED -> "🟡 APP MINIMIZED - Continuous Monitoring Started"
            SimpleDutyCyclingManager.DutyState.SCREEN_OFF -> "🔴 SCREEN OFF - Continuous Monitoring Paused"
        }
        
        Text(
            text = statusText,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
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
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("View Logs")
        }
        
        // Description
        Text(
            text = "This app sends JSON commands to other devices:\n\n" +
                    "• When app is OPENED:\n" +
                    "  'Continuous Monitoring Started - App Opened'\n\n" +
                    "• When app is MINIMIZED:\n" +
                    "  'Continuous Monitoring Started - App Minimized'\n\n" +
                    "• When screen is OFF:\n" +
                    "  'Continuous Monitoring Paused'\n\n" +
                    "• All commands are logged in memory\n" +
                    "• Check logs for detailed history",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
    
    // Logs dialog
    if (showLogs) {
        LogsDialog(
            dutyCyclingManager = dutyCyclingManager,
            onDismiss = { showLogs = false }
        )
    }
}

@Composable
fun LogsDialog(
    dutyCyclingManager: SimpleDutyCyclingManager,
    onDismiss: () -> Unit
) {
    var logs by remember { mutableStateOf("Loading...") }
    
    LaunchedEffect(Unit) {
        try {
            // Get logs directly from the duty cycling manager
            logs = dutyCyclingManager.getFormattedLogs()
        } catch (e: Exception) {
            logs = "Error reading logs: ${e.message}"
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Duty Cycling Logs") },
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