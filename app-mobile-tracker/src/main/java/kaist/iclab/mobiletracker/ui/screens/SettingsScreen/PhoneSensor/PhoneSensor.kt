package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.tracker.sensor.core.SensorState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SensorCard(
    modifier: Modifier = Modifier,
    sensorName: String,
    sensorStateFlow: StateFlow<SensorState>,
    isControllerRunning: Boolean,
    onToggle: () -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
) {
    val cardShape = when {
        isFirst -> Styles.CARD_SHAPE_TOP
        isLast -> Styles.CARD_SHAPE_BOTTOM
        else -> Styles.CARD_SHAPE_MIDDLE
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        shape = cardShape
    ) {
        SensorRow(
            sensorName = sensorName,
            sensorStateFlow = sensorStateFlow,
            isControllerRunning = isControllerRunning,
            toggleSensor = onToggle
        )
    }
}

@Composable
private fun SensorRow(
    sensorName: String,
    sensorStateFlow: StateFlow<SensorState>,
    isControllerRunning: Boolean,
    toggleSensor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sensorState = sensorStateFlow.collectAsState().value
    val isSensorEnabled = sensorState.flag == SensorState.FLAG.RUNNING ||
            sensorState.flag == SensorState.FLAG.ENABLED

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Styles.CARD_HORIZONTAL_PADDING,
                vertical = Styles.CARD_VERTICAL_PADDING
            )
    ) {
        Icon(
            imageVector = getSensorIcon(sensorName),
            contentDescription = sensorName,
            modifier = Modifier.size(Styles.ICON_SIZE),
            tint = AppColors.TextPrimary
        )
        Spacer(Modifier.width(Styles.ICON_SPACER_WIDTH))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = sensorName,
                color = AppColors.TextPrimary,
                fontSize = Styles.TEXT_FONT_SIZE,
                modifier = Modifier.padding(top = Styles.TEXT_TOP_PADDING)
            )
            Text(
                text = getSensorDescription(sensorName),
                color = AppColors.TextSecondary,
                fontSize = Styles.DESCRIPTION_FONT_SIZE,
                modifier = Modifier.padding(bottom = Styles.DESCRIPTION_BOTTOM_PADDING)
            )
        }
        Spacer(Modifier.width(Styles.SPACER_WIDTH))
        VerticalDivider(
            color = AppColors.BorderLight,
            modifier = Modifier
                .width(Styles.DIVIDER_WIDTH)
                .height(Styles.DIVIDER_HEIGHT),
            thickness = Styles.DIVIDER_THICKNESS
        )
        Spacer(Modifier.width(Styles.SPACER_WIDTH))
        SensorSwitch(
            checked = isSensorEnabled,
            enabled = !isControllerRunning,
            onCheckedChange = toggleSensor
        )
    }
}

@Composable
private fun SensorSwitch(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: () -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = { onCheckedChange() },
        enabled = enabled,
        modifier = Modifier.scale(Styles.SWITCH_SCALE),
        colors = Styles.switchColors()
    )
}
