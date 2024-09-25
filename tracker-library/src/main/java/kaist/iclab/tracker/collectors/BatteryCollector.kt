package kaist.iclab.tracker.collectors

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.filters.applyFilters
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger

class BatteryCollector(
    override val context: Context,
    override val database: DatabaseInterface
) : AbstractCollector(
    context, database
) {
    companion object {
        const val NAME = "BATTERY"
        const val TAG = "BatteryCollector"
        val action = "android.intent.action.BATTERY_CHANGED"
    }

    override val NAME: String
        get() = Companion.NAME

    // No permission required for it
    override val permissions: Array<String> = emptyArray()

    lateinit var trigger: SystemBroadcastTrigger

    // Access to Battery Status might be supported for all android systems
    override fun isAvailable(): Boolean = true

    override fun start() {
        trigger = SystemBroadcastTrigger(
            context,
            arrayOf(action)
        ) {
            database.insert(NAME, listener(it).applyFilters(filters))
        }
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
    }

    fun listener(intent: Intent): Map<String, Any> {
        if (action != intent.action) {
            Log.e(TAG, "Invalid action: ${intent.action}")
            return mapOf()
        }
        val timestamp = System.currentTimeMillis()

        return mapOf(
            "timestamp" to timestamp,
            "level" to intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
            "temperature" to intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
            "voltage" to intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
            "health" to intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1),
            "pluggedType" to intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1),
            "status" to intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
        )
    }
}