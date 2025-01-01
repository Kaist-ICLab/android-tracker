package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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


@Composable
fun CustomSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
                    color = if(isChecked) Color(0xFF3579FF) else Color(0xFF9A999E),
                    shape  = RoundedCornerShape(12.dp)
                )
                .clickable {
                    onCheckedChange(!isChecked)
                },
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
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

@Preview(showBackground = true, widthDp = 50, heightDp = 50)
@Composable
fun CustomSwitchPreview() {
    var isChecked by remember { mutableStateOf(true) }
    CustomSwitch(
        isChecked = isChecked,
        onCheckedChange = { isChecked = it }
    )
}