package com.example.notification_listener_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification_listener_app.ui.AccessibilityTest
import com.example.notification_listener_app.ui.NotificationTest
import com.example.notification_listener_app.ui.theme.AndroidtrackerTheme
import com.example.notification_listener_app.viewmodel.AccessibilityViewModel
import com.example.notification_listener_app.viewmodel.NotificationViewModel


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
                ListenerTestApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    changePageIndex: (Int) -> Unit,
) {
    var showDropDownMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(Icons.Filled.MoreVert, null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Notification") },
                    onClick = {
                        changePageIndex(0)
                        showDropDownMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Accessibility") },
                    onClick = {
                        changePageIndex(1)
                        showDropDownMenu = false
                    }
                )
            }
        },
    )
}

@SuppressLint("BatteryLife")
@Composable
fun ListenerTestApp(
    modifier: Modifier = Modifier
) {
    val notificationViewModel: NotificationViewModel = viewModel()
    val accessibilityViewModel: AccessibilityViewModel = viewModel()
    var pageIndex by remember { mutableIntStateOf(0) }

    val appTitle = listOf("Notification Test",  "Accessibility Test")

    Scaffold(
        topBar = {
            AppBar(
                title = appTitle[pageIndex],
                changePageIndex = { pageIndex = it }
            ) },

        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            when(pageIndex) {
                0 -> NotificationTest(
                    viewModel = notificationViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                else -> AccessibilityTest(
                    viewModel = accessibilityViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun AppBarPreview() {
    MaterialTheme {
        AppBar(
            title = "Testing Test",
            changePageIndex = {}
        )
    }
}