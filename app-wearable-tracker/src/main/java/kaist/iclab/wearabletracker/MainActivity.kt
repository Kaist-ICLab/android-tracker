package kaist.iclab.wearabletracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.wearabletracker.theme.WearableTrackerTheme
import kaist.iclab.wearabletracker.ui.SettingsScreen
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val permissionManager by inject<AndroidPermissionManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        permissionManager.bind(this)

        // Request notification permission at app start (required for Android 13+)
        requestNotificationPermission()
        setContent {
            WearableTrackerTheme {
                SettingsScreen(
                    androidPermissionManager = permissionManager
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionManager.request(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }
}