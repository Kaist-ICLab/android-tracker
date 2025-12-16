package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.PhoneCollectedDataSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.PhoneSensorCard
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.PhoneCollectedDataSettings.Styles
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.settings.DataSyncSettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Phone Sensors Settings screen
 */
@Composable
fun PhoneCollectedDataSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DataSyncSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

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
                    text = context.getString(R.string.sensor_phone_sensors),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Description text
            Text(
                text = context.getString(R.string.sensor_phone_sensors_description),
                color = AppColors.TextSecondary,
                fontSize = Styles.SCREEN_DESCRIPTION_FONT_SIZE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Styles.SCREEN_DESCRIPTION_HORIZONTAL_PADDING,
                        end = Styles.SCREEN_DESCRIPTION_HORIZONTAL_PADDING,
                        bottom = Styles.SCREEN_DESCRIPTION_BOTTOM_PADDING
                    )
            )

            // Main content - scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = Styles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            top = 8.dp,
                            end = Styles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            bottom = Styles.CARD_VERTICAL_PADDING
                        )
                    )
            ) {

                // Phone Sensor Cards
                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_ambient_light,
                    icon = Icons.Filled.BrightnessMedium,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_app_list_change,
                    icon = Icons.Filled.List,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_app_usage,
                    icon = Icons.Filled.Apps,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_battery,
                    icon = Icons.Filled.BatteryFull,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_bluetooth_scan,
                    icon = Icons.Filled.Bluetooth,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_call_log,
                    icon = Icons.Filled.History,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_connectivity,
                    icon = Icons.Filled.NetworkCheck,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_data_traffic,
                    icon = Icons.Filled.DataUsage,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_device_mode,
                    icon = Icons.Filled.PhoneAndroid,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_location,
                    icon = Icons.Filled.LocationOn,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_media,
                    icon = Icons.Filled.PlayArrow,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_message,
                    icon = Icons.AutoMirrored.Filled.Message,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_connectivity,
                    icon = Icons.Filled.NetworkCheck,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_notification,
                    icon = Icons.Filled.Notifications,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_screen,
                    icon = Icons.Filled.Phone,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_step,
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_user_interaction,
                    icon = Icons.Filled.TouchApp,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                PhoneSensorCard(
                    sensorNameRes = R.string.sensor_wifi_scan,
                    icon = Icons.Filled.Wifi,
                    viewModel = viewModel
                )
            }
        }
    }
}

