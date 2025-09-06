package kaist.iclab.tracker.listener

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import kaist.iclab.tracker.listener.core.Listener

/**
 * AlarmListener that only schedules once.
 * Unlike AlarmListener, scheduleNextAlarm() must be invoked to start the alarm going.
 */
class SingleAlarmListener(
    private val context: Context,
    private val actionName: String,
    private val actionCode: Int,
    private val isExact: Boolean = false,
): Listener<Intent?> {
    companion object {
        private val TAG = SingleAlarmListener::class.simpleName
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
                listener(intent)
            }
        }
        receivers[hash] = receiver

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            /*
            * From Tiramisu, we need to specify the receiver exported or not
            * One of RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED should be specified when a receiver isn't being registered exclusively for system broadcasts
            * */
            context.registerReceiver(receiver, IntentFilter(actionName), Context.RECEIVER_EXPORTED)

        } else {
            context.registerReceiver(receiver, IntentFilter(actionName))
        }
    }

    override fun removeListener(listener: (Intent?) -> Unit) {
        val hash = listener.hashCode()
        assert(receivers.contains(hash))
        val receiver = receivers[hash]

        context.unregisterReceiver(receiver)
        receivers.remove(hash)

        if(receivers.isNotEmpty()) return
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleNextAlarm(intervalInTimeMillis: Long) {
        val canScheduleExactAlarms = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true
        if(canScheduleExactAlarms && isExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalInTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalInTimeMillis,
                pendingIntent
            )
        }
    }
}