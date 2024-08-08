package dev.iclab.tracker.collectors

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.filters.applyFilters
import dev.iclab.tracker.triggers.SystemBroadcastTrigger

class BatteryCollector(
    override val context: Context,
    override val database: DatabaseInterface
) : AbstractCollector(
    context, database
) {
    companion object {
        const val TAG = "BatteryCollector"
        val actions = arrayOf(
            "android.intent.action.BATTERY_CHANGED"
        )
    }

    lateinit var trigger: SystemBroadcastTrigger

    // Collector is supported for all android systems
    override fun isAvailable(): Boolean = true

    // Collector does not require any permissions
    override suspend fun enable(): Boolean = true

    fun listener(intent: Intent): Map<String, Any> {
        if (!actions.contains(intent.action)) {
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

    override fun start() {
        trigger = SystemBroadcastTrigger(
            context,
            arrayOf("android.intent.action.BATTERY_CHANGED")
        ) {
            database.insert("battery", listener(it).applyFilters(filters))
        }
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}