package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.components.SettingRow
import kaist.iclab.field_tracker.ui.components.SwitchStatus
import kaist.iclab.field_tracker.ui.theme.Gray50

@Composable
fun UserProfileScreen() {
    val trackerStatus: Boolean = false
    val switchStatus = SwitchStatus(
        isChecked = trackerStatus,
        onCheckedChange = { /*TODO*/ },
        disabled = false
    )

    Box{
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
                    { SettingRow("Name", subtitle = "Uichin Lee") },
                    { SettingRow("Gender", subtitle = "Male") },
                    { SettingRow("Birth date / Age", subtitle = "1976.10.09 / 49") },
                    { SettingRow("Location")},
                    { SettingRow("Logout")},
                )
            )

        }
        Header(title = "User Profile")
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen()
}
