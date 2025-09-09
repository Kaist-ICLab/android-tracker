package kaist.iclab.wearabletracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

        setContent {
            WearableTrackerTheme {
                SettingsScreen(
                    androidPermissionManager = permissionManager
                )
            }
        }
    }
}