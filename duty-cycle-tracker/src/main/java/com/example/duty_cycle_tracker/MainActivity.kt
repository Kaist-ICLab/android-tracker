/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.duty_cycle_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.duty_cycle_tracker.theme.AndroidtrackerTheme
import com.example.duty_cycle_tracker.ui.SettingsScreen
import kaist.iclab.tracker.permission.AndroidPermissionManager
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val permissionManager by inject<AndroidPermissionManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        permissionManager.bind(this)

        setContent {
            AndroidtrackerTheme {
                SettingsScreen(
                    androidPermissionManager = permissionManager
                )
            }
        }
    }
}