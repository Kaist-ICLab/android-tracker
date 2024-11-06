package kaist.iclab.field_tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.CollectorState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: AbstractMainViewModel = koinViewModel()) {
    val isRunning = viewModel.controllerStateFlow.collectAsStateWithLifecycle()
    val collectorStates = viewModel.collectorStateFlow.collectAsStateWithLifecycle(
       viewModel.collectors.map{ it to CollectorState(CollectorState.FLAG.UNAVAILABLE, "Not initialized") }.toMap()
    )
    val collectorConfigs = viewModel.configFlow.collectAsStateWithLifecycle(
        viewModel.collectors.map{ it to CollectorConfig() }.toMap()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Header()
        Section(
            contents = listOf(
                {
                    SettingRow(
                        title = "Run Tracker",
                        displayStatus = false,
                        displayToggle = true,
                        toggleStatus = isRunning.value,
                        onClick = {if(isRunning.value) viewModel.stop() else viewModel.start()}
                    )
                }
            )
        )

//        Section(
//            contents = listOf(
//                {
//                    SettingRow(
//                        title = "Users",
//                        status = "testing@ic.kaist.ac.kr",
//                        displayStatus = true,
//                        displayToggle = false,
//                        onClick = {}
//                    )
//                }
//            )
//        )

        Section(
            contents = viewModel.collectors.map{ name ->
                {
                    SettingRow(
                        title = name,
                        displayStatus = false,
                        displayToggle = true,
                        toggleStatus = collectorStates.value[name]?.flag == CollectorState.FLAG.ENABLED,
                        onClick = {if(collectorStates.value[name]?.flag == CollectorState.FLAG.ENABLED)
                            viewModel.disableCollector(name) else viewModel.enableCollector(name)}
                    )
                }
            }
        )

//        Section(
////            viewModel.collectorMap.keys.toList().
//            contents = listOf(
//                {
//                    SettingRow(
//                        title = "External Devices",
//                        status = "Galaxy Watch, Polar H10",
//                        displayStatus = true,
//                        displayToggle = true,
//                        onClick = {}
//                    )
//                }
//            )
//        )

//        Section(
//            contents = listOf(
//                {
//                    SettingRow(
//                        title = "Conduct Experiment",
//                        status = "beta-testing",
//                        displayStatus = true,
//                        displayToggle = true,
//                        onClick = {}
//                    )
//                },
//                {
//                    SettingRow(
//                        title = "Server URL",
//                        status= "abc.kaist.ac.kr",
//                        displayStatus = true,
//                        displayToggle = false,
//                        onClick = {}
//                    )
//                },
//                {
//                    SettingRow(
//                        title = "Sync",
//                        status= "3 hours",
//                        displayStatus = true,
//                        displayToggle = true,
//                        onClick = {}
//                    )
//                },
//                {
//                    SettingRow(
//                        title = "Delete",
//                        displayStatus = false,
//                        displayToggle = false,
//                        onClick = {}
//                    )
//                }
//            )
//        )

        Section(
            contents = listOf(
                {
                    SettingRow(
                        title = "Version",
                        status = viewModel.getAppVersion(),
                        displayStatus = true,
                        displayToggle = false,
                        onClick = {}
                    )
                },
                {
                    SettingRow(
                        title = "Device",
                        status= viewModel.getDeviceInfo(),
                        displayStatus = true,
                        displayToggle = false,
                        onClick = {}
                    )
                },
                {
                    SettingRow(
                        title = "License",
                        displayStatus = false,
                        displayToggle = false,
                        onClick = {}
                    )
                },
            )
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    TopAppBar(
        title = {
            Text(text = "Tracker Configuration",
                fontWeight = FontWeight.SemiBold)
        },
        actions = {
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,  // This is the standard menu icon
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black,
            scrolledContainerColor = Color.White,
            navigationIconContentColor = Color.Black
        )
    )
}

@Composable
fun CustomDivider(
    modifier: Modifier,
    isHorizontal: Boolean = true
) {
    if (isHorizontal) {
        HorizontalDivider(
            modifier = modifier,
            thickness = 1.dp,
            color = Color(0xFFEAEAEA)
        )
    } else {
        VerticalDivider(
            modifier = modifier,
            thickness = 1.dp,
            color = Color(0xFFEAEAEA)
        )
    }
}

@Composable
fun Section(
    contents: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp)

    ) {
        contents.forEachIndexed { index, item ->
            if (index > 0) {
                CustomDivider(modifier = Modifier
                    .fillMaxWidth())
//                    .padding(horizontal = 18.dp))
            }
            item()
        }
    }
}


@Composable
fun CustomToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,            // Blue thumb when checked
            checkedTrackColor = Color(0xFF3579FF),      // Blue track when checked
            uncheckedThumbColor = Color.White,          // White thumb when unchecked
            uncheckedTrackColor = Color(0xFF9A999E),     // Light blue track when unchecked
            uncheckedBorderColor = Color.Transparent,    // No border for unchecked state
            checkedBorderColor = Color.Transparent      // No border for checked state
        ),
        thumbContent = {
            Box(
                modifier = Modifier
                    .size(16.dp)                         // Increase thumb size
                    .background(Color.Transparent, CircleShape) // Ensure thumb is circular
            )
        }
    )
}


@Composable
fun SettingRow(
    title: String,
    status: String = "",
    onClick: () -> Unit,
    toggleStatus: Boolean = false,
    displayStatus: Boolean,
    displayToggle: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                )
                if (displayStatus) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        color = Color(0xFF8E8D92)
                    )
                }
            }
            if (displayToggle) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomDivider(modifier = Modifier.height(36.dp), isHorizontal = false)
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomToggle(toggleStatus) { onClick() }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun Preview() {
//    MainScreen(MainViewModelFakeImpl())
//}
