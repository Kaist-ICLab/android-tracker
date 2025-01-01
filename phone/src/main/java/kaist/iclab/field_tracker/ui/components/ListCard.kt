package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListCard(
    rows: List<@Composable () -> Unit>
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            rows.forEachIndexed { index, rowContent ->
                rowContent()
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFCCCCCC),
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ListCardPreview() {
    val rows = listOf<@Composable () -> Unit>(
        { SettingRow(title = "Activity Recognition", switchStatus = SwitchStatus(
            isChecked = true,
            onCheckedChange = { }
        )) },
        { SettingRow(title = "SmartCasting", switchStatus = SwitchStatus(
            isChecked = false,
            onCheckedChange = { }
        )) },
        { SettingRow(title = "Ambient Light", switchStatus = SwitchStatus(
            isChecked = true,
            onCheckedChange = { }
        )) }
    )
    ListCard(rows = rows)
}
