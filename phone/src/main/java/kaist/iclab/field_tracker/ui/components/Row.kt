package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SwitchItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp), // Accessibility Guide
        contentAlignment = Alignment.Center
    ){
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .width(26.dp) // Switch 너비
                .height(14.dp), // Switch 높이
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3579FF),
                uncheckedTrackColor = Color(0xFF9A999E)
            ),
            thumbContent = {
                    Box(
                        modifier = Modifier
                            .size(12.dp) // Thumb 크기 설정
                            .background(Color.White, shape = CircleShape)
                    )
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 50, heightDp = 50)
@Composable
fun SwitchItemPreview() {
    var isChecked by remember { mutableStateOf(true) }
    SwitchItem(
        label = "ActivityRecognitionStat",
        isChecked = isChecked,
        onCheckedChange = { isChecked = it }
    )
}