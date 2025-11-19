package kaist.iclab.mobiletracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.navigation.NavGraph
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    private val bleHelper by inject<BLEHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize BLEHelper - dependencies are injected by Koin
        bleHelper.initialize()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // Get server client ID and Activity for ViewModel factory
                    val serverClientId = remember { getString(R.string.default_web_client_id) }
                    val activity = this@MainActivity
                    
                    // Get ViewModel from Koin - GoogleAuth is created internally by the factory
                    val authViewModel: AuthViewModel = koinViewModel(
                        parameters = { parametersOf(activity, serverClientId) }
                    )
                    
                    // Determine start destination based on current auth state
                    val userState by authViewModel.userState.collectAsState()
                    val startDestination = if (userState.isLoggedIn) {
                        Screen.Dashboard.route
                    } else {
                        Screen.Login.route
                    }
                    
                    // Create NavController
                    val navController = rememberNavController()
                    
                    // Setup navigation graph
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
