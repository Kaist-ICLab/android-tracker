package kaist.iclab.tracker.sensor.phone

import android.util.Log
import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.os.Build
import android.provider.Telephony
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

class MessageLogSensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    val stateStorage: StateStorage<SensorState>,
) : BaseSensor<MessageLogSensor.Config, MessageLogSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long
    ): SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val number: String,
        val messageType: String,
        val contactType: Int
    ) : SensorEntity

    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.RECEIVE_SMS
    )
    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else null
    ).toTypedArray()

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(1)
    )

    private val alarmListener = AlarmListener(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x13,
        configStateFlow.value.interval
    )

    private val mainCallback = { _: Intent? ->
        val current = System.currentTimeMillis()
        val from = current - configStateFlow.value.interval - TimeUnit.DAYS.toMillis(10)

        Log.v("MessageLogSensor", "$from $current")

        // READ SMS
        var cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms.ADDRESS,   // Sender/receiver number
                Telephony.Sms.DATE,      // Timestamp
                Telephony.Sms.TYPE,      // Type (Incoming/Outgoing)
            ),
            "${Telephony.Sms.DATE} > ?",
            arrayOf(from.toString()),
            Telephony.Sms.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val number = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                listeners.forEach { listener ->
                    listener.invoke(
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
        }
        cursor?.close()

        // READ MMS
        cursor = context.contentResolver.query(
            Telephony.Mms.CONTENT_URI,
            arrayOf(
                Telephony.Mms._ID,       // MMS ID
                Telephony.Mms.DATE,      // Timestamp
                Telephony.Mms.MESSAGE_BOX // Type (Incoming/Outgoing)
            ),
            "${Telephony.Mms.DATE} > ?",
            arrayOf(from.toString()),
            Telephony.Mms.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getLong(it.getColumnIndexOrThrow(Telephony.Mms._ID))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(Telephony.Mms.DATE)) * 1000 // MMS date is in seconds
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Mms.MESSAGE_BOX))

                listeners.forEach { listener ->
                    listener.invoke(
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
        }
        cursor?.close()

        Unit
    }

    private fun getMmsAddress(contentResolver: ContentResolver, mmsId: Long): String? {
        // Build the URI for the MMS address table using the MMS ID
        val uri = "content://mms/$mmsId/addr".toUri()
        val projection = arrayOf(Telephony.Mms.Addr.ADDRESS)

        val cursor: Cursor? = contentResolver.query(uri, projection, null,null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(Telephony.Mms.Addr.ADDRESS))
            }
        }
        return null
    }

    override fun init() {
        stateStorage.set(SensorState(SensorState.FLAG.DISABLED))
    }

    override fun onStart() {
        alarmListener.addListener(mainCallback)
    }

    override fun onStop() {
        alarmListener.removeListener(mainCallback)
    }
}