package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingSwitchRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.tracker.permission.PermissionState

@Composable
fun PermissionListScreen(
    permissionMap: Map<String, PermissionState>,
    onPermissionStateChange: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ListCard(
            rows = permissionMap.map { (name, permissionState) ->
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
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionListScreenPreview() {
    PermissionListScreen(
        mapOf(
            "Location" to PermissionState.GRANTED,
            "Activity" to PermissionState.PERMANENTLY_DENIED,
            "Microphone" to PermissionState.RATIONALE_REQUIRED
        ), { _, _ -> }
    )
}
