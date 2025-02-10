package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.BaseRow
import kaist.iclab.field_tracker.ui.components.DurationInputModalRow
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.MainTheme
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.phone.SampleCollector
import kaist.iclab.tracker.permission.Permission
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties


data class CollectorData(
    val name: String,
    val permissions: Array<String>,
    val stateFlow: StateFlow<CollectorState>,
    val configFlow: StateFlow<CollectorConfig>,
    val updateConfig: (CollectorConfig) -> Unit,
    val enable: () -> Unit,
    val disable: () -> Unit,
    val configClass: KClass<out CollectorConfig>
)

fun CollectorInterface.toCollectorData(): CollectorData {
    return CollectorData(
        stateFlow = this.stateFlow,
        configFlow = this.configFlow,
        enable = { this.enable() },
        disable = { this.disable() },
        configClass = this.getConfigClass(),
        name = this.NAME,
        updateConfig = { this.updateConfig(it) },
        permissions = this.permissions
    )
}

@Composable
fun CollectorSwitchRow(
    title: String,
    collectorState: CollectorState,
    enable: () -> Unit,
    disable: () -> Unit
) {
    SwitchRow(
        title,
        subtitle = when (collectorState.flag) {
            CollectorState.FLAG.UNAVAILABLE -> collectorState.message
            CollectorState.FLAG.ENABLED -> null
            CollectorState.FLAG.DISABLED -> null
            CollectorState.FLAG.RUNNING -> "Tracker is running. Please turn off the tracker to change configuration."
            else -> null
        },
        switchStatus = SwitchStatus(
            isChecked = collectorState.flag in setOf(
                CollectorState.FLAG.ENABLED,
                CollectorState.FLAG.RUNNING
            ),
            onCheckedChange = {
                if (collectorState.flag in setOf(
                        CollectorState.FLAG.ENABLED,
                        CollectorState.FLAG.DISABLED
                    )
                ) {
                    if (it) enable() else disable()
                }
            },
            disabled = collectorState.flag in setOf(
                CollectorState.FLAG.UNAVAILABLE,
                CollectorState.FLAG.RUNNING,
            )
        ),
    )
}

@Composable
fun DataConfigScreen(
    collector: CollectorData,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
    permissionMap: Map<String, PermissionState>,
    onPermissionRequest: (Array<String>, (Boolean) -> Unit) -> Unit,
    lastUpdated: String,
    recordCount: String
    /*TODO: DataLayer Stat 처리: lastUpdated, record Count*/
) {
    val collectorState = collector.stateFlow.collectAsState().value
    val collectorConfig = collector.configFlow.collectAsState().value

    Scaffold(
        topBar = {
            Header(
                title = "${collector.name} configuration",
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
                rows = listOf(
                    {
                        CollectorSwitchRow(
                            title = "Status",
                            collectorState = collectorState,
                            /*TODO: Should we remove this simple logic too? -> YES*/
                            enable = {
                                onPermissionRequest(collector.permissions) {
                                    if (it) collector.enable()
                                }
                            },
                            disable = collector.disable
                        )
                    },
                )
            )

            ListCard(title = "Permissions",
                rows = if (permissionMap.size == 0) listOf(
                    {
                        Text(
                            "No permissions required",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                )
                else permissionMap.map { (name, permissionState) ->
                    {
                        val permission =
                            Permission.supportedPermissions.find { it.ids[0] == name } ?: error("Permission not found")
                        PermissionStateSwitchRow(
                            permission,
                            permissionState,
                            onPermissionRequest = { names -> onPermissionRequest(names, {}) }
                        )
                    }
                }
            )

            ListCard(
                title = "configs",
                rows = collector.configClass.memberProperties.map { property ->
                    {
                        val curValue = property.getter.call(collectorConfig)?.toString()
                            ?: error("Value for ${property.name} is null")
                        when (property.returnType.classifier) {
                            /*TODO: Change it to more specific type (e.g., duration, category) considering formatting*/
                            in setOf(Int::class, Long::class) -> {
                                DurationInputModalRow(
                                    title = property.name,
                                    curValue = curValue,
                                    onValueChanged = {
                                        collector.updateConfig(
                                            collectorConfig.copy(
                                                property.name,
                                                it
                                            )
                                        )
                                    },
                                    enabled = !(collectorState.flag in setOf(
                                        CollectorState.FLAG.RUNNING,
                                    ))
                                )
                            }
                        }
                    }
                }
            )
            Box {
                ListCard(
                    title = "stats",
                    rows = listOf(
                        {
                            BaseRow("Last Record", subtitle = lastUpdated)
                        },
                        {
                            BaseRow("Records", subtitle = "${recordCount} Records")
                        },
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(color = Color.White.copy(alpha = .6F))
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataConfigScreenPreview() {
    MainTheme {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            DataConfigScreen(
                onPermissionRequest = { _, _ -> },
                permissionMap = mapOf(
                    "Location" to PermissionState.GRANTED,
                    "Activity" to PermissionState.PERMANENTLY_DENIED,
                    "Microphone" to PermissionState.RATIONALE_REQUIRED
                ),
                collector = CollectorData(
                    stateFlow = MutableStateFlow(CollectorState(CollectorState.FLAG.ENABLED)),
                    configFlow = MutableStateFlow(SampleCollector.Config(1000)),
                    enable = {},
                    disable = {},
                    configClass = SampleCollector.Config::class,
                    name = "SampleCollector",
                    updateConfig = {},
                    permissions = arrayOf("Location", "Activity", "Microphone")
                ),
                canNavigateBack = true,
                navigateBack = {},
                lastUpdated = "2021-01-01 12:00:00 (UTC+0000)",
                recordCount = "1000"
            )
        }
    }
}