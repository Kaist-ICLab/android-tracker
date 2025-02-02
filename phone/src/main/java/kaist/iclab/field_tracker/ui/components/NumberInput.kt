package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kaist.iclab.field_tracker.ui.theme.Gray300
import kaist.iclab.field_tracker.ui.theme.Gray500
import kaist.iclab.field_tracker.ui.theme.Gray600

@Composable
fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    labelFormatter: (Int) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
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
                    if (newText.all { it.isDigit() }) { // 숫자만 입력 가능
                        onValueChange(newText)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Black,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.width(72.dp)
            )
        }

        if (value.isNotEmpty() && value.all { it.isDigit() }) {
            val intValue = value.toIntOrNull() ?: 0
            Text(
                text = "ms",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Gray500,
                    fontWeight = FontWeight.Medium
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${labelFormatter(intValue)})",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Gray500,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}


@Preview
@Composable
fun NumberInputPreview() {
    var text by remember { mutableStateOf("111111") }

    NumberInput(
        value = text,
        onValueChange = { text = it },
        placeholder = "숫자를 입력하세요",
        labelFormatter = { num -> num.toDuration() }
    )
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
