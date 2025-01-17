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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.CustomSwitch
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingEditRow
import kaist.iclab.field_tracker.ui.components.SettingNextRow
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SettingSwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray50
import kaist.iclab.field_tracker.ui.theme.Gray500

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
            rows = listOf({
                SettingSwitchRow(
                    title = "Run Tracker",
                    subtitle = if (!trackerStatus) "Ready" else "Running",
                    switchStatus = trackerSwitchStatus
                )
            })
        )
        ListCard(
            title = "Data",
            rows = switchStatusMap.map { (title, switchStatus) ->
                { SettingSwitchRow(title, subtitle = "Ready", switchStatus) }
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
                    .background(color = Color.White.copy(alpha = .6F))
            )
        }

        ListCard(
            title = "Profile",
            rows = listOf(
                {
                    SettingNextRow(
                        "User",
                        "testing@ic.kaist.ac.kr",
                        onClick = onNavigateToUserProfile
                    )
                },
                { SettingEditRow("Experiment Group", "beta-testing", onButtonClick = {}) },
            )
        )

        ListCard(
            title = "Server Sync",
            rows = listOf(
                { SettingEditRow("Network Type", subtitle = "WiFi-only", onButtonClick = {}) },
                { SettingEditRow("Sync Frequency", subtitle = "Do not sync", onButtonClick = {}) },
            )
        )

        ListCard(
            title = "Permission",
            rows = listOf(
                { SettingNextRow("Permissions", onClick = onNavigateToPermissionList) },
            )
        )
        ListCard(
            title = "Info",
            rows = listOf(
                { SettingRow("Version", subtitle = "1.0.0") },
                { SettingRow("Device", subtitle = "SM-G991N / R3CR60FGTH") },
                {
                    SettingRow("License", showDivider = true) {
                        IconButton(
                            modifier = Modifier.size(48.dp),
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "Info",
                                tint = Gray500,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun SettingScreenPreview() {
    SettingScreen({}, {}, {})
}
