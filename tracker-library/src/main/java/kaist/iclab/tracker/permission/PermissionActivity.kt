package kaist.iclab.tracker.permission

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kaist.iclab.tracker.Tracker

open class PermissionActivity: ComponentActivity() {
    private val permissionManager= Tracker.getPermissionManager()

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        permissionManager.checkPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.initialize(this@PermissionActivity)
    }

    override fun onResume() {
        super.onResume()
        permissionManager.checkPermissions()
    }
}