package kaist.iclab.tracker.collectors

import android.Manifest
import android.content.Context
import android.provider.CallLog
import kaist.iclab.tracker.triggers.AlarmTrigger
import java.util.concurrent.TimeUnit

class CallLogCollector(
    override val context: Context
) : AbstractCollector(context) {

    var config: Config = Config(
        TimeUnit.MINUTES.toMillis(30)
    )
    data class DataEntity(
        val timestamp: Long,
        val duration: Long,
        val number: String,
        val type: Int
    ) : AbstractCollector.DataEntity()

    data class Config(
        val interval: Long
    ) : AbstractCollector.Config()


    // No permission required for it
    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG
    )
    override val foregroundServiceTypes: Array<Int> = emptyArray()



    // Access to Battery Status might be supported for all android systems
    override fun isAvailable(): Boolean = true

    val trigger = AlarmTrigger(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x11,
        config.interval
    ){
        val current = System.currentTimeMillis()
        val from = current - config.interval - TimeUnit.MINUTES.toMillis(5)
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
            ),
            "${CallLog.Calls.DATE} BETWEEN ? AND ?",
            arrayOf(from.toString(), current.toString()),
            CallLog.Calls.DATE + " DESC"
        )
        cursor?.use {
            while (it.moveToNext()) {
                val timestamp = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                val duration = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                listener?.invoke(
                    DataEntity(
                        timestamp,
                        duration.toLong(),
                        number,
                        type
                    )
                )
            }
        }
        cursor?.close()
    }

    override fun start() {
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
    }
}