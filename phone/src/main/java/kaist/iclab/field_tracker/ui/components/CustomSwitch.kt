package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kaist.iclab.field_tracker.ui.theme.Blue500
import kaist.iclab.field_tracker.ui.theme.Gray500

data class SwitchStatus(
    val isChecked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val disabled: Boolean
)


@Composable
fun CustomSwitch(
    switchStatus: SwitchStatus
) {
    Box(
        modifier = Modifier
            .size(48.dp), // Accessibility Guide
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(14.dp)
                .background(
                    color = if(switchStatus.isChecked) Blue500 else Gray500,
                    shape  = RoundedCornerShape(999.dp)
                )
                .background(color = Color.White.copy(alpha = if(switchStatus.disabled) 0.5f else 0f))
                .clickable {
                    switchStatus.onCheckedChange(!switchStatus.isChecked)
                },
            contentAlignment = if (switchStatus.isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ){
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .size(12.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 50, heightDp = 200)
@Composable
fun CustomSwitchPreview() {
    Column {
        CustomSwitch(
            SwitchStatus(
                isChecked = true,
                onCheckedChange ={},
                disabled = true
            )
        )
        CustomSwitch(
            switchStatus = SwitchStatus(
                isChecked = true,
                onCheckedChange ={},
                disabled = false
            )
        )
        CustomSwitch(
            switchStatus = SwitchStatus(
                isChecked = false,
                onCheckedChange ={},
                disabled = true
            )
        )
        CustomSwitch(
            switchStatus = SwitchStatus(
                isChecked = false,
                onCheckedChange ={},
                disabled = false
            )
        )
    }
}
