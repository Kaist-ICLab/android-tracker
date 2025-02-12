package kaist.iclab.field_tracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.BaseRow
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.NavigationRow
import kaist.iclab.field_tracker.ui.components.SelectOptionModalRow
import kaist.iclab.field_tracker.ui.components.SwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.MainTheme
import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.auth.User
import kaist.iclab.tracker.auth.UserState
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

@Composable
fun SettingScreen(
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
    onNavigateToPermissionList: () -> Unit,
    onNavigateToUserProfile: () -> Unit,
    onNavigateToDataConfig: (name: String) -> Unit,
    trackerState: TrackerState,
    onTrackerStateChange: (Boolean) -> Unit,
    collectorMap: Map<String, Collector>,
    enableCollector: (String) -> Unit,
    disableCollector: (String) -> Unit,
    permissionMap: Map<String, PermissionState>,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ListCard(
                rows = listOf({
                    SwitchRow(
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
                rows = collectorMap.map { (name, collector) ->
                    {
//                        SwitchRow(
//                            name,
//                            subtitle = if (collecterState.flag == CollectorState.FLAG.UNAVAILABLE) collecterState.message else null,
//                            switchStatus = SwitchStatus(
//                                isChecked = collecterState.flag == CollectorState.FLAG.ENABLED || collecterState.flag == CollectorState.FLAG.RUNNING,
//                                onCheckedChange = {
//                                    Log.d("SettingScreen", "Enable Collector is called")
//                                    if (it) enableCollector(name) else disableCollector(name)
//                                },
//                                disabled = collecterState.flag == CollectorState.FLAG.UNAVAILABLE
//                            ),
//                            onClick = { onNavigateToDataConfig(name) }
//                        )
                        val warning = Toast.makeText(LocalContext.current, "Please grant all permissions", Toast.LENGTH_SHORT)
                        CollectorSwitchRow(
                            name,
                            collector.collectorStateFlow.value,
                            enable = {
                                val isGranted =
                                    permissionMap.filter { it.key in collector.permissions }
                                        .all { it.value == PermissionState.GRANTED }
                                if (isGranted) {
                                    enableCollector(name)
                                } else {
                                    warning.show()
                                }
                            },
                            disable = { disableCollector(name) },
                            onClick = { onNavigateToDataConfig(name) }
                        )
                    }
                }
            )

            ListCard(
                title = "Permission",
                rows = listOf(
                    { NavigationRow("Permissions", onClick = onNavigateToPermissionList) },
                )
            )
            ListCard(
                title = "Info",
                rows = listOf(
                    { BaseRow("App Version", subtitle = appVersion) },
                    { BaseRow("Device", subtitle = deviceInfo) },
                    {
                        BaseRow("License", showDivider = true) {
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = "Info",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                )
            )

            /*TODO: How to integrate external services*/
            ListCard(
                title = "External Service",
                rows = listOf(
                    { BaseRow("Devices", subtitle = "Galaxy Watch, Polar H10") },
                    { BaseRow("External Apps", subtitle = "Samsung Health, Google Connect") },
                ),
                disabled = true
            )

            ListCard(
                title = "Profile",
                rows = listOf(
                    {
                        NavigationRow(
                            "User",
                            subtitle = if (userState.flag == UserState.FLAG.LOGGEDOUT) "Not Loggined" else (userState.user?.email
                                ?: ""),
                            onClick = onNavigateToUserProfile
                        )
                    },
                    {
                        var experimentGroup by remember { mutableStateOf("None") }
                        val experimentGroups = listOf("None", "Group A", "Group B", "Group C")
                        /*TODO: Consider Loading for network delay*/
                        SelectOptionModalRow(
                            "Experiment Group",
                            experimentGroup,
                            experimentGroups,
                            onOptionSelected = { experimentGroup = it }
                        )
                    },
                ),
                disabled = true
            )

            ListCard(
                title = "Server Sync",
                rows = listOf(
                    /*TODO: Connect w/ Server sync data layer*/
                    {
                        var networkType by remember { mutableStateOf("WiFi-only") }
                        val networkTypes = listOf("WiFi-only", "WiFi + Mobile Data")
                        SelectOptionModalRow(
                            "Network Type",
                            networkType,
                            networkTypes,
                            onOptionSelected = { networkType = it }
                        )
                    },
                    {
                        var syncFreq by remember { mutableStateOf("Do not Sync") }
                        val syncFreqCandidates =
                            listOf("Do not Sync", "Sync every hour", "Sync every 2 hours")
                        SelectOptionModalRow(
                            "Sync Frequency",
                            syncFreq,
                            syncFreqCandidates,
                            onOptionSelected = { syncFreq = it }
                        )
                    },
                ),
                disabled = true
            )
        }
    }

}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun SettingScreenPreview() {
    class TestCollector: Collector{
        override val ID: String
            get() = TODO("Not yet implemented")
        override val NAME: String
            get() = TODO("Not yet implemented")
        override val permissions: Array<String>
            get() = TODO("Not yet implemented")
        override val foregroundServiceTypes: Array<Int>
            get() = TODO("Not yet implemented")
        override val _defaultConfig: CollectorConfig
            get() = TODO("Not yet implemented")
        override val configStateFlow: StateFlow<CollectorConfig>
            get() = TODO("Not yet implemented")
        override val configClass: KClass<out CollectorConfig>
            get() = TODO("Not yet implemented")

        override fun updateConfig(config: CollectorConfig) {
            TODO("Not yet implemented")
        }

        override fun resetConfig() {
            TODO("Not yet implemented")
        }

        override val collectorStateFlow: StateFlow<CollectorState>
            get() = TODO("Not yet implemented")

        override fun init() {
            TODO("Not yet implemented")
        }

        override fun enable() {
            TODO("Not yet implemented")
        }

        override fun disable() {
            TODO("Not yet implemented")
        }

        override fun start() {
            TODO("Not yet implemented")
        }

        override fun stop() {
            TODO("Not yet implemented")
        }

        override val entityClass: KClass<out DataEntity>
            get() = TODO("Not yet implemented")

        override fun addListener(listener: (DataEntity) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun removeListener(listener: (DataEntity) -> Unit) {
            TODO("Not yet implemented")
        }
    }

    MainTheme {
        SettingScreen(
            canNavigateBack = false,
            navigateBack = {},
            onNavigateToPermissionList = {},
            onNavigateToUserProfile = {},
            onNavigateToDataConfig = {},
            trackerState = TrackerState(TrackerState.FLAG.DISABLED, "Disabled"),
            onTrackerStateChange = {},
            collectorMap = mapOf(
                "Location" to TestCollector(),
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
            permissionMap = mapOf(
                "android.permission.ACCESS_FINE_LOCATION" to PermissionState.GRANTED,
                "android.permission.ACTIVITY_RECOGNITION" to PermissionState.GRANTED,
            ),
            deviceInfo = "SM-G991N",
            appVersion = "1.0.0"
        )
    }
}
