package kaist.iclab.field_tracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kaist.iclab.field_tracker.ui.theme.TrackerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: AbstractMainViewModel = koinViewModel()) {
    val isRunning = viewModel.isRunningState.collectAsStateWithLifecycle()

    Column {
        CollectorControllerUI(isRunning.value, viewModel)
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                viewModel.collectorMap.keys.toList()
            ) { item ->
                Collector(item, viewModel)
            }
        }
    }
}


@Composable
fun Collector(name: String, viewModel: AbstractMainViewModel) {
    val enabledCollectors = viewModel.enabledCollectors.collectAsStateWithLifecycle()
    val (expanded, setExpand) = remember { mutableStateOf(true) }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { setExpand(!expanded) }) {
                    Icon(

                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = "expand",
                        tint = Color.Gray,
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = name,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = name, fontSize = 16.sp)
            }
            Switch(
                checked = enabledCollectors.value[name] ?: false,
                onCheckedChange = {
                    if (it) {
                        viewModel.enable(name)
                    } else {
                        viewModel.disable(name)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0B57D0),            // Blue thumb when checked
                    checkedTrackColor = Color(0xFFD3E3FD),      // Blue track when checked
                    uncheckedThumbColor = Color.White,          // White thumb when unchecked
                    uncheckedTrackColor = Color(0xFFC0C0C0),     // Light blue track when unchecked
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

        if (expanded) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(text = "Hello")
            }

        }
    }


}

@Composable
fun CollectorControllerUI(isRunning: Boolean, viewModel: AbstractMainViewModel) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Sync Button (left of Play/Pause)
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Sync",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        // Play/Pause Button (centered as main action)
        IconButton(
            onClick = {
                if (isRunning) {
                    viewModel.stop()
                } else {
                    viewModel.start()
                }
            },
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF0B57D0), CircleShape)
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // Flush Button
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Flush",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen(MainViewModelFakeImpl())
}
