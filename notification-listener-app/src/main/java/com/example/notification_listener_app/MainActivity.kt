package com.example.notification_listener_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification_listener_app.ui.theme.AndroidtrackerTheme
import com.example.notification_listener_app.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS, Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                    1
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier
) {
    val mainViewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Button(
            onClick = {
                mainViewModel.addCallback()
                Toast.makeText(context, "Callback added!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Add Listener")
        }

        Button(
            onClick = {
                mainViewModel.removeCallback()
                Toast.makeText(context, "Callback removed!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
            .fillMaxWidth()
        ) {
            Text("Remove Listener")
        }

        Button(
            onClick = {
                mainViewModel.sendNotification(context)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Post Notification")
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check permission for notification listening")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidtrackerTheme {
        Greeting()
    }
}