package com.example.sensor_test_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sensor_test_app.ui.SensorScreen
import com.example.sensor_test_app.ui.theme.AndroidtrackerTheme
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    private val permissionManager by inject<AndroidPermissionManager>()
    private val sensors by inject<List<Sensor<*, *>>>(named("sensors"))

    private val listener = sensors.map { sensor ->
        { data: SensorEntity -> Log.v(sensor.name, data.toString()); Unit }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.bind(this)

        // Request POST_NOTIFICATIONS permission on app launch
        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        // POST_NOTIFICATIONS permission is only required on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("MainActivity", "Requesting POST_NOTIFICATIONS permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            } else {
                Log.i("MainActivity", "POST_NOTIFICATIONS permission already granted")
            }
        } else {
            Log.i(
                "MainActivity",
                "POST_NOTIFICATIONS permission not required on Android ${Build.VERSION.SDK_INT}"
            )
        }
    }

    override fun onResume() {
        super.onResume()
        for (sensorIdx in sensors.indices) {
            sensors[sensorIdx].addListener(listener[sensorIdx])
        }
    }

    override fun onPause() {
        super.onPause()
        for (sensorIdx in sensors.indices) {
            sensors[sensorIdx].removeListener(listener[sensorIdx])
        }
    }
}
