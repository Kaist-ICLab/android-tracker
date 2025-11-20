package kaist.iclab.mobiletracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.helpers.LanguageHelper
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.screens.MainScreen.MainScreen
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MobileTracker"
    }

    private val bleHelper by inject<BLEHelper>()
    private val permissionManager by inject<AndroidPermissionManager>()
    private val sensors by inject<List<Sensor<*, *>>>(named("sensors"))
    
    // Flag to track whether listeners are currently added
    private var listenersAdded = false
    
    // Create listeners for each sensor to log data
    private val listener = sensors.map { sensor ->
        { data: SensorEntity ->
            // Debug: Log all sensor data
            Log.d(TAG, "Data Received From Sensor: ${sensor.name}, Data: $data")
            Unit
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val context = LanguageHelper(newBase).attachBaseContextWithLanguage(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind permission manager to activity
        permissionManager.bind(this)

        // Initialize BLEHelper - dependencies are injected by Koin
        bleHelper.initialize()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AppContent()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        setupSensorListeners()
    }

    private fun setupSensorListeners() {
        if (listenersAdded) return
        Log.d(TAG, "Adding listeners to ${sensors.size} sensors")
        for (sensorIdx in sensors.indices) {
            val currentSensor = sensors[sensorIdx]
            currentSensor.addListener(listener[sensorIdx])
        }
        listenersAdded = true
        Log.d(TAG, "All sensor listeners added successfully")
    }

    override fun onPause() {
        super.onPause()
        // Listeners are kept active - no cleanup on pause
    }
    
    @Composable
    private fun AppContent() {
        val serverClientId = remember { getString(R.string.default_web_client_id) }
        val authViewModel: AuthViewModel = koinViewModel(
            parameters = { parametersOf(this@MainActivity, serverClientId) }
        )
        
        val userState by authViewModel.userState.collectAsState()
        val startDestination = if (userState.isLoggedIn) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
        
        val navController = rememberNavController()
        
        MainScreen(
            navController = navController,
            authViewModel = authViewModel,
            startDestination = startDestination
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        bleHelper.cleanup()
    }
}
