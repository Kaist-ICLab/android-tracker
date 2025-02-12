package kaist.iclab.tracker.collector.phone

import android.Manifest
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.data.core.StateStorage
import kaist.iclab.tracker.listener.TestListener
import kaist.iclab.tracker.permission.PermissionManager

class SampleCollector(
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<CollectorState>,
    defaultConfig: Config
) :
    AbstractCollector<SampleCollector.Config, SampleCollector.Entity>(
        permissionManager, configStorage, stateStorage, Config::class, Entity::class
    ) {
    override val permissions: Array<String> = listOfNotNull<String>(
        Manifest.permission.BODY_SENSORS
    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()


    data class Config(
        val interval: Long
    ) : CollectorConfig {
        override fun copy(property: String, setValue: String): Config {
            return when (property) {
                "interval" -> this.copy(interval = setValue.toLong())
                else -> error("Unknown property $property")
            }
        }
    }

    override val _defaultConfig: CollectorConfig = defaultConfig

    data class Entity(
        val timestamp: Long
    ) : DataEntity

    override fun init() {}

    private var listener: TestListener? = null
    private val handleInvoke: (timestamp: Long) -> Unit = { timestamp ->
        listeners.forEach { it.invoke(Entity(timestamp)) }
    }

    override fun onStop() {
        listener?.removeListener(handleInvoke)
        listener = null
    }

    override fun onStart() {
        listener = TestListener(configStateFlow.value.interval)
        listener?.addListener(handleInvoke)
    }
}