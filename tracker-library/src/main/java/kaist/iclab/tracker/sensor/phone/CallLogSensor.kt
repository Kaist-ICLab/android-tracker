package kaist.iclab.tracker.sensor.phone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.provider.CallLog
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.TimeUnit

class CallLogSensor(
    private val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>
) : BaseSensor<CallLogSensor.Config, CallLogSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long
    ): SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val duration: Long,
        val number: String,
        val type: Int
    ): SensorEntity

    override val permissions: Array<String> = listOfNotNull(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL
        } else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
        } else null
    ).toTypedArray()

    private val alarmListener = AlarmListener(
        context,
        "kaist.iclab.tracker.${NAME}_REQUEST",
        0x11,
        configStateFlow.value.interval
    )

    private val mainCallback = { _: Intent? ->
        val current = System.currentTimeMillis()
        val from = current - configStateFlow.value.interval - TimeUnit.DAYS.toMillis(5)
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
                listeners.forEach { listener ->
                    listener.invoke(
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
        }
        cursor?.close()
        Unit
    }

    override fun init() {
        stateStorage.set(SensorState(SensorState.FLAG.DISABLED, ""))
    }

    override fun onStart() {
        alarmListener.addListener(mainCallback)
    }

    override fun onStop() {
        alarmListener.removeListener(mainCallback)
    }
}