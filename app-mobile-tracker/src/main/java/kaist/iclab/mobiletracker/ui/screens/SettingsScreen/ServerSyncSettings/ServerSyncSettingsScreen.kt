package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.ServerSyncSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.components.Popup.DialogButtonConfig
import kaist.iclab.mobiletracker.ui.components.Popup.PopupDialog
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.settings.ServerSyncSettingsViewModel
import org.koin.androidx.compose.koinViewModel
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.Styles as SettingsStyles

/**
 * Data & Sync Management screen
 */
@Composable
fun ServerSyncSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ServerSyncSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    // Observe state from ViewModel
    val currentTime by viewModel.currentTime.collectAsState()
    val lastWatchData by viewModel.lastWatchData.collectAsState()
    val lastPhoneSensor by viewModel.lastPhoneSensor.collectAsState()
    val lastSuccessfulUpload by viewModel.lastSuccessfulUpload.collectAsState()
    val nextScheduledUpload by viewModel.nextScheduledUpload.collectAsState()
    val dataCollectionStarted by viewModel.dataCollectionStarted.collectAsState()
    val isFlushing by viewModel.isFlushing.collectAsState()

    // Dialog state
    var showFlushDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Styles.HEADER_HEIGHT)
                    .padding(horizontal = Styles.HEADER_HORIZONTAL_PADDING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = context.getString(R.string.menu_server_sync),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Main content - scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = SettingsStyles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            top = 8.dp,
                            end = SettingsStyles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            bottom = SettingsStyles.CARD_VERTICAL_PADDING
                        )
                    )
            ) {
                // Timestamp information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.White),
                    shape = SettingsStyles.CARD_SHAPE
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Styles.CARD_CONTENT_PADDING)
                    ) {
                        // Current Time
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Styles.TEXT_SPACING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_current_time),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentTime,
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }

                        // Last Watch Data Received
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Styles.TEXT_SPACING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_last_watch_data),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lastWatchData ?: "--",
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }

                        // Last Phone Sensor Data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Styles.TEXT_SPACING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_last_phone_sensor),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lastPhoneSensor ?: "--",
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }

                        // Last Successful Upload
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Styles.TEXT_SPACING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_last_successful_upload),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lastSuccessfulUpload ?: "--",
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }

                        // Next Scheduled Upload
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Styles.TEXT_SPACING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_next_scheduled_upload),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = nextScheduledUpload ?: "--",
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }

                        // Data Collection Started
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = context.getString(R.string.sync_data_collection_started),
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dataCollectionStarted ?: "--",
                                fontSize = Styles.TEXT_FONT_SIZE,
                                color = AppColors.TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Styles.BUTTON_SPACING))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Styles.BUTTON_ROW_SPACING)
                ) {
                    // Start data upload button
                    Button(
                        onClick = {
                            // TODO: Implement data upload logic
                        },
                        modifier = Modifier
                            .weight(3f)
                            .height(Styles.BUTTON_HEIGHT),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.PrimaryColor
                        ),
                        shape = RoundedCornerShape(Styles.BUTTON_CORNER_RADIUS)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = AppColors.White,
                                modifier = Modifier.size(Styles.BUTTON_ICON_SIZE)
                            )
                            Spacer(modifier = Modifier.width(Styles.BUTTON_ICON_SPACING))
                            Text(
                                text = context.getString(R.string.sync_start_data_upload),
                                color = AppColors.White,
                                fontSize = Styles.BUTTON_TEXT_SIZE
                            )
                        }
                    }

                    // Delete Data button
                    androidx.compose.material3.Button(
                        onClick = { showFlushDialog = true },
                        modifier = Modifier
                            .weight(2f)
                            .height(Styles.BUTTON_HEIGHT),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = AppColors.ErrorColor
                        ),
                        shape = RoundedCornerShape(Styles.BUTTON_CORNER_RADIUS),
                        enabled = !isFlushing
                    ) {
                        Text(
                            text = context.getString(R.string.sync_delete_data),
                            color = AppColors.White,
                            fontSize = Styles.BUTTON_TEXT_SIZE
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Styles.SECTION_TITLE_SPACING))

                // Watch Sensors Section Title
                Text(
                    text = context.getString(R.string.sensor_watch_sensors),
                    fontSize = Styles.SECTION_TITLE_FONT_SIZE,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = Styles.SECTION_DESCRIPTION_SPACING)
                )

                // Watch Sensors Description
                Text(
                    text = context.getString(R.string.sensor_watch_sensors_description),
                    fontSize = Styles.SECTION_DESCRIPTION_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = Styles.SENSOR_CARD_SPACING)
                )

                // Watch Sensor Cards
                WatchSensorCard(
                    sensorNameRes = R.string.sensor_heart_rate,
                    icon = Icons.Filled.Favorite,
                    lastSyncToServer = lastSuccessfulUpload,
                    lastReceivedToPhone = lastWatchData
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_accelerometer,
                    icon = Icons.Filled.CheckCircle,
                    lastSyncToServer = lastSuccessfulUpload,
                    lastReceivedToPhone = lastWatchData
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_eda,
                    icon = Icons.Filled.SignalCellularAlt,
                    lastSyncToServer = lastSuccessfulUpload,
                    lastReceivedToPhone = lastWatchData
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_ppg,
                    icon = Icons.Filled.ShowChart,
                    lastSyncToServer = lastSuccessfulUpload,
                    lastReceivedToPhone = lastWatchData
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_skin_temperature,
                    icon = Icons.Filled.Thermostat,
                    lastSyncToServer = lastSuccessfulUpload,
                    lastReceivedToPhone = lastWatchData
                )

                Spacer(modifier = Modifier.height(Styles.SECTION_TITLE_SPACING))

                // Phone Sensors Section Title
                Text(
                    text = context.getString(R.string.sensor_phone_sensors),
                    fontSize = Styles.SECTION_TITLE_FONT_SIZE,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = Styles.SECTION_DESCRIPTION_SPACING)
                )

                // Phone Sensors Description
                Text(
                    text = context.getString(R.string.sensor_phone_sensors_description),
                    fontSize = Styles.SECTION_DESCRIPTION_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = Styles.SENSOR_CARD_SPACING)
                )

                // Phone Sensor Cards
                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_ambient_light,
                    icon = Icons.Filled.BrightnessMedium,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_app_list_change,
                    icon = Icons.Filled.List,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_app_usage,
                    icon = Icons.Filled.Apps,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_battery,
                    icon = Icons.Filled.BatteryFull,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_bluetooth_scan,
                    icon = Icons.Filled.Bluetooth,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_call_log,
                    icon = Icons.Filled.History,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_data_traffic,
                    icon = Icons.Filled.DataUsage,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_device_mode,
                    icon = Icons.Filled.PhoneAndroid,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_location,
                    icon = Icons.Filled.LocationOn,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_media,
                    icon = Icons.Filled.PlayArrow,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_message,
                    icon = Icons.AutoMirrored.Filled.Message,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_network_change,
                    icon = Icons.Filled.NetworkCheck,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_notification,
                    icon = Icons.Filled.Notifications,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_screen,
                    icon = Icons.Filled.Phone,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_step,
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_user_interaction,
                    icon = Icons.Filled.TouchApp,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_wifi_scan,
                    icon = Icons.Filled.Wifi,
                    lastSyncToServer = lastPhoneSensor,
                    lastReceivedToPhone = lastPhoneSensor,
                    viewModel = viewModel
                )
            }
        }
    }

    // Flush confirmation dialog
    if (showFlushDialog) {
        PopupDialog(
            title = context.getString(R.string.sync_clear_data_title),
            content = {
                Text(
                    text = context.getString(R.string.sync_clear_data_message),
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary
                )
            },
            primaryButton = DialogButtonConfig(
                text = context.getString(R.string.sync_clear_data_confirm),
                onClick = {
                    viewModel.flushAllData()
                    showFlushDialog = false
                },
                enabled = !isFlushing
            ),
            secondaryButton = DialogButtonConfig(
                text = context.getString(R.string.sync_clear_data_cancel),
                onClick = { showFlushDialog = false },
                isPrimary = false
            ),
            onDismiss = { showFlushDialog = false },
            centerButtons = true
        )
    }
}

