package com.example.sensor_test_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensor_test_app.ui.MainViewModel
import com.example.sensor_test_app.ui.theme.AndroidtrackerTheme
import com.example.sensor_test_app.util.addBunchOfSensors
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pm = PermissionManagerImpl(this)
        pm.bind(this)

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorTest(
                        permissionManager = pm,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SensorTest(
    permissionManager: PermissionManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel()
    addBunchOfSensors(context, permissionManager, mainViewModel)

    LazyColumn(
        modifier = modifier.
            fillMaxSize()
    ) {
        itemsIndexed(mainViewModel.sensors) { index: Int, item: BaseSensor<SensorConfig, SensorEntity> ->
            SensorTestRow(
                sensorName = item.NAME,
                sensorState = mainViewModel.sensorState[index],
                grantPermission = {
                    permissionManager.request(item.permissions)
                    mainViewModel.enableSensor(index)
                },
                startSensor = {
                    mainViewModel.startSensor(index)
                },
                stopSensor = { mainViewModel.stopSensor(index) },
                sensorValue = mainViewModel.sensorValues[index]
            )
        }
    }
}

@Composable
fun SensorTestRow(
    sensorName: String,
    sensorState: SensorState.FLAG,
    grantPermission: () -> Unit,
    startSensor: () -> Unit,
    stopSensor: () -> Unit,
    sensorValue: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(15.dp)
    ) {
        Text(sensorName)

        Spacer(Modifier.width(10.dp))

        SmallSquareIconButton(
            icon = Icons.Default.Build,
            enabled = (sensorState != SensorState.FLAG.UNAVAILABLE),
            onClick = grantPermission
        )

        Spacer(Modifier.width(5.dp))

        val isSensorEnabled = (sensorState == SensorState.FLAG.RUNNING || sensorState == SensorState.FLAG.ENABLED)
        if(sensorState == SensorState.FLAG.RUNNING) {
            SmallSquareIconButton(
                icon = Icons.Default.Close,
                enabled = true,
                onClick = stopSensor
            )
        } else {
            SmallSquareIconButton(
                icon = Icons.Default.PlayArrow,
                enabled = isSensorEnabled,
                onClick = startSensor
            )
        }

        Spacer(Modifier.width(15.dp))
        
        Text(sensorValue)
    }
}

@Composable
fun SmallSquareIconButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .width(25.dp)
            .height(25.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SensorTestPreview() {
//    AndroidtrackerTheme {
//        SensorTest()
//    }
//}

@Preview(showBackground = true)
@Composable
fun SensorTestRowPreview() {
    AndroidtrackerTheme {
        SensorTestRow(
            sensorName = "AmbientLight",
            sensorState = SensorState.FLAG.RUNNING,
            grantPermission = {},
            startSensor = {},
            stopSensor = {},
            sensorValue = "Value",
        )
    }
}

