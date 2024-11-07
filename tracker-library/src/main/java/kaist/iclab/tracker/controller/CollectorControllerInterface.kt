package kaist.iclab.tracker.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CollectorControllerInterface {
    val stateFlow: StateFlow<Boolean>
    fun start()
    fun stop()


    fun collectorStateFlow(): Flow<Map<String, CollectorState>>
    fun initializeCollectors(
        collectorMap: Map<String, CollectorInterface>
    )
    fun enableCollector(name: String)
    fun disableCollector(name: String)


    fun configFlow() : Flow<Map<String, CollectorConfig>>
    fun updateConfig(config: Map<String, CollectorConfig>)
}