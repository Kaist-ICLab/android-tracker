package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.theme.Blue500
import kaist.iclab.field_tracker.ui.theme.Gray500

@Composable
fun RadioBox(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Blue500, // ✅ 선택된 색상
                        unselectedColor = Gray500
                    )
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Preview
@Composable
fun CustomRadioButtonGroupPreview(){
    RadioBox(
        options = listOf("Option 1", "Option 2", "Option 3"),
        selectedOption = "Option 1",
        onOptionSelected = {}
    )
}