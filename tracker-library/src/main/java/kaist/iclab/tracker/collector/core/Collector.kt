package kaist.iclab.tracker.collector.core

import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

interface Collector {
    val ID: String
    val NAME: String

    val permissions: Array<String>
    val foregroundServiceTypes: Array<Int>

    /* Config-related */
    val _defaultConfig: CollectorConfig
    val configStateFlow: StateFlow<CollectorConfig>
    val configClass: KClass<out CollectorConfig>
    fun updateConfig(config: CollectorConfig)
    fun resetConfig()

    /* State-related */
    val collectorStateFlow: StateFlow<CollectorState>
    /* UNAVAILABLE => Check*/
    fun init()
    /* DISABLED => READY */
    fun enable()
    /* READY => DISABLED */
    fun disable()
    /* Start collector to collect data: READY => RUNNING */
    fun start()
    /* Stop collector to stop collecting data: RUNNING => READY */
    fun stop()

    /* Data-related */
    val entityClass: KClass<out DataEntity>
    fun addListener(listener: (DataEntity) -> Unit)
    fun removeListener(listener: (DataEntity) -> Unit)
}