/**
 * Reusable watch sensor card component showing sync status
 */
@Composable
private fun WatchSensorCard(
    sensorNameRes: Int,
    icon: ImageVector,
    lastSyncToServer: String?,
    lastReceivedToPhone: String?
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        shape = SettingsStyles.CARD_SHAPE
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Styles.CARD_CONTENT_PADDING)
        ) {
            // Sensor name with icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Styles.SENSOR_CARD_ROW_SPACING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.PrimaryColor,
                    modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                Text(
                    text = context.getString(sensorNameRes),
                    fontSize = Styles.SENSOR_CARD_TITLE_FONT_SIZE,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Last sync to server
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Styles.TEXT_SPACING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                Text(
                    text = context.getString(R.string.sensor_last_sync_server),
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastSyncToServer ?: "--",
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
            }

            // Last received to phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                Text(
                    text = context.getString(R.string.sensor_last_received_phone),
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastReceivedToPhone ?: "--",
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

/**
 * Phone sensor card component showing sync status with delete button
 */
@Composable
private fun PhoneSensorCard(
    sensorNameRes: Int,
    icon: ImageVector,
    lastSyncToServer: String?,
    lastReceivedToPhone: String?,
    viewModel: ServerSyncSettingsViewModel
) {
    val context = LocalContext.current
    val sensorName = context.getString(sensorNameRes)
    val sensorId = viewModel.getSensorId(sensorName)
    val isDeleting by viewModel.deletingSensors.collectAsState()
    val isDeletingThisSensor = sensorId != null && isDeleting.contains(sensorId)

    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        shape = SettingsStyles.CARD_SHAPE
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Styles.CARD_CONTENT_PADDING)
        ) {
            // Sensor name with icon and delete button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Styles.SENSOR_CARD_ROW_SPACING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.PrimaryColor,
                        modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                    )
                    Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                    Text(
                        text = sensorName,
                        fontSize = Styles.SENSOR_CARD_TITLE_FONT_SIZE,
                        color = AppColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete button - only show if sensor has storage
                if (sensorId != null && viewModel.hasStorageForSensor(sensorId)) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !isDeletingThisSensor
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = context.getString(R.string.sensor_delete_data),
                            tint = if (isDeletingThisSensor) AppColors.TextSecondary else AppColors.ErrorColor,
                            modifier = Modifier.size(Styles.DELETE_BUTTON_SIZE)
                        )
                    }
                }
            }

            // Last sync to server
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Styles.TEXT_SPACING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                Text(
                    text = context.getString(R.string.sensor_last_sync_server),
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastSyncToServer ?: "--",
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
            }

            // Last received to phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(Styles.SENSOR_CARD_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Styles.SENSOR_CARD_ROW_SPACING))
                Text(
                    text = context.getString(R.string.sensor_last_received_phone),
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastReceivedToPhone ?: "--",
                    fontSize = Styles.SENSOR_CARD_TIMESTAMP_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && sensorId != null) {
        PopupDialog(
            title = context.getString(R.string.sensor_delete_data_confirm),
            content = {
                androidx.compose.material3.Text(
                    text = context.getString(R.string.sensor_delete_data_message),
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary
                )
            },
            primaryButton = DialogButtonConfig(
                text = context.getString(R.string.sensor_delete_data),
                onClick = {
                    viewModel.deleteSensorData(sensorId)
                    showDeleteDialog = false
                },
                enabled = !isDeletingThisSensor
            ),
            secondaryButton = DialogButtonConfig(
                text = context.getString(R.string.sync_clear_data_cancel),
                onClick = { showDeleteDialog = false },
                isPrimary = false
            ),
            onDismiss = { showDeleteDialog = false },
            centerButtons = true
        )
    }
}
