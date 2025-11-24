package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.components.LogoutDialog.LogoutDialog
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import kaist.iclab.mobiletracker.viewmodels.SettingsViewModel
import kaist.iclab.tracker.sensor.controller.ControllerState
import org.koin.androidx.compose.koinViewModel

private val BUTTON_PADDING = 10.dp

/**
 * Home screen displaying user information and actions
 */
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val userState by authViewModel.userState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val controllerState = settingsViewModel.controllerState.collectAsState().value
    val isCollecting = controllerState.flag == ControllerState.FLAG.RUNNING

    // Logout confirmation dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = { authViewModel.logout() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userState.isLoggedIn) {
            Text(
                text = context.getString(
                    R.string.hello,
                    userState.user?.name ?: context.getString(R.string.unknown)
                ),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = context.getString(
                    R.string.email,
                    userState.user?.email ?: context.getString(R.string.no_email)
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { showLogoutDialog = true }) {
                Text(context.getString(R.string.sign_out))
            }
        } else {
            Text(
                text = context.getString(R.string.not_logged_in),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (activity != null) {
                Button(onClick = { authViewModel.login(activity) }) {
                    Text(context.getString(R.string.sign_in_with_google))
                }
            }
        }
    }
}

