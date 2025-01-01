package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SwitchStatus(
    val isChecked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    switchStatus: SwitchStatus? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if(switchStatus==null) 14.dp else 0.dp),
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
                    color = Color(0xFF8E8D92)
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
                    color = Color(0xFFCCCCCC)
                )

                CustomSwitch(
                    isChecked = it.isChecked,
                    onCheckedChange = it.onCheckedChange
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingRowPreview1() {
    var isChecked by remember { mutableStateOf(false) }
    SettingRow(
        title = "ActivityRecognitionStat",
        subtitle = "Not Activated",
        switchStatus = SwitchStatus(
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SettingRowPreview2() {
    var isChecked by remember { mutableStateOf(false) }
    SettingRow(
        title = "ActivityRecognitionStat",
        switchStatus = SwitchStatus(
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SettingRowPreview3() {
    var isChecked by remember { mutableStateOf(false) }
    SettingRow(
        title = "ActivityRecognitionStat",
        subtitle = "Not Activated",
    )
}

@Preview(showBackground = true)
@Composable
fun SettingRowPreview4() {
    var isChecked by remember { mutableStateOf(false) }
    SettingRow(
        title = "ActivityRecognitionStat",
    )
}
