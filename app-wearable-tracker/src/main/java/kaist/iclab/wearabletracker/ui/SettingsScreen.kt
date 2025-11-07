package kaist.iclab.wearabletracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.wearabletracker.data.DeviceInfo
import kaist.iclab.wearabletracker.helpers.PermissionCheckResult
import kaist.iclab.wearabletracker.helpers.PermissionHelper
import kaist.iclab.wearabletracker.theme.AppSizes
import kaist.iclab.wearabletracker.theme.AppSpacing
import kaist.iclab.wearabletracker.theme.AppTypography
import kaist.iclab.wearabletracker.theme.DeviceNameText
import kaist.iclab.wearabletracker.theme.SensorNameText
import kaist.iclab.wearabletracker.theme.SyncStatusText
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

    // Confirmation dialog state
    var showFlushDialog by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedDialog by remember { mutableStateOf(false) }

    /**
     * Helper function to handle notification permission check and execute action if granted.
     * Reduces code duplication across different features (upload, flush, startLogging).
     */
    fun handleNotificationPermissionCheck(onGranted: () -> Unit) {
        when (PermissionHelper.checkNotificationPermission(context, androidPermissionManager)) {
            PermissionCheckResult.Granted -> {
                onGranted()
            }

            PermissionCheckResult.PermanentlyDenied -> {
                showPermissionPermanentlyDeniedDialog = true
            }

            PermissionCheckResult.Requested -> {
                // Permission requested - user needs to grant it and try again
            }
        }
    }

    // Check if any sensor is enabled
    val hasEnabledSensors = sensorState.values.any { stateFlow ->
        val state = stateFlow.collectAsState().value
        state.flag == SensorState.FLAG.ENABLED || state.flag == SensorState.FLAG.RUNNING
    }

    // Device information state
    var deviceInfo by remember { mutableStateOf(DeviceInfo()) }
    LaunchedEffect(Unit) {
        settingsViewModel.getDeviceInfo(context) { receivedDeviceInfo ->
            deviceInfo = receivedDeviceInfo
        }
        // Load last sync timestamp on startup
        settingsViewModel.refreshLastSyncTimestamp()

        // Check notification permission at app startup (will request if needed, but won't show dialog for permanent denial)
        // The permanent denial dialog will only show when user tries to perform an action
        PermissionHelper.checkNotificationPermission(context, androidPermissionManager)
    }

    // Observe last sync timestamp
    val lastSyncTimestamp by settingsViewModel.lastSyncTimestamp.collectAsState()

    //UI
    Scaffold(
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
                upload = {
                    handleNotificationPermissionCheck {
                        settingsViewModel.upload()
                    }
                },
                flush = {
                    handleNotificationPermissionCheck {
                        showFlushDialog = true
                    }
                },
                startLogging = {
                    handleNotificationPermissionCheck {
                        settingsViewModel.startLogging()
                    }
                },
                stopLogging = { settingsViewModel.stopLogging() },
                isCollecting = (isCollecting.flag == ControllerState.FLAG.RUNNING),
                hasEnabledSensors = hasEnabledSensors
            )
            DeviceInfo(
                deviceInfo = deviceInfo,
                lastSyncTimestamp = lastSyncTimestamp,
            )
            ScalingLazyColumn(
                state = listState
            ) { // Lazy column for WearOS
                sensorState.forEach { name, state ->
                    item {
                        SensorToggleChipWithAvailabilityCheck(
                            sensorName = name,
                            sensorStateFlow = state,
                            updateStatus = { status ->
                                if (status) {
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

    // Confirmation Dialog
    FlushConfirmationDialog(
        showDialog = showFlushDialog,
        onDismiss = { showFlushDialog = false },
        onConfirm = {
            settingsViewModel.flush(context)
            showFlushDialog = false
        }
    )

    // Permission Permanently Denied Dialog
    PermissionPermanentlyDeniedDialog(
        showDialog = showPermissionPermanentlyDeniedDialog,
        onDismiss = { showPermissionPermanentlyDeniedDialog = false },
        onOpenSettings = {
            PermissionHelper.openNotificationSettings(context)
            showPermissionPermanentlyDeniedDialog = false
        }
    )
}

@Composable
fun SettingController(
    upload: () -> Unit,
    flush: () -> Unit,
    startLogging: () -> Unit,
    stopLogging: () -> Unit,
    isCollecting: Boolean,
    hasEnabledSensors: Boolean
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
            buttonSize = AppSizes.iconButtonSmall,
            iconSize = AppSizes.iconSmall
        )
        IconButton(
            icon = if (isCollecting) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
            onClick = {
                if (isCollecting) {
                    stopLogging()
                } else if (hasEnabledSensors) {
                    startLogging()
                }
                // Do nothing when disabled
            },
            contentDescription = "Start/Stop Collection",
            backgroundColor = when {
                isCollecting -> MaterialTheme.colors.error
                hasEnabledSensors -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.onSurface.copy(alpha = 0.3f) // Greyed out
            },
            buttonSize = AppSizes.iconButtonMedium,
            iconSize = AppSizes.iconLarge,
        )
        IconButton(
            icon = Icons.Default.Delete,
            onClick = flush,
            contentDescription = "Reset icon",
            backgroundColor = MaterialTheme.colors.secondary,
            buttonSize = AppSizes.iconButtonSmall,
            iconSize = AppSizes.iconSmall
        )
    }
}

@Composable
fun SensorToggleChipWithAvailabilityCheck(
    sensorName: String,
    sensorStateFlow: StateFlow<SensorState>,
    updateStatus: (status: Boolean) -> Unit
) {
    val sensorState = sensorStateFlow.collectAsState().value

    // Only render the chip if the sensor is available
    if (sensorState.flag != SensorState.FLAG.UNAVAILABLE) {
        SensorToggleChip(
            sensorName = sensorName,
            sensorStateFlow = sensorStateFlow,
            updateStatus = updateStatus
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
    val isEnabled =
        (sensorState.flag == SensorState.FLAG.ENABLED || sensorState.flag == SensorState.FLAG.RUNNING)

    ToggleChip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = AppSpacing.sensorChipHorizontal,
                end = AppSpacing.sensorChipHorizontal,
                bottom = AppSpacing.sensorChipBottom
            )
            .height(AppSizes.sensorChipHeight),
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
            SensorNameText(
                text = sensorName,
                maxLines = 1
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
            .padding(AppSpacing.iconButtonPadding)
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
    lastSyncTimestamp: Long?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = AppSpacing.deviceInfoBottom,
                top = AppSpacing.deviceInfoTop
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DeviceNameText(text = deviceInfo.name)
        SyncStatusText(
            text = if (lastSyncTimestamp != null) {
                formatSyncTimestamp(lastSyncTimestamp)
            } else {
                "Last Sync: -"
            }
        )
    }
}

/**
 * Format the sync timestamp to "Last Sync: YYYYMMDD HH.mm" format.
 */
private fun formatSyncTimestamp(timestamp: Long): String {
    val dateFormat = java.text.SimpleDateFormat("yyyy/MM/dd HH.mm", java.util.Locale.getDefault())
    return "Last Sync: ${dateFormat.format(java.util.Date(timestamp))}"
}

@Composable
fun FlushConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        Dialog(
            showDialog = showDialog,
            onDismissRequest = onDismiss
        ) {
            Alert(
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Delete All Data?",
                            style = AppTypography.dialogTitle,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "This cannot be undone",
                            style = AppTypography.dialogBody,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                negativeButton = {
                    Button(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            modifier = Modifier.size(AppSizes.iconMedium)
                        )
                    }
                },
                positiveButton = {
                    Button(
                        onClick = onConfirm,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            modifier = Modifier.size(AppSizes.iconMedium)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun PermissionPermanentlyDeniedDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (showDialog) {
        Dialog(
            showDialog = showDialog,
            onDismissRequest = onDismiss
        ) {
            Alert(
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Permission Required",
                            style = AppTypography.dialogTitle,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Please enable notifications to enable this feature.",
                            style = AppTypography.dialogBody,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                },
                negativeButton = {
                    Button(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            modifier = Modifier.size(AppSizes.iconMedium)
                        )
                    }
                },
                positiveButton = {
                    Button(
                        onClick = onOpenSettings,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            modifier = Modifier.size(AppSizes.iconMedium)
                        )
                    }
                }
            )
        }
    }
}

