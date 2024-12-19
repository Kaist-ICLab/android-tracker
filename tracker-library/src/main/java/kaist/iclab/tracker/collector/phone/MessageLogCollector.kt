package kaist.iclab.tracker.collector.phone

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.listener.AlarmListener
import java.util.concurrent.TimeUnit

class MessageLogCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<MessageLogCollector.Config, MessageLogCollector.Entity>(permissionManager) {

    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_SMS
    )
    override val foregroundServiceTypes: Array<Int> = emptyArray()

    data class Config(
        val interval: Long
    ): CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(1)
    )

    override fun start() {
        super.start()
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
        super.stop()
    }

    // Access to Battery Status might be supported for all android systems
    override fun isAvailable() = Availability(true)

    val trigger = AlarmListener(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x13,
        configFlow.value.interval
    ){
        val current = System.currentTimeMillis()
        val from = current - configFlow.value.interval - TimeUnit.DAYS.toMillis(1)
        var cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms.ADDRESS,   // Sender/receiver number
                Telephony.Sms.DATE,      // Timestamp
                Telephony.Sms.TYPE,      // Type (Incoming/Outgoing)
            ),
            "${Telephony.Sms.DATE} BETWEEN ? AND ?",
            arrayOf(from.toString(), current.toString()),
            Telephony.Sms.DATE + " DESC"
        )
        cursor?.use {
            while (it.moveToNext()) {
                val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val number = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                listener?.invoke(
                    Entity(
                        System.currentTimeMillis(),
                        timestamp,
                        number,
                        "SMS",
                        type
                    )
                )
            }
        }
        cursor?.close()
        cursor = context.contentResolver.query(
            Telephony.Mms.CONTENT_URI,
            arrayOf(
                Telephony.Mms._ID,       // MMS ID
                Telephony.Mms.DATE,      // Timestamp
                Telephony.Mms.MESSAGE_BOX // Type (Incoming/Outgoing)
            ),
            "${Telephony.Mms.DATE} BETWEEN ? AND ?",
            arrayOf(from.toString(), current.toString()),
            Telephony.Mms.DATE + " DESC"
        )
        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getLong(it.getColumnIndexOrThrow(Telephony.Mms._ID))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Mms.DATE)) * 1000 // MMS date is in seconds
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Mms.MESSAGE_BOX))

                listener?.invoke(
                    Entity(
                        System.currentTimeMillis(),
                        timestamp,
                        getMmsAddress(context.contentResolver, number) ?: "UNKNOWN",
                        "MMS",
                        type
                    )
                )
            }
        }
        cursor?.close()
    }

    fun getMmsAddress(contentResolver: ContentResolver, mmsId: Long): String? {
        // Build the URI for the MMS address table using the MMS ID
        val uri = Uri.parse("content://mms/$mmsId/addr")
        val projection = arrayOf(Telephony.Mms.Addr.ADDRESS)

        val cursor: Cursor? = contentResolver.query(uri, projection, null,null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(Telephony.Mms.Addr.ADDRESS))
            }
        }
        return null
    }


    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val number: String,
        val messageType: String,
        val contactType: Int
    ) : DataEntity(received)
}