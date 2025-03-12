package kaist.iclab.tracker.permission

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.StateFlow

interface PermissionManager {
    /* Initialize the permission manager with the activity */
    fun initialize(activity: PermissionActivity)

    val permissionStateFlow: StateFlow<Map<String, PermissionState>>

    /*To update current status: Used for Special Permission Update */
    fun checkPermissions()

    fun request(
        permissions: Array<String>,
        onResult: ((result: Boolean) -> Unit)? = null
    )
}