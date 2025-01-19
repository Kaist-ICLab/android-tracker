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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.TrackerState
import kaist.iclab.field_tracker.ui.User
import kaist.iclab.field_tracker.ui.UserState
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
import kaist.iclab.tracker.collector.core.CollectorState

@Composable
fun SettingScreen(
    onNavigateToPermissionList: () -> Unit,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToDataConfig: (name: String) -> Unit,
    trackerState: TrackerState,
    onTrackerStateChange: (Boolean) -> Unit,
    collectorMap: Map<String, CollectorState>,
    enableCollector: (String) -> Unit,
    disableCollector: (String) -> Unit,
    userState: UserState,
    deviceInfo: String,
    appVersion: String
) {
    /*TODO: MODAL 연결 안함...*/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ListCard(
            rows = listOf({
                SettingSwitchRow(
                    title = "Run Tracker",
                    subtitle = if (trackerState.flag == TrackerState.FLAG.DISABLED) trackerState.message else trackerState.flag.toString(),
                    switchStatus = SwitchStatus(
                        /*TODO*/
                        isChecked = trackerState.flag == TrackerState.FLAG.RUNNING,
                        onCheckedChange = onTrackerStateChange,
                        disabled = trackerState.flag == TrackerState.FLAG.DISABLED
                    )
                )
            })
        )
        ListCard(
            title = "Data",
            rows = collectorMap.map { (name, collecterState) ->
                {
                    SettingSwitchRow(
                        name,
                        subtitle = if (collecterState.flag == CollectorState.FLAG.UNAVAILABLE) collecterState.message else null,
                        switchStatus = SwitchStatus(
                            isChecked = collecterState.flag == CollectorState.FLAG.ENABLED || collecterState.flag == CollectorState.FLAG.RUNNING,
                            onCheckedChange = {
                                if (it) enableCollector(name) else disableCollector(
                                    name
                                )
                            },
                            disabled = collecterState.flag == CollectorState.FLAG.UNAVAILABLE
                        ),
                        onClick = { onNavigateToDataConfig(name) }
                    )
                }
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
                        subtitle = userState.user?.email ?: "None",
                        onClick = onNavigateToUserProfile
                    )
                },
                {
                    SettingEditRow(
                        "Experiment Group",
                        subtitle = userState.user?.experimentGroup ?: "None",
                        onButtonClick = {/*TODO*/ })
                },
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
                { SettingRow("Version", subtitle = appVersion) },
                { SettingRow("Device", subtitle = deviceInfo) },
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
    SettingScreen(
        onNavigateToPermissionList = {},
        onNavigateToUserProfile = {},
        onNavigateToDataConfig = {},
        trackerState = TrackerState(TrackerState.FLAG.DISABLED, "Disabled"),
        onTrackerStateChange = {},
        collectorMap = mapOf(
            "Location" to CollectorState(CollectorState.FLAG.ENABLED, "Enabled"),
            "Activity" to CollectorState(CollectorState.FLAG.RUNNING, "Running"),
            "Notification" to CollectorState(CollectorState.FLAG.UNAVAILABLE, "Unavailable"),
        ),
        enableCollector = {},
        disableCollector = {},
        userState = UserState(
            UserState.FLAG.LOGGEDIN, User(
                name = "John Doe",
                gender = "Male",
                email = "john.doe@example",
                birthDate = "1990-01-01",
                age = 31,
            )
        ),
        deviceInfo = "SM-G991N",
        appVersion = "1.0.0"
    )
}
