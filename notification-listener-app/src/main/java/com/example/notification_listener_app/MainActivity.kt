package com.example.notification_listener_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification_listener_app.ui.MainViewModel
import com.example.notification_listener_app.ui.theme.AndroidtrackerTheme


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
                    NotificationListenerTestApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("BatteryLife")
@Composable
fun NotificationListenerTestApp(
    modifier: Modifier = Modifier
) {
    val mainViewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    NotificationTestScreen(
        addCallback = {
            mainViewModel.addCallback()
            Toast.makeText(context, "Callback added!", Toast.LENGTH_SHORT).show()
        },
        removeCallback =  {
            mainViewModel.removeCallback()
            Toast.makeText(context, "Callback removed!", Toast.LENGTH_SHORT).show()
        },
        postNotification = {
            mainViewModel.sendNotification(context)
        },
        checkNotificationListeningPermission = {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        },
        checkBackgroundProcessPermission = {
            val packageName: String = context.packageName
            val intent = Intent()

            val pm = context.getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
                intent.setData("package:$packageName".toUri())
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Already enabled!", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    )
}

@SuppressLint("BatteryLife")
@Composable
fun NotificationTestScreen(
    modifier: Modifier = Modifier,
    addCallback: () -> Unit = {},
    removeCallback: () -> Unit = {},
    postNotification: () -> Unit = {},
    checkNotificationListeningPermission: () -> Unit = {},
    checkBackgroundProcessPermission: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            "Actions",
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Button(
            onClick = addCallback,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Add Listener")
        }
        Button(
            onClick = removeCallback,
            modifier = Modifier
            .fillMaxWidth()
        ) {
            Text("Remove Listener")
        }

        Button(
            onClick = postNotification,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Post Notification")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Permissions",
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Button(
            onClick = checkNotificationListeningPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Notification listening")
        }

        Button(
            onClick = checkBackgroundProcessPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Background process")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidtrackerTheme {
        NotificationTestScreen()
    }
}