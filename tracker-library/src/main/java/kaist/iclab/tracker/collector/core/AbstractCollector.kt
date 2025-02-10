package kaist.iclab.tracker.collector.core

import android.util.Log
import kaist.iclab.tracker.collector.phone.SampleCollector.Companion.defaultConfig
import kaist.iclab.tracker.collector.phone.SampleCollector.Config
import kaist.iclab.tracker.collector.phone.SampleCollector.Entity
import kaist.iclab.tracker.data.core.DataStorageInterface
import kaist.iclab.tracker.data.core.SingletonStorageInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.permission.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

abstract class AbstractCollector<
        T: CollectorConfig,
        K: DataEntity>(
    val permissionManager: PermissionManagerInterface,
    private val configStorage: SingletonStorageInterface<T>,
    private val stateStorage: SingletonStorageInterface<CollectorState>,
): CollectorInterface {
    protected val TAG: String = this::class.simpleName ?: "UnnamedClass"
    override val NAME: String = extractName(this::class.simpleName ?: "UnknownCollector")

    abstract val _defaultConfig: T
    private lateinit var _configFlow: MutableStateFlow<T>
    override val configFlow: StateFlow<T>
        get() = configStorage.stateFlow
    override fun resetConfig() {
        configStorage.set(_defaultConfig)
    }
    @Suppress("UNCHECKED_CAST")
    override fun updateConfig(config: CollectorConfig) {
        if(stateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
        try {
            configStorage.set(config as T)
        } catch (e: ClassCastException) {
            error("Invalid config type: ${config::class.simpleName}")
        }
    }

//    protected fun initConfig(){
//        _configFlow = MutableStateFlow(defaultConfig as T)
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    override fun updateConfig(config: CollectorConfig) {
//        if(_stateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
//        try{
//            Log.d(TAG, "updateConfig(): ${config}")
//            _configFlow.value = config as T
//            Log.d(TAG, "updateConfig(): ${_configFlow.value} / ${_configFlow.value.equals(config)}")
//        } catch (e: ClassCastException) {
//            Log.e(TAG, "Invalid config type: ${config::class.simpleName}")
//        }
//    }
//     override fun resetConfig() {
//        if(_stateFlow.value.flag == CollectorState.FLAG.RUNNING) throw IllegalStateException("Cannot update config while running.")
//        _configFlow.tryEmit(defaultConfig as T)
//    }

    companion object {
        val defaultState = CollectorState(CollectorState.FLAG.UNAVAILABLE, "Not initialized")
    }
    override val stateFlow: StateFlow<CollectorState>
        get() = stateStorage.stateFlow
    private fun initState() {
        val availability = isAvailable()
        if(!availability.status) stateStorage.set(CollectorState(CollectorState.FLAG.UNAVAILABLE, availability.reason))
        else stateStorage.set(CollectorState(CollectorState.FLAG.DISABLED))
    }

//    protected val _stateFlow = MutableStateFlow(CollectorState(CollectorState.FLAG.UNAVAILABLE, "Not initialized"))
//    override val stateFlow: StateFlow<CollectorState>
//        get() = _stateFlow

//    protected fun initState() {
//        val availability = isAvailable()
//        if(!availability.status) _stateFlow.value = CollectorState(CollectorState.FLAG.UNAVAILABLE, availability.reason)
////        else if(
////            permissions.any { permission->
////                permissionManager.permissionStateFlow.value[permission] != PermissionState.GRANTED
////            }
////        ) {
////            _stateFlow.value = CollectorState(CollectorState.FLAG.PERMISSION_REQUIRED)
////        }
//        else _stateFlow.value = CollectorState(CollectorState.FLAG.DISABLED)
//    }
    override fun initialize() {
        initState()
//        initConfig()
    }

    /* Check whether the system allow to collect data
    * In case of sensor malfunction or broken, it would not be available.*/
    abstract fun isAvailable(): Availability

    override fun enable() {
        Log.d("AbstractCollector", "enable(): ${stateFlow.value.flag}")
        if(stateFlow.value.flag == CollectorState.FLAG.DISABLED){
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
        if(stateFlow.value.flag == CollectorState.FLAG.ENABLED){
            stateStorage.set(CollectorState(CollectorState.FLAG.DISABLED))
        }
    }

    override fun start() {
        if(stateFlow.value.flag == CollectorState.FLAG.ENABLED){
            stateStorage.set(CollectorState(CollectorState.FLAG.RUNNING))
        }
    }

    override fun stop() {
        if(stateFlow.value.flag == CollectorState.FLAG.RUNNING){
            stateStorage.set(CollectorState(CollectorState.FLAG.ENABLED))
        }
    }

    //    override fun enable() {
//        Log.d(TAG, "enable(): ${_stateFlow.value.flag}")
//        if (_stateFlow.value.flag == CollectorState.FLAG.DISABLED){
//            if(
//                permissions.any { permission->
//                    permissionManager.permissionStateFlow.value[permission] != PermissionState.GRANTED
//                }
//            ){
//                _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED, "Permission required"))
//            }else{
//
//                _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
//            }
//        }
//    }
//
//    override fun disable() {
//        if (_stateFlow.value.flag == CollectorState.FLAG.ENABLED) {
//            _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
//        }
//    }
//
//    override fun start() {
//        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.RUNNING))
//    }
//
//    override fun stop() {
//        _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.ENABLED))
//    }

    /* Request required permissions to collect data */
//    override fun requestPermissions(onResult: ((Boolean) -> Unit)) {
//        permissionManager.request(permissions) { granted ->
//            Log.d(TAG, "Permission granted: $granted")
//            if(granted) _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.DISABLED))
//            else _stateFlow.tryEmit(CollectorState(CollectorState.FLAG.PERMISSION_REQUIRED, "Permission denied"))
//            onResult(granted)
//        }
//    }

    override var listener: ((DataEntity) -> Unit)? = null
    private fun extractName(className: String): String {
        // Replace "Collector" with an empty string
        val tmp = className.replace("Collector", "")

        // Split the name into parts based on camel case
        val parts =
            tmp.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())

        // Join the parts using whitespace and convert
        return parts.joinToString(" ").trim()
    }
}