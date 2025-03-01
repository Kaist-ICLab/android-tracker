package dev.iclab.test_auth

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.iclab.test_auth.AuthViewModel
import kaist.iclab.tracker.auth.GoogleAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serverClientId =   this.getString(R.string.default_web_client_id)
        val googleAuth: GoogleAuth = GoogleAuth(this@MainActivity,serverClientId)
        val authViewModel: AuthViewModel = AuthViewModel(googleAuth)
        setContent {
            AuthScreen(authViewModel)
        }
    }
}

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val userState by viewModel.userState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userState.isLoggedIn) {
            Text(text = "Hello, ${userState.user?.name ?: "Unknown"}!")
            Text(text = "Email: ${userState.user?.email ?: "No email"}")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text= "Token: ${userState.token ?: "No token"}")
            Button(onClick = {
                Log.d("AuthScreen", "Get token button clicked")
                viewModel.getToken()
            }) {
                Text("Get Token")
            }
        } else {
            Button(onClick = {
                Log.d("AuthScreen", "Login button clicked")
                viewModel.login(activity)
            }) {
                Text("Login with Google")
            }
        }
    }
}
