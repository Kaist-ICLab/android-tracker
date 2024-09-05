package kaist.iclab.wearablelogger

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import kaist.iclab.wearablelogger.ui.SettingsScreen

class MainActivity : PermissionActivity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionList = listOfNotNull(
            Manifest.permission.FOREGROUND_SERVICE,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
            Manifest.permission.BODY_SENSORS,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        requestPermissions.launch(permissionList.toTypedArray())

        setContent {
            SettingsScreen()
        }
    }
}