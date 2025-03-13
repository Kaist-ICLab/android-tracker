package com.example.sensor_test_app

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensor_test_app.ui.MainViewModel
import com.example.sensor_test_app.ui.theme.AndroidtrackerTheme
import com.example.sensor_test_app.util.ambientLightCollector
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorTest(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SensorTest(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel()

    val ambientSensor = ambientLightCollector(context)
    ambientSensor.addListener {
        mainViewModel.setSensorValue(0, it.value.toDouble())
        Log.v("test_ambient", "${it.value.toDouble()}")
    }
    mainViewModel.registerSensor(ambientSensor as BaseSensor<SensorConfig, SensorEntity>)

    LazyColumn(
        modifier = modifier.
            fillMaxSize()
    ) {
        itemsIndexed(mainViewModel.sensors) { index: Int, item: BaseSensor<SensorConfig, SensorEntity> ->
            SensorTestRow(
                sensorName = item.NAME,
                isRunning = mainViewModel.sensorRunning[index],
                startSensor = { mainViewModel.startSensor(index) },
                stopSensor = {
                    mainViewModel.stopSensor(index)
                    Log.v("test_ambient", "${mainViewModel.sensorRunning[index]}")
                 },
                sensorValue = mainViewModel.sensorValues[index]
            )
        }
    }
}

@Composable
fun SensorTestRow(
    sensorName: String,
    isRunning: Boolean,
    startSensor: () -> Unit,
    stopSensor: () -> Unit,
    sensorValue: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(15.dp)
    ) {
        Text(sensorName)

        Spacer(Modifier.width(10.dp))

        if(isRunning) {
            Button(
                onClick = stopSensor,
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.width(15.dp)
                )
            }
        } else {
            Button(
                onClick = startSensor,
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier
                        .width(25.dp)
                        .height(25.dp)
                )
            }
        }

        Spacer(Modifier.width(15.dp))
        
        Text("$sensorValue")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidtrackerTheme {
        SensorTest()
    }
}

@Preview(showBackground = true)
@Composable
fun SensorTestRowPreview() {
    AndroidtrackerTheme {
        SensorTestRow(
            sensorName = "AmbientLight",
            isRunning = false,
            startSensor = {},
            stopSensor = {},
            sensorValue = 0.0,
        )
    }
}

