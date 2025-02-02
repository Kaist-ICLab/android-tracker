package kaist.iclab.field_tracker.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.RadioBox
import kaist.iclab.field_tracker.ui.components.SettingEditModalRow
import kaist.iclab.field_tracker.ui.components.SettingNextRow
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SettingSwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray500
import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.auth.User
import kaist.iclab.tracker.auth.UserState
import kaist.iclab.tracker.collector.core.CollectorState

@Composable
fun SettingScreen(
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
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
    Scaffold(
        topBar = {
            Header(
                title = "The Tracker",
                canNavigateBack = canNavigateBack,
                navigateBack = navigateBack
            )
        }
    ) { innerPadding ->
        /*TODO: MODAL 연결 안함...*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
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
                                    Log.d("SettingScreen", "Enable Collector is called")
                                    if (it) enableCollector(name) else disableCollector(name)
                                },
                                disabled = collecterState.flag == CollectorState.FLAG.UNAVAILABLE
                            ),
                            onClick = { onNavigateToDataConfig(name) }
                        )
                    }
                }
            )

            // TODO:  semi-transparent overlay applied to indicate not implemented yet
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
                            subtitle = if(userState.flag == UserState.FLAG.LOGGEDOUT) "Not Loggined" else (userState.user?.email ?: ""),
                            onClick = onNavigateToUserProfile
                        )
                    },
                    {
                        /*TODO: Experiment Group Data Layer*/
                        var experimentGroup by remember { mutableStateOf("None") }
                        var selectedExperimentGroup by remember { mutableStateOf(experimentGroup) }
                        SettingEditModalRow("Experiment Group", experimentGroup, onConfirm = {
                            experimentGroup = selectedExperimentGroup
                        })  {
                            RadioBox(
                                listOf("None", "Group A", "Group B", "Group C"),
                                selectedExperimentGroup,
                                onOptionSelected = { selectedExperimentGroup = it}
                            )
                        }
                    },
                )
            )

            ListCard(
                title = "Server Sync",
                rows = listOf(
//                { SettingEditRow("Network Type", subtitle = "WiFi-only", onButtonClick = {}) },
                    {
//                    TODO: Network Type 지정하는 Data Layer
                        var networkType by remember { mutableStateOf("WiFi-only") }
                        var selectedNetworkType by remember { mutableStateOf(networkType) }
                        SettingEditModalRow("Network Type", networkType, onConfirm = {
                            networkType = selectedNetworkType
                        })  {
                            RadioBox(
                                listOf("WiFi-only", "WiFi + Mobile Data"),
                                selectedNetworkType,
                                onOptionSelected = { selectedNetworkType = it}
                            )
                        }
                    },
                    {
//                    TODO: Sync Freq 지정하는 Data Layer
                        var syncFreq by remember { mutableStateOf("Do not Sync") }
                        var selectedSyncFreq by remember { mutableStateOf(syncFreq) }
                        SettingEditModalRow("Sync Frequency", syncFreq, onConfirm = {
                            syncFreq = selectedSyncFreq
                        })  {
                            RadioBox(
                                listOf("Do not Sync", "Sync every hour", "Sync every 2 hours"),
                                selectedSyncFreq,
                                onOptionSelected = { selectedSyncFreq = it}
                            )
                        }
                    },
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

}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun SettingScreenPreview() {
    SettingScreen(
        canNavigateBack = false,
        navigateBack = {},
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
