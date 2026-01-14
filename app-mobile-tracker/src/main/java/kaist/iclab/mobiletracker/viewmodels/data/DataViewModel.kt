package kaist.iclab.mobiletracker.viewmodels.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.repository.DataRepository
import kaist.iclab.mobiletracker.repository.SensorInfo
import kaist.iclab.mobiletracker.utils.AppToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Data screen.
 */
data class DataUiState(
    val isLoading: Boolean = true,
    val sensors: List<SensorInfo> = emptyList(),
    val totalRecords: Int = 0,
    val error: String? = null,
    val isUploading: Boolean = false,
    val isDeleting: Boolean = false
)

/**
 * ViewModel for the Data screen.
 * Provides sensor list with record counts and last recorded times.
 */
class DataViewModel(
    private val dataRepository: DataRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    init {
        loadSensorInfo()
    }

    /**
     * Load sensor information from the repository.
     */
    fun loadSensorInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val sensors = dataRepository.getAllSensorInfo()
                val totalRecords = sensors.sumOf { it.recordCount }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    sensors = sensors,
                    totalRecords = totalRecords
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load sensor data"
                )
            }
        }
    }

    /**
     * Refresh the sensor list.
     */
    fun refresh() {
        loadSensorInfo()
    }

    /**
     * Upload all data for all sensors.
     */
    fun uploadAllData() {
        if (_uiState.value.isUploading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)
            try {
                val successCount = dataRepository.uploadAllData()
                if (successCount > 0) {
                    AppToast.show(context, R.string.toast_upload_all_summary, successCount)
                } else if (successCount == 0) {
                    AppToast.show(context, R.string.toast_no_data_to_upload)
                } else {
                    AppToast.show(context, R.string.toast_sensor_data_upload_error)
                }
                loadSensorInfo()
            } catch (e: Exception) {
                AppToast.show(context, R.string.toast_sensor_data_upload_error)
            } finally {
                _uiState.value = _uiState.value.copy(isUploading = false)
            }
        }
    }

    /**
     * Delete all data for all sensors.
     */
    fun deleteAllData() {
        if (_uiState.value.isDeleting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            try {
                dataRepository.deleteAllAllData()
                AppToast.show(context, R.string.toast_data_deleted)
                loadSensorInfo()
            } catch (e: Exception) {
                AppToast.show(context, R.string.toast_error_generic)
            } finally {
                _uiState.value = _uiState.value.copy(isDeleting = false)
            }
        }
    }
}
