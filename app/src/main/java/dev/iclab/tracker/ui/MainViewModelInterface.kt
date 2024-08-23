package dev.iclab.tracker.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface MainViewModelInterface {
    val _isRunningState: MutableStateFlow<Boolean>
    val isRunningState: StateFlow<Boolean>
        get() = _isRunningState.asStateFlow()

    val collectorList: List<String>
    val _collectorConfigState: MutableStateFlow<Map<String, Boolean>>
    val collectorConfigState: StateFlow<Map<String, Boolean>>

    fun start()
    fun stop()
    fun enable(name: String)
    fun disable(name: String)

    fun sync()
    fun delete()
}