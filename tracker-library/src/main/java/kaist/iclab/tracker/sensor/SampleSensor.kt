package kaist.iclab.tracker.sensor

import kaist.iclab.tracker.listener.SampleListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage

class SampleSensor(
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    stateStorage: StateStorage<SensorState>,
    override val initialConfig: Config
) : BaseSensor<SampleSensor.Config, SampleSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    override val permissions: Array<String> = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    data class Config(
        val interval: Long
    ) : SensorConfig

    data class Entity(
        val timestamp: Long
    ) : SensorEntity

    override fun init() {}

    private var listener: SampleListener? = null
    private val handleInvoke: (timestamp: Long) -> Unit = { timestamp ->
        listeners.forEach { it.invoke(Entity(timestamp)) }
    }

    override fun onStop() {
        listener?.removeListener(handleInvoke)
        listener = null
    }

    override fun onStart() {
        listener = SampleListener(configStateFlow.value.interval)
        listener?.addListener(handleInvoke)
    }
}