package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray50

@Composable
fun SettingScreen(
    onNavigateToPermissionList: () -> Unit,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToDataConfig: (name: String) -> Unit,
) {
    val trackerStatus: Boolean = false
    val trackerSwitchStatus = SwitchStatus(
        isChecked = trackerStatus,
        onCheckedChange = { /*TODO*/ },
        disabled = false
    )

    val switchStatusMap: Map<String, SwitchStatus> = mapOf(
        "Ambient Light" to trackerSwitchStatus,
        "User Interaction" to trackerSwitchStatus,
        "Location" to trackerSwitchStatus,
        "Call Log" to trackerSwitchStatus,
        "Message Log" to trackerSwitchStatus,
        "Data Traffic" to trackerSwitchStatus,
        "Notification" to trackerSwitchStatus,
        "App Activity" to trackerSwitchStatus,
        "Screen On/Off" to trackerSwitchStatus,
        "Battery" to trackerSwitchStatus,
        "Ambient WiFi" to trackerSwitchStatus,
        "Ambient Bluetooth" to trackerSwitchStatus,
        "Device Mode" to trackerSwitchStatus,
        "Ambient Audio" to trackerSwitchStatus,
        "Network Status" to trackerSwitchStatus,
        "App Update" to trackerSwitchStatus
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ListCard(
            rows = listOf(
                {
                    SettingRow(
                        "Run Tracker",
                        if (!trackerStatus) "Ready" else "Running",
                        trackerSwitchStatus
                    )
                }
            )
        )
        ListCard(
            title = "Data",
            rows = switchStatusMap.map { (key, value) ->
                { SettingRow(key, switchStatus = value) }
            }
        )

        // Make semi-transparent overlay to indicate not implemented yet
        Box {
            ListCard(
                title = "External Service",
                rows = listOf(
                    { SettingRow("Devices", subtitle = "Galaxy Watch, Polar H10") },
                    { SettingRow("External Apps", subtitle = "Samsung Health, Google Connect") },
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(color = Color.White.copy(alpha= .6F))
            )
        }

        ListCard(
            title = "Profile",
            rows = listOf(
                { SettingRow("User", "testing@ic.kaist.ac.kr") },
                { SettingRow("Experiment Group", "beta-testing") },
            )
        )

        ListCard(
            title = "Server Sync",
            rows = listOf(
                { SettingRow("Network Type", subtitle = "WiFi-only") },
                { SettingRow("Sync Frequency", subtitle = "Do not sync") },
            )
        )

        ListCard(
            title = "Permission",
            rows = listOf(
                { SettingRow("Permissions") },
            )
        )
        ListCard(
            title = "Info",
            rows = listOf(
                { SettingRow("Version", subtitle = "1.0.0") },
                { SettingRow("Device", subtitle = "SM-G991N / R3CR60FGTH") },
                { SettingRow("License") },
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun SettingScreenPreview() {
    SettingScreen({}, {}, {})
}
