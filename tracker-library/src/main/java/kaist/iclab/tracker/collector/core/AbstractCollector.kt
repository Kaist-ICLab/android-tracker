package kaist.iclab.tracker.collector.core

import kaist.iclab.tracker.data.core.StateStorage
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

abstract class AbstractCollector<
        T: CollectorConfig,
        K: DataEntity>(
    val permissionManager: PermissionManager,
    private val configStorage: StateStorage<T>,
    private val stateStorage: StateStorage<CollectorState>,
    override val configClass: KClass<T>,
    override val entityClass: KClass<K>,
): Collector {
    override val ID: String = extractId(this::class.simpleName ?: "UnknownCollector")
    override val NAME: String = extractName(this::class.simpleName ?: "UnknownCollector")

    // Keep abstract
    // val permissions: Array<String>
    // val foregroundServiceTypes: Array<Int>

    /* Config-related */
    //    override val _defaultConfig: T
    override val configStateFlow: StateFlow<T>
        get() = configStorage.stateFlow
    @Suppress("UNCHECKED_CAST")
    override fun updateConfig(config: CollectorConfig) {
        if(collectorStateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
        try {
            configStorage.set(config as T)
        } catch (e: ClassCastException) {
            error("Invalid config type: ${config::class.simpleName}")
        }
    }
    override fun resetConfig() {
        updateConfig(_defaultConfig)
    }

    /* State-related */
    override val collectorStateFlow: StateFlow<CollectorState>
        get() = stateStorage.stateFlow
    /* override fun init() */
    override fun enable() {
        if(collectorStateFlow.value.flag == CollectorState.FLAG.DISABLED){
            if(
                permissionManager.permissionStateFlow.value.any { (permission, state) ->
                    permissions.contains(permission) && state != PermissionState.GRANTED
                }
            ){
                stateStorage.set(CollectorState(CollectorState.FLAG.DISABLED, "Permission required"))
            }else{
                stateStorage.set(CollectorState(CollectorState.FLAG.ENABLED))
            }
        }
    }

    override fun disable() {
        if(collectorStateFlow.value.flag == CollectorState.FLAG.ENABLED){
            stateStorage.set(CollectorState(CollectorState.FLAG.DISABLED))
        }
    }

    override fun start() {
        onStart()
        if(collectorStateFlow.value.flag == CollectorState.FLAG.ENABLED){
            stateStorage.set(CollectorState(CollectorState.FLAG.RUNNING))
        }
    }
    abstract fun onStart()

    override fun stop() {
        onStop()
        if(collectorStateFlow.value.flag == CollectorState.FLAG.RUNNING){
            stateStorage.set(CollectorState(CollectorState.FLAG.ENABLED))
        }
    }
    abstract fun onStop()

    /* Data-related */
    protected val listeners = mutableListOf<(DataEntity) -> Unit>()
    override fun addListener(listener: (DataEntity) -> Unit) {
        listeners.add(listener)
    }
    override fun removeListener(listener: (DataEntity) -> Unit) {
        listeners.remove(listener)
    }

    private fun extractName(className: String): String {
        // Replace "Collector" with an empty string
        val tmp = className.replace("Collector", "")
        // Split the name into parts based on camel case
        val parts =
            tmp.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())

        // Join the parts using whitespace and convert
        return parts.joinToString(" ").trim()
    }

    private fun extractId(className: String): String {
        // Replace "Collector" with an empty string
        val tmp = className.replace("Collector", "")
        return tmp
    }
}