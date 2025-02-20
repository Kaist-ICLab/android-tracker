package kaist.iclab.field_tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.data.core.DataStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class AbstractMainViewModel(
    private val _collectors: Map<String, Collector>,
    private val _dataStorages: Map<String, DataStorage>
) : ViewModel(), MainViewModelInterface {

    override val collectors: Map<String, Collector>
        get() = _collectors
    override val dataStorages: Map<String, DataStorage>
        get() = _dataStorages

    private val _collectorStateFlow = MutableStateFlow<Map<String, CollectorState>>(emptyMap())
    override val collectorStateFlow: StateFlow<Map<String, CollectorState>>
        get() = _collectorStateFlow

    init {
        /* Combine state flow of collectors */
        val stateFlowMap = _collectors.mapValues { (_, collector) -> collector.collectorStateFlow }
        if (stateFlowMap.size == 1) {
            val (key, flow) = stateFlowMap.entries.first()
            flow.onEach { state ->
                _collectorStateFlow.value = mapOf(key to state)
            }.launchIn(viewModelScope)
        } else {
            combine(stateFlowMap.values) { states ->
                stateFlowMap.keys.zip(states).toMap()
            }.onEach { newStateMap ->
                _collectorStateFlow.value = newStateMap
            }.launchIn(viewModelScope)
        }
    }

    override fun enableCollector(name: String) {
        _collectors.get(name)?.enable()
    }

    override fun disableCollector(name: String) {
        _collectors.get(name)?.disable()
    }
}
