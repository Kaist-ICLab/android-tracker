package dev.iclab.tracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: AbstractMainViewModel = koinViewModel()) {
    val isRunning = viewModel.isRunningState.collectAsStateWithLifecycle()
    val collectorConfig = viewModel.collectorConfigState.collectAsStateWithLifecycle()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.sync() }) {
                Icon(
                    imageVector = Icons.Rounded.Upload,
                    contentDescription = "upload",
                    tint = Color.Gray,
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
            }
            if(!isRunning.value){
                IconButton(onClick = { viewModel.start() }) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Starting Button",
                        tint = Color.Red,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                    )
                }
            }else{
                IconButton(onClick = { viewModel.stop() }) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = "Stopping Button",
                        tint = Color.Red,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                    )
                }
            }
            IconButton(onClick = { viewModel.delete() }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "delete data",
                    tint = Color.Gray,
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                viewModel.collectorList
            ) { item ->
                Row(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(item, fontSize = 21.sp)
                    Switch(
                        enabled = collectorConfig.value[item] != null,
                        checked = collectorConfig.value[item]?: false, onCheckedChange = {
                        if(it){
                            viewModel.enable(item)
                        }else{
                            viewModel.disable(item)
                        }
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen(MainViewModelFakeImpl())
}
