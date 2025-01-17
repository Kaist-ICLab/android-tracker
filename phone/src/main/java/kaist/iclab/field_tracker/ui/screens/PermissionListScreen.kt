package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray50

@Composable
fun PermissionListScreen() {
    val trackerStatus: Boolean = false
    val switchStatus = SwitchStatus(
        isChecked = trackerStatus,
        onCheckedChange = { /*TODO*/ },
        disabled = false
    )

    val switchStatusMap: Map<String, SwitchStatus> = mapOf(
        "Location" to switchStatus,
        "Accessibility Service" to switchStatus,
        "Notification Access" to switchStatus,
        "App Usage Access" to switchStatus,
        "Battery Optimization" to switchStatus,
        "Post Notification" to switchStatus,
        "Network State Access" to switchStatus,
        "Activity Recognition" to switchStatus,
        "Received Boot" to switchStatus,
        "Bluetooth Scan" to switchStatus,
        "Bluetooth Connect" to switchStatus,
        "Read Contact" to switchStatus,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
            .padding(top = 60.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ListCard(
            rows = switchStatusMap.map { (title, switchStatus) ->
                { SettingRow(title, subtitle = "Ready", switchStatus = switchStatus) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionListScreenPreview() {
    PermissionListScreen()
}
