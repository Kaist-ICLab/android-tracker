package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensor.SensorCard
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensor.Styles
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.SettingsViewModel
import kaist.iclab.tracker.sensor.controller.ControllerState
import org.koin.androidx.compose.koinViewModel

private val VERTICAL_PADDING = 16.dp

/**
 * Settings screen with sensor management functionality
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val controllerState = viewModel.controllerState.collectAsState().value
    val isCollecting = controllerState.flag == ControllerState.FLAG.RUNNING
    val sensorList = viewModel.sensorState.toList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = VERTICAL_PADDING)
        ) {
            itemsIndexed(
                items = sensorList,
                key = { _, pair -> pair.first }
            ) { index, (sensorName, sensorStateFlow) ->
                val isFirst = index == 0
                val isLast = index == sensorList.size - 1
                
                SensorCard(
                    sensorName = sensorName,
                    sensorStateFlow = sensorStateFlow,
                    isControllerRunning = isCollecting,
                    onToggle = { viewModel.toggleSensor(sensorName) },
                    isFirst = isFirst,
                    isLast = isLast,
                    modifier = Modifier.padding(horizontal = Styles.CARD_CONTAINER_HORIZONTAL_PADDING)
                )
                
                // Add horizontal divider between cards (not after the last one)
                if (!isLast) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Styles.CARD_CONTAINER_HORIZONTAL_PADDING),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalDivider(
                            color = AppColors.BorderDark,
                            thickness = 0.dp,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }
                }
            }
        }
    }
}
