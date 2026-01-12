package kaist.iclab.mobiletracker.viewmodels.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.repository.DataRepository
import kaist.iclab.mobiletracker.repository.DateFilter
import kaist.iclab.mobiletracker.repository.SensorDetailInfo
import kaist.iclab.mobiletracker.repository.SensorRecord
import kaist.iclab.mobiletracker.repository.SortOrder
import kaist.iclab.mobiletracker.utils.AppToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Sensor Detail screen.
 */
data class SensorDetailUiState(
    val isLoading: Boolean = true,
    val sensorInfo: SensorDetailInfo? = null,
    val records: List<SensorRecord> = emptyList(),
    val filteredCount: Int = 0,
    val dateFilter: DateFilter = DateFilter.ALL_TIME,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
    val hasMoreRecords: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val isUploading: Boolean = false,
    val isDeleting: Boolean = false
)

/**
 * ViewModel for the Sensor Detail screen.
 */
class SensorDetailViewModel(
    private val dataRepository: DataRepository,
    private val sensorId: String,
    private val context: Context
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow(SensorDetailUiState())
    val uiState: StateFlow<SensorDetailUiState> = _uiState.asStateFlow()

    private var currentOffset = 0

    init {
        loadSensorDetail()
    }

    /**
     * Load sensor detail info and initial records.
     */
    fun loadSensorDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val info = dataRepository.getSensorDetailInfo(sensorId)
                val dateFilter = _uiState.value.dateFilter
                val sortOrder = _uiState.value.sortOrder
                val filteredCount = dataRepository.getSensorRecordCount(sensorId, dateFilter)
                
                currentOffset = 0
                val records = dataRepository.getSensorRecords(
                    sensorId = sensorId,
                    dateFilter = dateFilter,
                    sortOrder = sortOrder,
                    limit = PAGE_SIZE,
                    offset = 0
                )
                
                _uiState.value = SensorDetailUiState(
                    isLoading = false,
                    sensorInfo = info,
                    records = records,
                    filteredCount = filteredCount,
                    dateFilter = dateFilter,
                    sortOrder = sortOrder,
                    hasMoreRecords = records.size >= PAGE_SIZE && currentOffset + records.size < filteredCount
                )
                currentOffset = records.size
            } catch (e: Exception) {
                _uiState.value = SensorDetailUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load sensor data"
                )
            }
        }
    }

    /**
     * Load more records (pagination).
     */
    fun loadMoreRecords() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreRecords) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            try {
                val moreRecords = dataRepository.getSensorRecords(
                    sensorId = sensorId,
                    dateFilter = _uiState.value.dateFilter,
                    sortOrder = _uiState.value.sortOrder,
                    limit = PAGE_SIZE,
                    offset = currentOffset
                )
                
                currentOffset += moreRecords.size
                val allRecords = _uiState.value.records + moreRecords
                
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    records = allRecords,
                    hasMoreRecords = moreRecords.size >= PAGE_SIZE && currentOffset < _uiState.value.filteredCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }

    /**
     * Change date filter.
     */
    fun setDateFilter(filter: DateFilter) {
        if (filter == _uiState.value.dateFilter) return
        _uiState.value = _uiState.value.copy(dateFilter = filter)
        loadSensorDetail()
    }

    /**
     * Change sort order.
     */
    fun setSortOrder(order: SortOrder) {
        if (order == _uiState.value.sortOrder) return
        _uiState.value = _uiState.value.copy(sortOrder = order)
        loadSensorDetail()
    }

    /**
     * Toggle sort order.
     */
    fun toggleSortOrder() {
        val newOrder = if (_uiState.value.sortOrder == SortOrder.NEWEST_FIRST) {
            SortOrder.OLDEST_FIRST
        } else {
            SortOrder.NEWEST_FIRST
        }
        setSortOrder(newOrder)
    }

    /**
     * Delete a specific record.
     */
    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                dataRepository.deleteRecord(sensorId, recordId)
                // Remove from local list and refresh counts
                val updatedRecords = _uiState.value.records.filter { it.id != recordId }
                _uiState.value = _uiState.value.copy(
                    records = updatedRecords,
                    filteredCount = _uiState.value.filteredCount - 1
                )
                // Also update sensor info
                val updatedInfo = _uiState.value.sensorInfo?.copy(
                    totalRecords = (_uiState.value.sensorInfo?.totalRecords ?: 1) - 1
                )
                _uiState.value = _uiState.value.copy(sensorInfo = updatedInfo)
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }

    /**
     * Upload all data for the current sensor.
     */
    fun uploadSensorData() {
        if (_uiState.value.isUploading) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)
            try {
                val result = dataRepository.uploadSensorData(sensorId)
                when {
                    result > 0 -> {
                        AppToast.show(context, R.string.toast_sensor_data_uploaded)
                        // Refresh to update sync timestamp
                        loadSensorDetail()
                    }
                    result == 0 -> {
                        AppToast.show(context, R.string.toast_no_data_to_upload)
                    }
                    else -> {
                        AppToast.show(context, R.string.toast_sensor_data_upload_error)
                    }
                }
            } catch (e: Exception) {
                Log.e("SensorDetailViewModel", "Error uploading data", e)
                AppToast.show(context, R.string.toast_sensor_data_upload_error)
            } finally {
                _uiState.value = _uiState.value.copy(isUploading = false)
            }
        }
    }

    /**
     * Delete all data for the current sensor.
     */
    fun deleteAllSensorData() {
        if (_uiState.value.isDeleting) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            try {
                dataRepository.deleteAllSensorData(sensorId)
                AppToast.show(context, R.string.toast_sensor_data_deleted)
                // Refresh list
                loadSensorDetail()
            } catch (e: Exception) {
                Log.e("SensorDetailViewModel", "Error deleting data", e)
                AppToast.show(context, R.string.toast_error_generic)
            } finally {
                _uiState.value = _uiState.value.copy(isDeleting = false)
            }
        }
    }
}
