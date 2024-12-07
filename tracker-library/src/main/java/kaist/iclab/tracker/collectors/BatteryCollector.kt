package kaist.iclab.tracker.collectors

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger

class BatteryCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<BatteryCollector.Config, BatteryCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config: CollectorConfig()

    override val defaultConfig = Config()

    // Access to Battery Status might be supported for all android systems
    override fun isAvailable() = Availability(true)

    override fun start() {
        super.start()
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
        super.stop()
    }

    val trigger: SystemBroadcastTrigger = SystemBroadcastTrigger(
        context,
        arrayOf(
            Intent.ACTION_BATTERY_CHANGED
        )
    ){ intent ->
        val timestamp = System.currentTimeMillis()
        listener?.invoke(Entity(
            timestamp,
            timestamp,
            intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
            intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
            intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        ))

    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val connectedType: Int,
        val status: Int,
        val level: Int,
        val temperature: Int
    ): DataEntity(received)
}