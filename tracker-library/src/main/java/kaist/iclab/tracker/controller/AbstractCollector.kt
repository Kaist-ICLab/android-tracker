package kaist.iclab.tracker.controller

import android.util.Log
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractCollector<
        T: CollectorConfig,
        K: DataEntity>(
    val permissionManager: PermissionManagerInterface
): CollectorInterface {
    protected val TAG: String = this::class.simpleName ?: "UnnamedClass"
    override val NAME: String = extractName(this::class.simpleName ?: "UnknownCollector")

    abstract val defaultConfig: T
    private val _configFlow
        get() = MutableStateFlow<T>(defaultConfig)
    override val configFlow: StateFlow<T>
        get() = _configFlow.asStateFlow()

    protected fun initConfig(){
        _configFlow.tryEmit(defaultConfig)
    }

    @Suppress("UNCHECKED_CAST")
    override fun updateConfig(config: CollectorConfig) {
        if(_stateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
        try{
            _configFlow.tryEmit(config as T)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Invalid config type: ${config::class.simpleName}")
        }

    }
     override fun resetConfig() {
        if(_stateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
        _configFlow.tryEmit(defaultConfig)
    }



    protected val _stateFlow = MutableStateFlow(CollectorState(CollectorState.FLAG.UNAVAILABLE, "Not initialized"))
    override val stateFlow: StateFlow<CollectorState>
        get() = _stateFlow.asStateFlow()
    protected fun initState() {
        val availability = isAvailable()
        if(!availability.status) _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.UNAVAILABLE, availability.reason))
        else if(!permissionManager.isPermissionsGranted(permissions)) _stateFlow.tryEmit(
            CollectorState(CollectorState.FLAG.PERMISSION_REQUIRED))
        else _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
    }
    override fun initialize() {
        initState()
        initConfig()
    }

    /* Check whether the system allow to collect data
    * In case of sensor malfunction or broken, it would not be available.*/
    abstract fun isAvailable(): Availability

    override fun enable() {
        if (_stateFlow.value.flag == CollectorState.FLAG.DISABLED) {
            _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
        }
    }

    override fun disable() {
        if (_stateFlow.value.flag == CollectorState.FLAG.ENABLED) {
            _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
        }
    }

    override fun start() {
        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.RUNNING))
    }

    override fun stop() {
        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
    }

    /* Request required permissions to collect data */
    override fun requestPermissions(onResult: ((Boolean) -> Unit)) {
        permissionManager.request(permissions) {
            val granted = permissions.all { permission -> it[permission] == true }
            Log.d(TAG, "Permission granted: $granted")
            if(granted) _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
            else _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.PERMISSION_REQUIRED, "Permission denied"))
            onResult(granted)
        }
    }

    override var listener: ((DataEntity) -> Unit)? = null
    private fun extractName(className: String): String {
        // Replace "Collector" with an empty string
        val tmp = className.replace("Collector", "")

        // Split the name into parts based on camel case
        val parts =
            tmp.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())

        // Join the parts using whitespace and convert
        return parts.joinToString(" ")
    }
}