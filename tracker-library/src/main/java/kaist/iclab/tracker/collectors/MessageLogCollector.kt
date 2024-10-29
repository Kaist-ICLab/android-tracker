package kaist.iclab.tracker.collectors

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.Telephony
import kaist.iclab.tracker.triggers.AlarmTrigger
import java.util.concurrent.TimeUnit

class MessageLogCollector(
    override val context: Context
) : AbstractCollector(context) {

    var config: Config = Config(
        TimeUnit.MINUTES.toMillis(30)
    )
    data class DataEntity(
        val timestamp: Long,
        val number: String,
        val messageType: String,
        val contactType: Int
    ) : AbstractCollector.DataEntity()

    data class Config(
        val interval: Long
    ) : AbstractCollector.Config()


    // No permission required for it
    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_SMS
    )
    override val foregroundServiceTypes: Array<Int> = emptyArray()



    // Access to Battery Status might be supported for all android systems
    override fun isAvailable(): Boolean = true

    val trigger = AlarmTrigger(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x13,
        config.interval
    ){
        val current = System.currentTimeMillis()
        val from = current - config.interval - TimeUnit.MINUTES.toMillis(5)
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
                    DataEntity(
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
                    DataEntity(
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

    override fun start() {
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
    }
}