package kaist.iclab.tracker.triggers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AlarmTrigger(
    private val context: Context,
    private val ACTION_NAME: String,
    private val ACTION_CODE: Int,
    private val ACTION_INTERVAL_MS: Long,
    private val action: () -> Unit
) {
    companion object {
        const val TAG = "AlarmTrigger"
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "receiver:onReceive")
            action()
        }
    }

    private val intent by lazy {
        PendingIntent.getBroadcast(
            context,
            ACTION_CODE,
            Intent(ACTION_NAME),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val alarmManager by lazy {
        context.getSystemService<AlarmManager>()!!
    }

    fun register() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            /*
            * From Tiramisu, we need to specify the receiver exported or not
            * One of RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED should be specified when a receiver isn't being registered exclusively for system broadcasts
            * */
            context.registerReceiver(receiver, IntentFilter(ACTION_NAME), Context.RECEIVER_EXPORTED)

        }else{
            context.registerReceiver(receiver, IntentFilter(ACTION_NAME))
        }
        Log.d(TAG, "register ALARM: ${ACTION_INTERVAL_MS}")
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            ACTION_INTERVAL_MS,
            intent
        )

    }

    fun unregister() {
        context.unregisterReceiver(receiver)
        alarmManager.cancel(intent)
    }
}