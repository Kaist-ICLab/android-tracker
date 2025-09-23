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
import kaist.iclab.tracker.sensor.survey.SurveySensor
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val permissionManager by inject<AndroidPermissionManager>()
    private val surveySensor by inject<SurveySensor>()

    private val listener = { response: SurveySensor.Entity -> Log.d("MainActivity", response.toString()); Unit }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.bind(this)
        surveySensor.addListener(listener)

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

    override fun onDestroy() {
        super.onDestroy()
        surveySensor.removeListener(listener)
    }
}
