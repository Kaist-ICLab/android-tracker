package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.WatchCollectedDataSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.WatchSensorCard
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.settings.DataSyncSettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Watch Sensors Settings screen
 */
@Composable
fun WatchCollectedDataSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DataSyncSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lastWatchData by viewModel.lastWatchData.collectAsState()

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
                    text = context.getString(R.string.sensor_watch_sensors),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Description text
            Text(
                text = context.getString(R.string.sensor_watch_sensors_description),
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

                // Watch Sensor Cards
                WatchSensorCard(
                    sensorNameRes = R.string.sensor_heart_rate,
                    icon = Icons.Filled.Favorite,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_accelerometer,
                    icon = Icons.Filled.CheckCircle,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_eda,
                    icon = Icons.Filled.SignalCellularAlt,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_ppg,
                    icon = Icons.Filled.ShowChart,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_skin_temperature,
                    icon = Icons.Filled.Thermostat,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(Styles.SENSOR_CARD_SPACING))

                WatchSensorCard(
                    sensorNameRes = R.string.sensor_location,
                    icon = Icons.Filled.LocationOn,
                    lastReceivedToPhone = lastWatchData,
                    viewModel = viewModel
                )
            }
        }
    }
}

