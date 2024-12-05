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


//        if(!Tracker.getPermissionManager().isPermissionGranted(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)){
////            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
////            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
////                data = Uri.fromParts("package", (this@MainActivity as Context).packageName, null)
////            }
////            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
//            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//
//        }else{
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
//        }

//        if(!Tracker.getPermissionManager().isPermissionGranted(Manifest.permission.PACKAGE_USAGE_STATS)){
//            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        }
//        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))

//        checkAndRequestUsageStatsPermission(this)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Tracker.getPermissionManager().request(
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
////                    Manifest.permission.SCHEDULE_EXACT_ALARM)
//            ) {
//                Log.d("MAIN_ACTIVITY", "Permission $it")
//            }
//        }

//    private fun checkAndRequestUsageStatsPermission(context: Context) {
//        if (!hasUsageStatsPermission(context)) {
//            // 권한이 없으므로 설정 화면으로 이동
//            Toast.makeText(context, "Usage Stats 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
//                data = Uri.fromParts("package", context.packageName, null)
//            }
//            context.startActivity(intent)
//        } else {
//            // 권한이 이미 있음
//            Toast.makeText(context, "Usage Stats 권한이 이미 허용되었습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

}