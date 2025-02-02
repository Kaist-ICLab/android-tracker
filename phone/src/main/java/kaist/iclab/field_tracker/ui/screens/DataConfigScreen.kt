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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.NumberInput
import kaist.iclab.field_tracker.ui.components.SettingEditModalRow
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SettingSwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.components.toDuration
import kaist.iclab.field_tracker.ui.theme.Gray500
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.phone.SampleCollector
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


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
    SettingSwitchRow(
        "Status",
        subtitle = when (collectorState.flag) {
            CollectorState.FLAG.UNAVAILABLE -> collectorState.message
            CollectorState.FLAG.ENABLED -> null
            CollectorState.FLAG.DISABLED -> null
            CollectorState.FLAG.RUNNING -> "Tracker is running. Please turn off the tracker to change configuration."
//            CollectorState.FLAG.PERMISSION_REQUIRED -> "Permission Required. Please grant the permission first."
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
//                CollectorState.FLAG.PERMISSION_REQUIRED
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
    onPermissionRequest: (Array<String>, (Boolean)-> Unit) -> Unit,
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
                            enable = {
                                onPermissionRequest(collector.permissions){
                                    if(it) collector.enable()
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
                            color = Gray500,
                            fontSize = 9.sp
                        )
                    }
                )
                else permissionMap.map { (name, permissionState) ->
                    {
                        val permission = permissions.find{ it.ids[0] == name } ?: error("Permission not found")
                        PermissionStateSwitchRow(
                            permission,
                            permissionState,
                            onPermissionRequest = {names -> onPermissionRequest(names, {}) }
                        )
//                        SettingSwitchRow(
//                            name,
//                            subtitle = permissionState.toString(),
//                            switchStatus = SwitchStatus(
//                                isChecked = permissionState == PermissionState.GRANTED,
//                                onCheckedChange = { if (it) onPermissionRequest(arrayOf(name)) },
//                                disabled = permissionState == PermissionState.PERMANENTLY_DENIED
//                            )
//                        )
                    }
                }
            )

            ListCard(
                title = "configs",
                rows = collector.configClass.memberProperties.map { property ->
                    {
                        val curValue = property.getter.call(collectorConfig)?.toString()
                        var changedValue by remember { mutableStateOf(curValue) }
                        SettingEditModalRow(
                            title = property.name,
                            subtitle = curValue,
                            enabled = !(collectorState.flag in setOf(
                                CollectorState.FLAG.RUNNING,
                            )),
                            onConfirm = {
                                val constructor = collector.configClass.primaryConstructor
                                constructor?.let {
                                    val newConfig = it.callBy(
                                        it.parameters.associateWith { parameter ->
                                            when (parameter.name) {
                                                property.name -> when (property.returnType.classifier) {
                                                    Int::class -> changedValue?.toInt()
                                                    Long::class -> changedValue?.toLong()
                                                    else -> error("Not supported type")
                                                }
                                                else -> property.getter.call(collectorConfig)
                                            }
                                        }
                                    )
                                    Log.d("DataConfigScreen", "NewConfig: $newConfig")
                                    collector.updateConfig(newConfig)
                                }
                            }
                        ) {
                            when (property.returnType.classifier) {
                                in setOf(Int::class, Long::class) ->
                                    NumberInput(
                                        value = changedValue.toString(),
                                        onValueChange = { changedValue = it },
                                        placeholder = "Enter a value",
                                        labelFormatter = { it.toDuration() }
                                    )
                            }
                        }
                    }
                }
            )
            Box {
                /*TODO: Implement*/
                ListCard(
                    title = "stats",
                    rows = listOf(
                        {
                            val lastUpdated by remember { mutableStateOf("2025-01-30 12:43:21 (a minute ago)") }
                            SettingRow("Last Record", subtitle = lastUpdated)
                        },
                        {
                            val recordCount by remember { mutableStateOf("1,000") }
                            SettingRow("Records", subtitle = "${recordCount} Records")
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

@Preview(showBackground = true)
@Composable
fun DataConfigScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DataConfigScreen(
            onPermissionRequest = { _,_ -> },
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
            navigateBack = {}
        )
    }
}