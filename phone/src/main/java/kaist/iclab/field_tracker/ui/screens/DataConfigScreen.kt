package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingEditRow
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SettingSwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray500
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.permission.PermissionState

@Composable
fun DataConfigScreen(
    permissionMap: Map<String, PermissionState>,
    onPermissionStateChange: (String, Boolean) -> Unit,
    collectorState: CollectorState,
    enableCollector: () -> Unit,
    disableCollector: () -> Unit,
    collectorConfig: Map<String, Any>,
    onConfigChange: (String, Any) -> Unit,
    lastUpdated: String,
    recordCount: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ListCard(
            rows = listOf(
                {
                    SettingSwitchRow(
                        "Status",
                        subtitle = if (collectorState.flag == CollectorState.FLAG.UNAVAILABLE) collectorState.message else null,
                        switchStatus = SwitchStatus(
                            isChecked = collectorState.flag == CollectorState.FLAG.ENABLED || collectorState.flag == CollectorState.FLAG.RUNNING,
                            onCheckedChange = {
                                if (it) enableCollector() else disableCollector()
                            },
                            disabled = collectorState.flag == CollectorState.FLAG.UNAVAILABLE
                        ),
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
                    SettingSwitchRow(
                        name,
                        subtitle = permissionState.toString(),
                        switchStatus = SwitchStatus(
                            isChecked = permissionState == PermissionState.GRANTED,
                            onCheckedChange = { onPermissionStateChange(name, it) },
                            disabled = permissionState == PermissionState.PERMANENTLY_DENIED
                        )
                    )
                }
            }
        )

        ListCard(
            title = "configs",
            rows = collectorConfig.map { (name, value) ->
                { SettingEditRow(
                    title = name,
                    subtitle = value.toString(),
                    onButtonClick = { /*TODO - Modal로 Config Change*/ }
                ) }
            }

        )
        ListCard(
            title = "stats",
            rows = listOf(
                { SettingRow("Last Record", subtitle = lastUpdated) },
                { SettingRow("Records", subtitle = "${recordCount} Records") },
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DataConfigScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DataConfigScreen(
            mapOf(
                "Location" to PermissionState.GRANTED,
                "Activity" to PermissionState.PERMANENTLY_DENIED,
                "Microphone" to PermissionState.RATIONALE_REQUIRED
            ), { _, _ -> },
            CollectorState(CollectorState.FLAG.ENABLED, "Collector is running"),
            { },
            { },
            mapOf(
                "Config 1" to "Value 1",
                "Config 2" to "Value 2",
                "Config 3" to "Value 3",
            ),
            { _, _ -> },
            "2021-09-01 12:00:00",
            100
        )
    }
}