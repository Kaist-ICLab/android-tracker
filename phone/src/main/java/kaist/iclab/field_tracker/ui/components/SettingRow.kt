package kaist.iclab.field_tracker.ui.components

import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.theme.Gray50
import kaist.iclab.field_tracker.ui.theme.Gray500

data class SwitchStatus(
    val isChecked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val disabled: Boolean
)

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    switchStatus: SwitchStatus? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if(switchStatus!= null) Arrangement.SpaceBetween else Arrangement.Start
    ) {
        Column(
            verticalArrangement = if(subtitle!= null) Arrangement.spacedBy(2.dp) else Arrangement.Center
        ){
            Text(
                text = title,
                fontSize = 12.sp
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 9.sp,
                    color = Gray500
                )
            }
        }
        switchStatus?.let{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(14.dp),
                    thickness = 1.dp,
                    color = Gray50
                )
                CustomSwitch(
                    isChecked = it.isChecked,
                    onCheckedChange = it.onCheckedChange,
                    disabled = it.disabled
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SettingRowPreview() {
    val switchStatus = SwitchStatus(
        isChecked = true,
        onCheckedChange = { },
        disabled = false
    )
    Column{
        SettingRow(
            title = "ActivityRecognitionStat",
            subtitle = "Not Activated",
            switchStatus = switchStatus
        )
        SettingRow(
            title = "ActivityRecognitionStat",
            switchStatus = switchStatus
        )
        SettingRow(
            title = "ActivityRecognitionStat",
            subtitle = "Not Activated",
        )
        SettingRow(
            title = "ActivityRecognitionStat",
        )
    }
}