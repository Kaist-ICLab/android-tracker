package kaist.iclab.tracker.sensor.phone

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import kaist.iclab.tracker.listener.BroadcastListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage

class BatterySensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    stateStorage: StateStorage<SensorState>,
) : BaseSensor<BatterySensor.Config, BatterySensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    class Config: SensorConfig
    data class Entity(
        val received: Long,
        val timestamp: Long,
        val connectedType: Int,
        val status: Int,
        val level: Int,
        val temperature: Int
    ): SensorEntity

    override val defaultConfig = Config()

    override val permissions = listOfNotNull<String>().toTypedArray()
    // May need SYSTEM_EXEMPTED? (by chatGPT)
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    private val broadcastListener: BroadcastListener = BroadcastListener(
        context,
        arrayOf(
            Intent.ACTION_BATTERY_CHANGED
        )
    )

    private val mainCallback = { intent: Intent? ->
        if(intent == null)
            throw NullPointerException("Intent does not exist!")

        val timestamp = System.currentTimeMillis()
        listeners.forEach { listener ->
            listener.invoke(
                Entity(
                    timestamp,
                    timestamp,
                    intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
                    intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
                    intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                    intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
                )
            )
        }
    }

    override fun init() { }

    override fun onStart() {
        broadcastListener.addListener(mainCallback)
    }

    override fun onStop() {
        broadcastListener.removeListener(mainCallback)
    }
}