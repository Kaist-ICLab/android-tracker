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


class AlarmListener(
    private val context: Context,
    private val actionName: String,
    private val actionCode: Int,
    private val actionIntervalInMilliseconds: Long,
): Listener<Intent?> {
    // Stores receiver objects to Map, so they can be managed with listeners instead of receivers
    private val receivers = mutableMapOf<Int, BroadcastReceiver>()

    companion object {
        const val TAG = "AlarmTrigger"
    }

    override fun init() {}

    private val intent by lazy {
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

    override fun addListener(listener: (Intent?) -> Unit) {
        val hash = listener.hashCode()
        assert(!receivers.contains(hash))

        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "receiver:onReceive")
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

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            actionIntervalInMilliseconds,
            intent
        )

        Log.d(TAG, "register ALARM: $actionIntervalInMilliseconds")
    }

    override fun removeListener(listener: (Intent?) -> Unit) {
        val hash = listener.hashCode()
        assert(receivers.contains(hash))
        val receiver = receivers[hash]

        context.unregisterReceiver(receiver)
        receivers.remove(hash)
        alarmManager.cancel(intent)
    }

    fun getPendingIntent(): PendingIntent {
        return intent
    }
}