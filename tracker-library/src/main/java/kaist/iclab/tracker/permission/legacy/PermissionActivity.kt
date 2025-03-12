//package kaist.iclab.tracker.permission.legacy
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.result.contract.ActivityResultContracts
//
//open class PermissionActivity(
//    private val permissionManager: PermissionManager
//): ComponentActivity() {
//    val permissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions(),
//    ) {
//        permissionManager.checkPermissions()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        permissionManager.initialize(this@PermissionActivity)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        permissionManager.checkPermissions()
//    }
//}