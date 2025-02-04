package kaist.iclab.field_tracker.ui.components.userInputs

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.theme.MainTheme

@Composable
fun DurationInput(
    value: String,
    onValueChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box {
            if (value.isEmpty()) {
                Text(
                    text = value,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { newText ->
                    Log.d("DurationInput", "newText: $newText")
                    if (newText.all { it.isDigit() }) { // 숫자만 입력 가능
                        onValueChanged(newText)
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(60.dp)
            )
        }

        if (value.isNotEmpty() && value.all { it.isDigit() }) {
            val intValue = value.toIntOrNull() ?: 0
            Text(
                text = "ms",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${intValue.toDuration()})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}


@Preview
@Composable
fun DurationInputPreview() {
    var text by remember { mutableStateOf("111111") }
    MainTheme {
        DurationInput(
            value = text,
            onValueChanged = { text = it }
        )
    }
}

fun Int.toDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return buildString {
        if (hours > 0) append("${hours}시간 ")
        if (minutes > 0) append("${minutes}분 ")
        if (seconds > 0) append("${seconds}초")
        if (this.isEmpty()) append("0초")
    }.trim()
}
