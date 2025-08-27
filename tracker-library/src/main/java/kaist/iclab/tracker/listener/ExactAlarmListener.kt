package kaist.iclab.tracker.listener

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import kaist.iclab.tracker.listener.core.Listener

class ExactAlarmListener(
    private val context: Context,
    private val actionName: String,
    private val actionCode: Int,
    private val actionIntervalInMilliseconds: Long,
): Listener<Intent?> {
    companion object {
        private val TAG = ExactAlarmListener::class.simpleName
    }

    private val receivers = mutableMapOf<Int, BroadcastReceiver>()

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            actionCode,
            Intent(actionName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val alarmManager by lazy {
        context.getSystemService<AlarmManager>()!!
    }

    override fun init() {}

    override fun addListener(listener: (Intent?) -> Unit) {
        val hash = listener.hashCode()
        assert(!receivers.contains(hash))

        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val canScheduleExactAlarms = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true

                if(canScheduleExactAlarms) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + actionIntervalInMilliseconds,
                        pendingIntent
                    )
                }

                listener(intent)
            }
        }
        receivers[hash] = receiver

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + actionIntervalInMilliseconds,
            pendingIntent
        )

        Log.d(TAG, "register ALARM: $actionIntervalInMilliseconds")
    }

    override fun removeListener(listener: (Intent?) -> Unit) {
        val hash = listener.hashCode()
        assert(receivers.contains(hash))
        val receiver = receivers[hash]

        context.unregisterReceiver(receiver)
        receivers.remove(hash)
        alarmManager.cancel(pendingIntent)
    }
}