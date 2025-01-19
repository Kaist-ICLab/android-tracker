package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.StateFlow

interface MainViewModelInterface {
    val trackerStateFlow: StateFlow<TrackerState>
    fun runTracker()
    fun stopTracker()

    val collectorStateFlow: StateFlow<Map<String, CollectorState>>
    fun enableCollector(name: String)
    fun disableCollector(name: String)

    val collectors: Map<String, CollectorInterface>

    val collectorConfigFlow: StateFlow<Map<String, CollectorConfig>>
    val lastUpdatedFlow: StateFlow<Map<String, String>>
    val recordCountFlow: StateFlow<Map<String, Long>>


    fun getDeviceInfo(): String
    fun getAppVersion(): String

    val userStateFlow: StateFlow<UserState>
    fun login()
    fun logout()
    fun selectExperimentGroup(name: String)

    val permissionStateFlow: StateFlow<Map<String, PermissionState>>
    fun requestPermission(name: String)
}

data class TrackerState(
    val flag: FLAG,
    val message: String? = null
) {
    enum class FLAG {
        DISABLED, // The tracker is not ready to run
        READY, // The tracker is ready to run
        RUNNING // The tracker is running
    }
}

data class UserState(
    val flag: FLAG,
    val user: User? = null,
) {
    enum class FLAG {
        LOGGEDIN,
        LOGGEDOUT
    }
}

data class User(
    val email: String,
    val name: String,
    val gender: String,
    val birthDate: String,
    val age: Int,
    val experimentGroup: String? = null
)
