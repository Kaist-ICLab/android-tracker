package kaist.iclab.tracker.collector.phone

import android.content.Context
import android.content.Intent
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.listener.BroadcastListener

class ScreenCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<ScreenCollector.Config, ScreenCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config: CollectorConfig()

    override val defaultConfig = Config()

    // Access to Battery Status might be supported for all android systems
    override fun isAvailable() = Availability(true)


    override fun start() {
        super.start()
        broadcastTrigger.register()
    }

    override fun stop() {
        broadcastTrigger.unregister()
        super.stop()
    }

    private val broadcastTrigger = BroadcastListener(
        context,
        arrayOf(
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_USER_PRESENT
        )
    ) { intent ->
        val timestamp = System.currentTimeMillis()
        listener?.invoke(
            Entity(
                timestamp,
                timestamp,
                intent.action ?: "UNKNOWN"
            )
        )
    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val type: String,
    ) : DataEntity(received)
}