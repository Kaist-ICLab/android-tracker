package kaist.iclab.tracker.collector.core

import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

interface CollectorInterface {
    val NAME: String


    val permissions: Array<String>
    val foregroundServiceTypes: Array<Int>

    val configFlow: StateFlow<CollectorConfig>
    fun getConfigClass(): KClass<out CollectorConfig>
    fun updateConfig(config: CollectorConfig)
    fun resetConfig()

    val stateFlow: StateFlow<CollectorState>


    /* UNAVAILABLE => Check*/
    fun initialize()
    /* DISABLED => READY */
    fun enable()
    /* READY => DISABLED */
    fun disable()
    /* Start collector to collect data: READY => RUNNING */
    fun start()
    /* Stop collector to stop collecting data: RUNNING => READY */
    fun stop()

    /* Based on the data, define action */
    var listener: ((DataEntity) -> Unit)?
    fun getEntityClass(): KClass<out DataEntity>


    //    /* Request permission to collect data: PERMISSION_REQUIRED => READY */
//    fun requestPermissions(onResult: ((Boolean) -> Unit))
}