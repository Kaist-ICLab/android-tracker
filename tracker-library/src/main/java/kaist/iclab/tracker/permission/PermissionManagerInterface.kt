package kaist.iclab.tracker.permission

interface PermissionManagerInterface {
    var onPermissionResult: PermissionResultCallback?
    fun attach(activity: PermissionActivity)
    fun isPermissionGranted(permission: String): Boolean
    fun isPermissionsGranted(permissions: Array<String>): Boolean
    fun request(
        permissions: Array<String>,
        onResult: ((permissionResult: PermissionResult) -> Unit)? = null
    )
}