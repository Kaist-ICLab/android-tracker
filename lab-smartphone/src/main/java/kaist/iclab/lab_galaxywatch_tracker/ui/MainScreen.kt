package kaist.iclab.lab_galaxywatch_tracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import formatLapsedTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AbstractMainViewModel = koinViewModel()
) {
    val isRecording = viewModel.isRecordingState.collectAsState()
    val lapsedTime = viewModel.lapsedTime.collectAsState()
    val data = viewModel.dataState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Galaxy Wearable Logger", fontWeight = FontWeight.SemiBold)
                },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        modifier = Modifier.padding(0.dp)

    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(innerPadding)
        ) {

            /* Collector Controller & Timer */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    onClick = { if (isRecording.value) viewModel.stop() else viewModel.start() }) {
                    Text(text = if (isRecording.value) "STOP" else "START", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Timer: ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = lapsedTime.value.formatLapsedTime(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            /* Retrieved Data */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Last Update: ${data.value.timestamp}", fontSize = 16.sp)
                Text(text = "${data.value.acc}", fontSize = 16.sp)
                Text(text = "${data.value.ppg}", fontSize = 16.sp)
                Text(text = "${data.value.hr}", fontSize = 16.sp)
                Text(text = "${data.value.ibi}", fontSize = 16.sp)

            }

            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            /* Event Tagging */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    onClick = { viewModel.tag() }) {
                    Text(text = "TAG", fontSize = 16.sp)
                }

//                LazyColumn {
//                    items(10) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth(1f)
//                                .padding(8.dp)
//                        ) {
//                            Text(text = "Item $it", fontSize = 16.sp)
//                        }
//                    }
//                }
            }

        }
    }

}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(viewModel = FakeMainViewModelImpl())
}