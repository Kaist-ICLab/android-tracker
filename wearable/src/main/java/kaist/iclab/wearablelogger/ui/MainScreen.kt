package kaist.iclab.wearablelogger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: AbstractMainViewModel = koinViewModel()
) {
    val isRunning = viewModel.isRunningState.collectAsState().value
    val collectorConfig = viewModel.collectorConfigState.collectAsState().value



//    val isCollecting = settingsViewModel.isCollectorState.collectAsState().value
//    val uiState = settingsViewModel.uiState.collectAsState().value
//    val listState = rememberScalingLazyListState() // for Scaling Lazy column
    //UI
//    Scaffold(
//        timeText = {
//            TimeText(modifier = Modifier.scrollAway(listState))
//        },
//        vignette = {
//            Vignette(vignettePosition = VignettePosition.TopAndBottom)
//        },
//        positionIndicator = {
//            PositionIndicator(
//                scalingLazyListState = listState
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 10.dp),
//        ) {
//            SettingController(
//                upload = { settingsViewModel.upload() },
//                flush = { settingsViewModel.flush() },
//                startLogging = {
//                    settingsViewModel.startLogging() },
//                stopLogging = {
//                    settingsViewModel.stopLogging() },
//                isCollecting = isCollecting
//            )
//            ScalingLazyColumn(
//                state = listState
//            ) { // Lazy column for WearOS
//                uiState.sensorStates.forEach { sensorState ->
//                    item {
//                        SensorToggleChip(
//                            sensorName = sensorState.name,
//                            isEnabled = sensorState.isEnabled,
//                            updateStatus = {status -> settingsViewModel.update(sensorState.name, status)}
//                        )
//                    }
//                }
//            }
//        }
//    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(viewModel = FakeMainViewModelImpl())
}

//@Composable
//fun SettingController(
//    upload: () -> Unit,
//    flush: () -> Unit,
//    startLogging: () -> Unit,
//    stopLogging: () -> Unit,
//    isCollecting: Boolean
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth(1f),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(
//            icon = Icons.Default.Upload,
//            onClick = upload,
//            contentDescription = "Upload data",
//            backgroundColor = MaterialTheme.colors.secondary,
//            buttonSize = 32.dp,
//            iconSize = 20.dp
//        )
//        IconButton(
//            icon = if (isCollecting) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
//            onClick = if (isCollecting) stopLogging else startLogging,
//            contentDescription = "Start/Stop Collection",
//            backgroundColor = if (isCollecting) MaterialTheme.colors.error else MaterialTheme.colors.primary,
//            buttonSize = 48.dp,
//            iconSize = 36.dp,
//        )
//        IconButton(
//            icon = Icons.Default.Delete,
//            onClick = flush,
//            contentDescription = "Reset icon",
//            backgroundColor = MaterialTheme.colors.secondary,
//            buttonSize = 32.dp,
//            iconSize = 20.dp
//        )
//    }
//}
//
//@Composable
//fun SensorToggleChip(
//    sensorName: String, isEnabled: Boolean,
//    updateStatus: (status: Boolean) -> Unit
//) {
//    ToggleChip(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
//            .height(32.dp),
//        checked = isEnabled,
//        toggleControl = {
//            Switch(
//                checked = isEnabled,
//                modifier = Modifier.semantics {
//                    this.contentDescription = if (isEnabled) "On" else "Off"
//                },
//            )
//        },
//        onCheckedChange = updateStatus,
//        label = {
//            Text(
//                text = sensorName,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    )
//}
//
//@Composable
//fun IconButton(
//    icon: ImageVector,
//    onClick: () -> Unit,
//    contentDescription: String,
//    backgroundColor: Color,
//    buttonSize: Dp = 32.dp,
//    iconSize: Dp = 20.dp,
//) {
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
//        modifier = Modifier
//            .padding(4.dp)
//            .size(buttonSize)
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = contentDescription,
//            modifier = Modifier.size(iconSize)
//        )
//    }
//}
//
//@Preview
//@Composable
//fun IconButtonPreview(){
//    IconButton(
//        icon = Icons.Default.PlayArrow,
//        onClick = {},
//        contentDescription =  "ASDAS",
//        backgroundColor = MaterialTheme.colors.primary
//    )
//}
