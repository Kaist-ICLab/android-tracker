package kaist.iclab.tracker.permission

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kaist.iclab.tracker.Tracker

open class PermissionActivity: ComponentActivity() {
    private val permissionManager= Tracker.getPermissionManager()

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result: PermissionResult ->
        permissionManager.onPermissionResult?.invoke(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.attach(this@PermissionActivity)
    }
}