package kaist.iclab.wearablelogger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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

@Composable
fun SensorToggleChip(sensorName: String, listStates : MutableState<List<Boolean>>) {
    val toggledList by listStates
    val sensorNameToIndexMap = mapOf(
        "PPG Green" to 0,
        "Accelerometer" to 1,
        "Heart Rate" to 2,
        "Skin Temperature" to 3,
    )
    fun getSensorIndex(sensorName: String): Int {
        return sensorNameToIndexMap[sensorName] ?: 0 // 기본값은 0
    }
    ToggleChip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
            .height(32.dp),
        checked = toggledList[getSensorIndex(sensorName)],
        toggleControl = {
            Switch(
                checked = toggledList[getSensorIndex(sensorName)],
                modifier = Modifier.semantics {
                    this.contentDescription = if (toggledList[getSensorIndex(sensorName)]) "On" else "Off"
                }
            )
        },
        onCheckedChange = {
            newCheckedState ->
                val updatedList = toggledList.toMutableList()
                updatedList[getSensorIndex(sensorName)] = newCheckedState
                listStates.value = updatedList
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

