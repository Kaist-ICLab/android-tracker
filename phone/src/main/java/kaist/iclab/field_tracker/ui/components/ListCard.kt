package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.theme.Blue600
import kaist.iclab.field_tracker.ui.theme.Gray50

@Composable
fun ListCard(
    title: String? = null,
    rows: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (title != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                text = title.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Blue600,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            rows.forEachIndexed { index, rowContent ->
                rowContent()
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        color = Gray50,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF9FAFB)
@Composable
fun ListCardPreview() {
    val switchStatus = SwitchStatus(
        isChecked = true,
        onCheckedChange = { },
        disabled = false
    )
    val rows = listOf<@Composable () -> Unit>(
        {
            SettingRow(
                title = "Activity Recognition",
                switchStatus = switchStatus
            )
        },{
            SettingRow(
                title = "Location",
                switchStatus = switchStatus
            )
        }
    )
    Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
        ListCard(title = "Collectors", rows = rows)
        ListCard(rows = rows)
    }
}
