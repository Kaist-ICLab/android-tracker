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
import kaist.iclab.mobiletracker.viewmodels.SettingsViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MobileTracker"
    }

    private val bleHelper by inject<BLEHelper>()
    private val permissionManager by inject<AndroidPermissionManager>()
    private val settingsViewModel: SettingsViewModel by viewModel()

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
        settingsViewModel.setupSensorListeners()
    }

    override fun onPause() {
        super.onPause()
        // Listeners are kept active - no cleanup on pause
        // If cleanup is needed, call: settingsViewModel.cleanupSensorListeners()
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
