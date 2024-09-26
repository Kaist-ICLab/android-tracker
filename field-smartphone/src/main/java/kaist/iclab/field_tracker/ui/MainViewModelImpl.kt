package kaist.iclab.field_tracker.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainViewModelImpl(
    private val collectorController: CollectorControllerInterface,
    private val database: DatabaseInterface,
    private val permissionManager: PermissionManagerInterface
): AbstractMainViewModel() {

    companion object{
        const val TAG = "MainViewModelImpl"
    }

    override val _isRunningState = MutableStateFlow(false)
    override val _collectorConfigState: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(mapOf())

    init {
        Log.d(TAG, "isRunning: ${_isRunningState.value}")
        Log.d(TAG, "collectorConfig: ${_collectorConfigState.value}")
        viewModelScope.launch {
            Log.d(TAG,"INITIALIZED")
            collectorController.isRunningFlow().collect {
                Log.d(TAG, "isRunningState: $it")
                _isRunningState.value = it
            }
        }
        viewModelScope.launch {
            Log.d(TAG,"INITIALIZED COLLECTOR CONFIG")
            collectorController.getCollectorConfigChange().collect {
                Log.d(TAG, "CollectorConfig: $it")
                _collectorConfigState.value = it
            }
        }
    }

    override val collectorList = collectorController.getCollectorsList()

    override fun start() {
        collectorController.start()
    }

    override fun stop() {
        collectorController.stop()
    }

    override fun enable(name: String) {
        collectorController.enable(name, permissionManager)
    }

    override fun disable(name: String) {
        collectorController.disable(name)
    }

    override fun sync() {
        database.sync()
    }

    override fun delete() {
        database.deleteAll()
    }
}