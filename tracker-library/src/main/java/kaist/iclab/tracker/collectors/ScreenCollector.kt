package kaist.iclab.tracker.collectors

import android.content.Context
import android.content.Intent
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger

class ScreenCollector(
    override val context: Context
) : AbstractCollector(context) {

    data class DataEntity(
        val timestamp: Long,
        val type: String,
    ) : AbstractCollector.DataEntity()

    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    override fun isAvailable(): Boolean = true


    private val broadcastTrigger = SystemBroadcastTrigger(
        context,
        arrayOf(
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_USER_PRESENT
        )
    ) { intent ->
        listener?.invoke(
            DataEntity(
                System.currentTimeMillis(),
                intent.action ?: "UNKNOWN"
            )
        )
    }


    override fun start() {
        broadcastTrigger.register()
    }

    override fun stop() {
        broadcastTrigger.unregister()
    }
}