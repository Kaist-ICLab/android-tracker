package kaist.iclab.field_tracker.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainViewModelImpl(
    private val collectorController: CollectorControllerInterface,
    private val permissionManager: PermissionManagerInterface,
    private val database: DatabaseInterface,
    override val collectorMap: Map<String, AbstractCollector>
) : AbstractMainViewModel() {

    companion object {
        const val TAG = "MainViewModelImpl"
    }

    override val _isRunningState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val _enabledCollectors: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(
        collectorMap.map { it.key to false }.toMap()
    )


    init {
        Log.d(TAG, "isRunning: ${_isRunningState.value}")
        viewModelScope.launch {
            Log.d(TAG, "INITIALIZED")
            collectorController.isRunningFlow().collect {
                Log.d(TAG, "isRunningState: $it")
                _isRunningState.value = it
            }
        }
        viewModelScope.launch {
            database.getConfigFlow().collect {
                Log.d(TAG, "enabledCollectors: $it")
                _enabledCollectors.value = it
            }
        }
    }

    override fun start() {
        collectorController.start()
    }

    override fun stop() {
        collectorController.stop()
    }

    override fun enable(name: String) {
        collectorMap[name]?.let { collector->
            collectorController.add(collector)
            collector.enable(permissionManager){
                if(it){
                    _enabledCollectors.value = _enabledCollectors.value.toMutableMap().apply {
                        this[name] = true
                    }
                }
            }

        }
    }

    override fun disable(name: String) {
        collectorMap[name]?.let { collector->
            collectorController.remove(collector)
        }
    }

    override fun sync() {
        throw NotImplementedError("Not implemented")
    }

    override fun delete() {
        throw NotImplementedError("Not implemented")
    }
}