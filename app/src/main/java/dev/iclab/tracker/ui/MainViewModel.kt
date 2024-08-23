package dev.iclab.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iclab.tracker.CollectorControllerInterface
import dev.iclab.tracker.PermissionManager
import dev.iclab.tracker.database.DatabaseInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(
    private val collectorController: CollectorControllerInterface,
    private val database: DatabaseInterface,
    private val permissionManager: PermissionManager
): ViewModel(), MainViewModelInterface {

    init {
        viewModelScope.launch {
            collectorController.isRunningFlow().collect {
                _isRunningState.value = it
            }
        }
        viewModelScope.launch {
            collectorController.getCollectorConfigChange().collect {
                _collectorConfigState.value = it
            }
        }
    }

    override val collectorList = collectorController.getCollectorsList()

    override val _isRunningState = MutableStateFlow(false)
    override val isRunningState: StateFlow<Boolean>
        get() = _isRunningState.asStateFlow()

    override val _collectorConfigState: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(mapOf())
    override val collectorConfigState: StateFlow<Map<String, Boolean>>
        get() = _collectorConfigState.asStateFlow()

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