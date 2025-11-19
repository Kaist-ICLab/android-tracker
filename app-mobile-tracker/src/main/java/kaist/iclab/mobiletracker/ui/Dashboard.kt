package kaist.iclab.mobiletracker.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel

@Composable
fun Dashboard(
    viewModel: AuthViewModel
) {
    val userState by viewModel.userState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userState.isLoggedIn) {
            Text(
                text = "Hello, ${userState.user?.name ?: "Unknown"}!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Email: ${userState.user?.email ?: "No email"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { viewModel.logout() }) {
                Text("Sign Out")
            }
        } else {
            Text(
                text = "Not logged in",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (activity != null) {
                Button(onClick = { viewModel.login(activity) }) {
                    Text("Sign In with Google")
                }
            }
        }
    }
}
