package kaist.iclab.mobiletracker.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.data.sensors.phone.ProfileData
import kaist.iclab.mobiletracker.repository.DailySensorCounts
import kaist.iclab.mobiletracker.repository.HomeRepository
import kaist.iclab.mobiletracker.repository.UserProfileRepository
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kotlinx.coroutines.flow.*
import java.util.*

data class HomeUiState(
    val isTrackingActive: Boolean = false,
    val lastSyncedTime: String? = null,
    val locationCount: Int = 0,
    val appUsageCount: Int = 0,
    val activityCount: Int = 0,
    val batteryCount: Int = 0,
    val notificationCount: Int = 0,
    val screenCount: Int = 0,
    val connectivityCount: Int = 0,
    val bluetoothCount: Int = 0,
    val userName: String? = null
)

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val backgroundController: BackgroundController,
    private val syncTimestampService: SyncTimestampService,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val startOfDay: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    val uiState: StateFlow<HomeUiState> = combine(
        backgroundController.controllerStateFlow,
        homeRepository.getDailySensorCounts(startOfDay),
        userProfileRepository.profileFlow
    ) { state: ControllerState, counts: DailySensorCounts, profile: ProfileData? ->
        HomeUiState(
            isTrackingActive = state.flag == ControllerState.FLAG.RUNNING,
            lastSyncedTime = syncTimestampService.getLastSuccessfulUpload(),
            locationCount = counts.locationCount,
            appUsageCount = counts.appUsageCount,
            activityCount = counts.activityCount,
            batteryCount = counts.batteryCount,
            notificationCount = counts.notificationCount,
            screenCount = counts.screenCount,
            connectivityCount = counts.connectivityCount,
            bluetoothCount = counts.bluetoothCount,
            userName = profile?.email?.split("@")?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun triggerSync() {
        // Handled by existing sync logic
    }
}
