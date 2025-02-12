package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.TrackerUtil
import kaist.iclab.tracker.auth.User
import kaist.iclab.tracker.auth.UserState
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.controller.CollectorController
import kaist.iclab.tracker.data.core.DataStorage
import kaist.iclab.tracker.data.core.StateStorage
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModelImpl(
    private val collectorController: CollectorController,
    _collectors: Map<String, Collector>,
    _datastorages: Map<String, DataStorage>,
    trackerStateStorage: StateStorage<TrackerState>,
    private val permissionManager: PermissionManager
) : AbstractMainViewModel(_collectors, _datastorages) {
    init {
        collectorController.init(_collectors, trackerStateStorage)
    }

    override val trackerStateFlow: StateFlow<TrackerState>
        get() = collectorController.trackerStateFlow

    override fun runTracker() {
        collectorController.start()
    }

    override fun stopTracker() {
        collectorController.stop()
    }

    /*TODO: */
    private val _userStateFlow = MutableStateFlow(UserState(UserState.FLAG.LOGGEDOUT))
    override val userStateFlow: StateFlow<UserState>
        get() = _userStateFlow

    override fun login() {
        _userStateFlow.value = UserState(
            UserState.FLAG.LOGGEDIN,
            User("test@ic.kaist.ac.kr", "test", "M", "2025-01-01", 20)
        )
    }

    override fun logout() {
        _userStateFlow.value = UserState(UserState.FLAG.LOGGEDOUT)
    }

    override fun selectExperimentGroup(name: String) {
        TODO("Not yet implemented")
    }


    override val permissionStateFlow: StateFlow<Map<String, PermissionState>>
        get() = permissionManager.permissionStateFlow

    override fun requestPermissions(names: Array<String>, onResult: ((Boolean) -> Unit)?) {
        permissionManager.request(names) {
            onResult?.invoke(it)
        }
    }

    override fun getDeviceInfo(): String = TrackerUtil.getDeviceModel()
    override fun getAppVersion(): String = TrackerUtil.getAppVersion()
}