package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.viewmodels.auth.AuthViewModel
import kaist.iclab.mobiletracker.viewmodels.settings.SettingsViewModel
import kaist.iclab.tracker.sensor.controller.ControllerState
import org.koin.androidx.compose.koinViewModel

private val BUTTON_PADDING = 10.dp

/**
 * Home screen displaying start sensors button
 */
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val controllerState = settingsViewModel.controllerState.collectAsState().value
    val isCollecting = controllerState.flag == ControllerState.FLAG.RUNNING

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Start/Stop Sensors Button
        Button(
            onClick = if (isCollecting) {
                { settingsViewModel.stopLogging() }
            } else {
                {
                    if (settingsViewModel.hasNotificationPermission()) {
                        settingsViewModel.startLogging()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BUTTON_PADDING, vertical = BUTTON_PADDING)
        ) {
            Text(
                text = if (isCollecting) {
                    context.getString(R.string.stop_sensors)
                } else {
                    context.getString(R.string.start_sensors)
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

