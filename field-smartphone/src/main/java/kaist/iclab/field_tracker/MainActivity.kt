package kaist.iclab.field_tracker

import android.Manifest
import android.os.Build
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Tracker.getPermissionManager().request(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.SCHEDULE_EXACT_ALARM)
            ){
                Log.d("MAIN_ACTIVITY", "Permission $it")
            }
        }
    }
}