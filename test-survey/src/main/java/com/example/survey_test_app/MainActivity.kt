package com.example.survey_test_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.survey_test_app.ui.SensorScreen
import com.example.survey_test_app.ui.theme.AndroidtrackerTheme
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    private val permissionManager by inject<AndroidPermissionManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.bind(this)

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
}
