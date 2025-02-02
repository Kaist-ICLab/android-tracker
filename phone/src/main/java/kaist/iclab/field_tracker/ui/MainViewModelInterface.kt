package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.auth.UserState
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.StateFlow

interface MainViewModelInterface {
    val trackerStateFlow: StateFlow<TrackerState>
    fun runTracker()
    fun stopTracker()

    val collectors: Map<String, CollectorInterface>
    val collectorStateFlow: StateFlow<Map<String, CollectorState>>
    fun enableCollector(name: String)
    fun disableCollector(name: String)

//    val lastUpdatedFlow: StateFlow<Map<String, String>>
//    val recordCountFlow: StateFlow<Map<String, Long>>

    fun getDeviceInfo(): String
    fun getAppVersion(): String

    val userStateFlow: StateFlow<UserState>
    fun login()
    fun logout()
    fun selectExperimentGroup(name: String)

    val permissionStateFlow: StateFlow<Map<String, PermissionState>>
    fun requestPermissions(names: Array<String>, onResult: ((Boolean) -> Unit)? = null)
}