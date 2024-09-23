package dev.iclab.tracker.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractMainViewModel: ViewModel() {
    abstract val _isRunningState: MutableStateFlow<Boolean>
    val isRunningState: StateFlow<Boolean>
        get() = _isRunningState.asStateFlow()

    abstract val collectorList: List<String>
    abstract val _collectorConfigState: MutableStateFlow<Map<String, Boolean>>
    val collectorConfigState: StateFlow<Map<String, Boolean>>
        get() = _collectorConfigState.asStateFlow()

    abstract fun start()
    abstract fun stop()
    abstract fun enable(name: String)
    abstract fun disable(name: String)

    abstract fun sync()
    abstract fun delete()
}