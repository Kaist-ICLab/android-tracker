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
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.ui.Dashboard
import kaist.iclab.mobiletracker.ui.LoginScreen
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import kaist.iclab.tracker.auth.Authentication
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    private val bleHelper by inject<BLEHelper>()
    private val TAG = "MainActivity"

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
                    // Get server client ID
                    val serverClientId = remember { getString(R.string.default_web_client_id) }
                    val activity = this@MainActivity
                    
                    // Get GoogleAuth from Koin factory
                    val googleAuth: Authentication = remember {
                        GlobalContext.get().get(parameters = { parametersOf(activity, serverClientId) })
                    }
                    
                    // Get ViewModel from Koin with injected GoogleAuth
                    val authViewModel: AuthViewModel = koinViewModel(
                        parameters = { parametersOf(googleAuth) }
                    )
                    
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
