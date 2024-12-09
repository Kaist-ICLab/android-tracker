package kaist.iclab.tracker.permission

import kotlinx.coroutines.flow.StateFlow

interface PermissionManagerInterface {
    /* Initialize the permission manager with the activity
    * */
    fun initialize(activity: PermissionActivity)

    val permissionStateFlow: StateFlow<Map<String, PermissionState>>
    fun checkPermissions()
//    var onPermissionResult: PermissionResultCallback?
//    fun isPermissionGranted(permission: String): Boolean
//    fun isPermissionsGranted(permissions: Array<String>): Boolean
    fun request(
        permissions: Array<String>,
        onResult: ((result: Boolean) -> Unit)? = null
    )

}