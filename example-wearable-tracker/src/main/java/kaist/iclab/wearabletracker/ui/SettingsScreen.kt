package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kaist.iclab.wearabletracker.data.DeviceInfo
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.scrollAway
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    androidPermissionManager: AndroidPermissionManager,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val sensorMap = settingsViewModel.sensorMap
    val isCollecting = settingsViewModel.controllerState.collectAsState().value
    val sensorState = settingsViewModel.sensorState
    val listState = rememberScalingLazyListState() // for Scaling Lazy column
    
    // Device information state
    var deviceInfo by remember { mutableStateOf(DeviceInfo()) }
    LaunchedEffect(Unit) {
        settingsViewModel.getDeviceInfo(context) { receivedDeviceInfo ->
            deviceInfo = receivedDeviceInfo
        }
    }

    //UI
    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.scrollAway(listState))
        },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
        ) {
            SettingController(
                upload = { settingsViewModel.upload() },
                flush = { settingsViewModel.flush() },
                startLogging = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            androidPermissionManager.request(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                        }
                    }
                    settingsViewModel.startLogging()
               },
                stopLogging = { settingsViewModel.stopLogging() },
                isCollecting = (isCollecting.flag == ControllerState.FLAG.RUNNING)
            )
            DeviceInfo(
                deviceInfo = deviceInfo,
            )
            ScalingLazyColumn(
                state = listState
            ) { // Lazy column for WearOS
                sensorState.forEach { name, state ->
                    item {
                        SensorToggleChip(
                            sensorName = name,
                            sensorStateFlow = state,
                            updateStatus = { status ->
                                if(status) {
                                    androidPermissionManager.request(sensorMap[name]!!.permissions)
                                }
                                settingsViewModel.update(name, status)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingController(
    upload: () -> Unit,
    flush: () -> Unit,
    startLogging: () -> Unit,
    stopLogging: () -> Unit,
    isCollecting: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            icon = Icons.Default.Upload,
            onClick = upload,
            contentDescription = "Upload data",
            backgroundColor = MaterialTheme.colors.secondary,
            buttonSize = 32.dp,
            iconSize = 20.dp
        )
        IconButton(
            icon = if (isCollecting) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
            onClick = if (isCollecting) stopLogging else startLogging,
            contentDescription = "Start/Stop Collection",
            backgroundColor = if (isCollecting) MaterialTheme.colors.error else MaterialTheme.colors.primary,
            buttonSize = 48.dp,
            iconSize = 36.dp,
        )
        IconButton(
            icon = Icons.Default.Delete,
            onClick = flush,
            contentDescription = "Reset icon",
            backgroundColor = MaterialTheme.colors.secondary,
            buttonSize = 32.dp,
            iconSize = 20.dp
        )
    }
}

@Composable
fun SensorToggleChip(
    sensorName: String,
    sensorStateFlow: StateFlow<SensorState>,
    updateStatus: (status: Boolean) -> Unit
) {
    val sensorState = sensorStateFlow.collectAsState().value
    val isEnabled = (sensorState.flag == SensorState.FLAG.ENABLED || sensorState.flag == SensorState.FLAG.RUNNING)

    ToggleChip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
            .height(32.dp),
        checked = isEnabled,
        toggleControl = {
            Switch(
                checked = isEnabled,
                modifier = Modifier.semantics {
                    this.contentDescription = if (isEnabled) "On" else "Off"
                },
            )
        },
        onCheckedChange = updateStatus,
        label = {
            Text(
                text = sensorName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
            )
        }
    )
}

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    backgroundColor: Color,
    buttonSize: Dp = 32.dp,
    iconSize: Dp = 20.dp,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier
            .padding(4.dp)
            .size(buttonSize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun DeviceInfo(
    deviceInfo: DeviceInfo,
) {
    Text(
        fontSize = 10.sp,
        text = deviceInfo.name,
        style = MaterialTheme.typography.body1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun IconButtonPreview(){
    IconButton(
        icon = Icons.Default.PlayArrow,
        onClick = {},
        contentDescription =  "ASDAS",
        backgroundColor = MaterialTheme.colors.primary
    )
}
