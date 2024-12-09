package kaist.iclab.field_tracker

import android.Manifest
import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.LocationServices
import kaist.iclab.tracker.permission.PermissionActivity
import kaist.iclab.field_tracker.ui.MainScreen
import kaist.iclab.field_tracker.ui.theme.TrackerTheme
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.ActivityRecognitionStatCollector
import kaist.iclab.tracker.collectors.ActivityRecognitionStatCollector.Entity
import kaist.iclab.tracker.collectors.DataTrafficStatCollector
import kaist.iclab.tracker.collectors.LocationCollector
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
import org.koin.android.ext.android.get
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
