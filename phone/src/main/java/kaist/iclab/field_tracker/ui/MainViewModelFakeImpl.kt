package kaist.iclab.field_tracker.ui

//class MainViewModelFakeImpl(
//    private val _collectors: Map<String, CollectorInterface>
//) : AbstractMainViewModel(_collectors) {
//
//    private val _trackerStateFlow: MutableStateFlow<TrackerState> =
//        MutableStateFlow(TrackerState(TrackerState.FLAG.READY))
//    override val trackerStateFlow: StateFlow<TrackerState>
//        get() = _trackerStateFlow
//
//
//    override fun runTracker() {
//        _trackerStateFlow.value = TrackerState(
//            TrackerState.FLAG.RUNNING
//        )
//    }
//
//    override fun stopTracker() {
//        _trackerStateFlow.value = TrackerState(
//            TrackerState.FLAG.READY
//        )
//    }
//
////    override val lastUpdatedFlow: StateFlow<Map<String, String>>
////        get() = MutableStateFlow(_collectors.keys.associate { Pair(it, "Last time") })
////    override val recordCountFlow: StateFlow<Map<String, Long>>
////        get() = MutableStateFlow(_collectors.keys.associate { Pair(it, 10) })
//
//    override fun getDeviceInfo(): String = "TESTING-DEVICE"
//    override fun getAppVersion(): String = "TESTING"
//
//    private val _userStateFlow = MutableStateFlow(UserState(UserState.FLAG.LOGGEDOUT))
//    override val userStateFlow: StateFlow<UserState>
//        get() = _userStateFlow
//
//    override fun login() {
//        _userStateFlow.value = UserState(
//            UserState.FLAG.LOGGEDIN,
//            User("test@ic.kaist.ac.kr", "test", "M", "2025-01-01", 20)
//        )
//    }
//
//    override fun logout() {
//        _userStateFlow.value = UserState(UserState.FLAG.LOGGEDOUT)
//    }
//
//    override fun selectExperimentGroup(name: String) {
//        TODO("Not yet implemented")
//    }
//
//    private val _permissionStateFlow = MutableStateFlow<Map<String, PermissionState>>(mapOf())
//    override val permissionStateFlow: StateFlow<Map<String, PermissionState>>
//        get() = _permissionStateFlow
//
//    override fun requestPermission(name: String) {}
//}