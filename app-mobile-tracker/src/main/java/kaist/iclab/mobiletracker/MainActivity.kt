package kaist.iclab.mobiletracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kaist.iclab.mobiletracker.ui.Dashboard
import kaist.iclab.mobiletracker.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    var showDashboard by remember { mutableStateOf(false) }
                    
                    if (showDashboard) {
                        Dashboard()
                    } else {
                        LoginScreen(
                            onSignInWithGoogle = { /* TODO: Implement sign in */ },
                            onTestWithoutLogin = { showDashboard = true }
                        )
                    }
                }
            }
        }
    }
}
