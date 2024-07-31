package dev.iclab.tracker.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = koinViewModel()
    val uiState by mainViewModel.uiState.collectAsState()
    Row {
        Button(onClick = { mainViewModel.start() },
        enabled = uiState.isRunning.not()){
            Text("Start")
        }
        Button(onClick = { mainViewModel.stop() },
            enabled = uiState.isRunning
        ) {
            Text("Stop")
        }
    }
}