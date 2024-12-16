package kaist.iclab.field_tracker

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kaist.iclab.tracker.permission.PermissionActivity
import kaist.iclab.field_tracker.ui.MainScreen
import kaist.iclab.field_tracker.ui.theme.TrackerTheme
import kaist.iclab.tracker.Tracker
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinAndroidContext {
                TrackerTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MAIN_ACTIVITY", "onResume")
    }

    override fun onPostResume() {
        super.onPostResume()
        test()
    }

    private fun test() {
        val permissionManager = Tracker.getPermissionManager()
        permissionManager.request(
            arrayOf(
                Manifest.permission.PACKAGE_USAGE_STATS,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
////                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
//                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
            )
        ) {
            Log.d("PERMISSION", "GRANTED")
        }
    }
}
