package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.auth.UserState
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.data.core.DataStorage
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.StateFlow

interface MainViewModelInterface {
    val trackerStateFlow: StateFlow<TrackerState>
    fun runTracker()
    fun stopTracker()

    val collectors: Map<String, Collector>
    val collectorStateFlow: StateFlow<Map<String, CollectorState>>
    fun enableCollector(name: String)
    fun disableCollector(name: String)

    val dataStorages: Map<String, DataStorage>


    fun getDeviceInfo(): String
    fun getAppVersion(): String

    val userStateFlow: StateFlow<UserState>
    fun login()
    fun logout()
    fun selectExperimentGroup(name: String)

    val permissionStateFlow: StateFlow<Map<String, PermissionState>>
    fun requestPermissions(names: Array<String>, onResult: ((Boolean) -> Unit)? = null)
}