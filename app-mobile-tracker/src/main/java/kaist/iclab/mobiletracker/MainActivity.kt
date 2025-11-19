package kaist.iclab.mobiletracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kaist.iclab.mobiletracker.helpers.AuthPreferencesHelper
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import kaist.iclab.mobiletracker.ui.Dashboard
import kaist.iclab.mobiletracker.ui.LoginScreen
import kaist.iclab.tracker.auth.GoogleAuth

class MainActivity : ComponentActivity() {

    private lateinit var bleHelper: BLEHelper
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supabaseHelper = SupabaseHelper()
        
        // Create all sensor services with shared SupabaseHelper
        val locationSensorService = LocationSensorService(supabaseHelper)
        val accelerometerSensorService = AccelerometerSensorService(supabaseHelper)
        val edaSensorService = EDASensorService(supabaseHelper)
        val heartRateSensorService = HeartRateSensorService(supabaseHelper)
        val ppgSensorService = PPGSensorService(supabaseHelper)
        val skinTemperatureSensorService = SkinTemperatureSensorService(supabaseHelper)
        
        // Initialize BLEHelper with injected dependencies
        bleHelper = BLEHelper(
            context = this,
            locationSensorService = locationSensorService,
            accelerometerSensorService = accelerometerSensorService,
            edaSensorService = edaSensorService,
            heartRateSensorService = heartRateSensorService,
            ppgSensorService = ppgSensorService,
            skinTemperatureSensorService = skinTemperatureSensorService
        )
        bleHelper.initialize()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // Initialize Google Auth inside Composable to ensure same instance
                    val serverClientId = remember { getString(R.string.default_web_client_id) }
                    val googleAuth = remember { GoogleAuth(this@MainActivity, serverClientId) }
                    val context = LocalContext.current
                    val authPreferencesHelper = remember { AuthPreferencesHelper(context) }
                    
                    val authViewModel: AuthViewModel = viewModel {
                        AuthViewModel(googleAuth, authPreferencesHelper)
                    }
                    val userState by authViewModel.userState.collectAsState()

                    // Log state changes for debugging
                    LaunchedEffect(userState.isLoggedIn) {
                        Log.d(TAG, "User state changed - isLoggedIn: ${userState.isLoggedIn}, user: ${userState.user?.name}, email: ${userState.user?.email}")
                    }

                    // Automatically navigate to Dashboard when login is successful
                    when {
                        userState.isLoggedIn -> {
                            Log.d(TAG, "Showing Dashboard")
                            Dashboard(viewModel = authViewModel)
                        }
                        else -> {
                            Log.d(TAG, "Showing LoginScreen")
                            LoginScreen(
                                onSignInWithGoogle = { 
                                    Log.d(TAG, "Login button clicked")
                                    authViewModel.login(this@MainActivity) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
