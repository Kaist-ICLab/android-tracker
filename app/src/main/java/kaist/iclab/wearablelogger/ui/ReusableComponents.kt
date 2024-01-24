package kaist.iclab.wearablelogger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import kaist.iclab.wearablelogger.ToggleStates

@Composable
fun SensorToggleChip(sensorName: String, toggleStates: ToggleStates) {
//    "PPG Green", "Accelerometer", "Heart Rate", "Skin Temperature"
    var isChecked by remember { mutableStateOf((false)) }
    isChecked = when (sensorName) {
        "PPG Green" -> toggleStates.ppgState
        "Accelerometer" -> toggleStates.accState
        "Heart Rate" -> toggleStates.hrState
        "Skin Temperature" -> toggleStates.stState
        else -> false // 다른 경우 기본값은 false로 설정
    }


    ToggleChip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
            .height(32.dp),
        checked = isChecked,
        toggleControl = {
            Switch(
                checked = isChecked,
                modifier = Modifier.semantics {
                    this.contentDescription = if (isChecked) "On" else "Off"
                },
            )
        },
        onCheckedChange = {
                newCheckedState ->
            when (sensorName) {
                "PPG Green" -> toggleStates.ppgState = newCheckedState
                "Accelerometer" -> toggleStates.accState = newCheckedState
                "Heart Rate" -> toggleStates.hrState = newCheckedState
                "Skin Temperature" -> toggleStates.stState = newCheckedState
            }
            isChecked = newCheckedState
        },
        label = {
            Text(
                text = sensorName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Composable
fun IconButtonWithIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    backgroundColor: Color,
    buttonSize: Dp = 32.dp,
    iconSize: Dp = 20.dp,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier
            .padding(4.dp)
            .size(buttonSize)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

