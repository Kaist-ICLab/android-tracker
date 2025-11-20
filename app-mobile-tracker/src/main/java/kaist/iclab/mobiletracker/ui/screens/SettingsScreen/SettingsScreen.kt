package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.SettingsViewModel
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

/**
 * Settings screen with sensor management functionality
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isCollecting =
        viewModel.controllerState.collectAsState().value.flag == ControllerState.FLAG.RUNNING
    val controllerStateValue = viewModel.controllerState.collectAsState().value

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Button(
                onClick = {
                    if (isCollecting) viewModel.stopLogging()
                    else {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            viewModel.startLogging()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isCollecting) "Stop Sensors" else "Start Sensors",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        items(
            items = viewModel.sensorState.toList(),
            key = { it.first }
        ) { (key, value) ->
            SensorTestRow(
                sensorName = key,
                sensorStateFlow = value,
                isControllerRunning = controllerStateValue.flag == ControllerState.FLAG.RUNNING,
                toggleSensor = { viewModel.toggleSensor(key) },
                sensorValue = if (key == "DeviceModeSensor") {
                    "Tap 'Test Device Modes' below to verify data collection"
                } else ""
            )
        }
    }
}

@Composable
fun SensorTestRow(
    sensorName: String,
    sensorStateFlow: StateFlow<SensorState>,
    isControllerRunning: Boolean,
    toggleSensor: () -> Unit,
    sensorValue: String,
    modifier: Modifier = Modifier,
) {
    val currentSensorState = sensorStateFlow.collectAsState().value

    val isSensorEnabled =
        (currentSensorState.flag == SensorState.FLAG.RUNNING || currentSensorState.flag == SensorState.FLAG.ENABLED)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(15.dp)
    ) {
        Text(
            text = sensorName,
            color = AppColors.TextPrimary
        )
        Spacer(Modifier.width(10.dp))
        SmallSquareIconButton(
            icon = if (isSensorEnabled) Icons.Default.Check else Icons.Default.Close,
            enabled = !isControllerRunning,
            onClick = toggleSensor
        )
        Spacer(Modifier.width(15.dp))
        if (sensorValue.isNotEmpty()) {
            Text(
                text = sensorValue,
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
fun SmallSquareIconButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .width(25.dp)
            .height(25.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
        )
    }
}

