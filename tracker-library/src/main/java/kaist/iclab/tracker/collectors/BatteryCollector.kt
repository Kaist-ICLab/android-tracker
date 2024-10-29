package kaist.iclab.tracker.collectors

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger

class BatteryCollector(
    override val context: Context
) : AbstractCollector(context) {

    data class DataEntity(
        val timestamp: Long,
        val connectedType: Int,
        val status: Int,
        val level: Int,
        val temperature: Int
    ): AbstractCollector.DataEntity()

    // No permission required for it
    override val permissions: Array<String> = emptyArray()
    override val foregroundServiceTypes: Array<Int> = emptyArray()

    val trigger: SystemBroadcastTrigger = SystemBroadcastTrigger(
        context,
        arrayOf(
            Intent.ACTION_BATTERY_CHANGED
        )
    ){ intent ->
        listener?.invoke(DataEntity(
            System.currentTimeMillis(),
            intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
            intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
            intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        ))

    }

    // Access to Battery Status might be supported for all android systems
    override fun isAvailable(): Boolean = true

    override fun start() {
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
    }
}