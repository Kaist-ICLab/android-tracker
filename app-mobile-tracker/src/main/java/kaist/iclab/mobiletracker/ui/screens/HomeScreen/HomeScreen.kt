package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = Styles.SCREEN_HORIZONTAL_PADDING),
        verticalArrangement = Arrangement.spacedBy(Styles.SCREEN_VERTICAL_SPACING)
    ) {
        Spacer(modifier = Modifier.height(Styles.TOP_SPACER_HEIGHT))

        // 1. Greeting Section
        Column {
            Text(
                text = stringResource(R.string.home_hello, uiState.userName ?: ""),
                fontSize = Styles.GREETING_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Text(
                text = stringResource(R.string.home_greeting_subtitle),
                fontSize = Styles.GREETING_SUBTITLE_FONT_SIZE,
                color = AppColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Styles.GREETING_SUBTITLE_TOP_PADDING)
            )
        }

        // 2. Tracking Status Card
        TrackingStatusCard(
            isActive = uiState.isTrackingActive,
            lastSyncedTime = uiState.lastSyncedTime
        )

        // 3. Grid Section Title
        Text(
            text = stringResource(R.string.home_daily_highlights),
            fontSize = Styles.GRID_SECTION_TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(top = Styles.GRID_SECTION_TITLE_TOP_PADDING)
        )

        // 4. Highlight Grid (8 cards)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Styles.GRID_VERTICAL_SPACING)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Styles.GRID_HORIZONTAL_SPACING)
            ) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_location_label),
                    value = stringResource(R.string.home_logs_unit, uiState.locationCount),
                    icon = Icons.Default.Place,
                    iconColor = Styles.Colors.LOCATION
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_app_usage_label),
                    value = stringResource(R.string.home_logs_unit, uiState.appUsageCount),
                    icon = Icons.Default.GridView,
                    iconColor = Styles.Colors.APP_USAGE
                )
            }
            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Styles.GRID_HORIZONTAL_SPACING)
            ) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_activity_label),
                    value = stringResource(R.string.home_samples_unit, uiState.activityCount),
                    icon = Icons.Default.DirectionsWalk,
                    iconColor = Styles.Colors.ACTIVITY
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_device_status_label),
                    value = stringResource(R.string.home_logs_unit, uiState.batteryCount),
                    icon = Icons.Default.BatteryChargingFull,
                    iconColor = Styles.Colors.DEVICE_STATUS
                )
            }
            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Styles.GRID_HORIZONTAL_SPACING)
            ) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_notifications_label),
                    value = stringResource(R.string.home_notifications_unit, uiState.notificationCount),
                    icon = Icons.Default.Notifications,
                    iconColor = Styles.Colors.NOTIFICATIONS
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_screen_label),
                    value = stringResource(R.string.home_events_unit, uiState.screenCount),
                    icon = Icons.Default.StayCurrentPortrait,
                    iconColor = Styles.Colors.SCREEN
                )
            }
            // Row 4
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Styles.GRID_HORIZONTAL_SPACING)
            ) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_connectivity_label),
                    value = stringResource(R.string.home_updates_unit, uiState.connectivityCount),
                    icon = Icons.Default.Wifi,
                    iconColor = Styles.Colors.CONNECTIVITY
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_bluetooth_label),
                    value = stringResource(R.string.home_scans_unit, uiState.bluetoothCount),
                    icon = Icons.Default.Bluetooth,
                    iconColor = Styles.Colors.BLUETOOTH
                )
            }
        }

        Spacer(modifier = Modifier.height(Styles.BOTTOM_SPACER_HEIGHT))
    }
}
