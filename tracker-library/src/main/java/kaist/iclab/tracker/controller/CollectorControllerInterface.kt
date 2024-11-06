package kaist.iclab.tracker.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CollectorControllerInterface {
    val collectorMap: Map<String, CollectorInterface>

    val stateFlow: StateFlow<Boolean>
    fun start()
    fun stop()

    val collectorStateFlow: Flow<Map<String, CollectorState>>

    fun enableCollector(name: String)
    fun disableCollector(name: String)


    val configFlow : Flow<Map<String, CollectorConfig>>
    fun updateConfig(config: Map<String, CollectorConfig>)
}