package kaist.iclab.tracker.collector.phone

import android.Manifest
import android.content.Context
import android.provider.CallLog
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.listener.AlarmListener
import java.util.concurrent.TimeUnit

class CallLogCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<CallLogCollector.Config, CallLogCollector.Entity>(permissionManager) {
    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG
    )
    override val foregroundServiceTypes: Array<Int> = emptyArray()

    data class Config(
        val interval: Long
    ): CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(1)
    )

    override fun isAvailable() =  Availability(true)

    override fun start() {
        super.start()
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
        super.stop()
    }

    val trigger = AlarmListener(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x11,
        configFlow.value.interval
    ){
        val current = System.currentTimeMillis()
        val from = current - configFlow.value.interval - TimeUnit.DAYS.toMillis(5)
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
                    Entity(
                        System.currentTimeMillis(),
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

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val duration: Long,
        val number: String,
        val type: Int
    ) : DataEntity(received)
}