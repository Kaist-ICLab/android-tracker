package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray50
import kaist.iclab.field_tracker.ui.theme.Gray500

@Composable
fun DataConfigScreen(name: String, permissions: List<String>) {
    val trackerStatus: Boolean = false
    val switchStatus = SwitchStatus(
        isChecked = trackerStatus,
        onCheckedChange = { /*TODO*/ },
        disabled = false
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
            rows = listOf(
                { SettingRow("Status", subtitle = "Ready", switchStatus = switchStatus) },
            )
        )

        ListCard(title = "Permissions",
            rows = if (permissions.size == 0) listOf(
                {
                    Text(
                        "No permissions required",
                        color = Gray500,
                        fontSize = 9.sp
                    )
                }
            )
            else permissions.map { permission ->
                { SettingRow(permission, subtitle = "Ready", switchStatus = switchStatus) }
            }
        )

        ListCard(
            title = "configs",
            rows = listOf(
                { SettingRow("Update Period", switchStatus = switchStatus) },

                )
        )
        ListCard(
            title = "stats",
            rows = listOf(
                { SettingRow("Last Record", subtitle = "2025-01-01 13:02:23 (UTC+0900)") },
                { SettingRow("Records", subtitle = "1,400 Records") },
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DataConfigScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DataConfigScreen("Location", listOf("Location"))
    }
}