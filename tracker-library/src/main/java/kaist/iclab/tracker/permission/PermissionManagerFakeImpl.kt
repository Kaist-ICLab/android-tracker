package kaist.iclab.tracker.permission

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermissionManagerFakeImpl:PermissionManagerInterface {
    override fun initialize(activity: PermissionActivity) {}

    private val _permissionStateFlow = MutableStateFlow<Map<String,PermissionState>>(mapOf())
    override val permissionStateFlow: StateFlow<Map<String, PermissionState>>
        get() = _permissionStateFlow

    override fun checkPermissions(){}

    override fun request(
        permissions: Array<String>,
        onResult: ((result: Boolean) -> Unit)?
    ) {}
}