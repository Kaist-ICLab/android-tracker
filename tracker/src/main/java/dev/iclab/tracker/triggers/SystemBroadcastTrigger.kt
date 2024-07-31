package dev.iclab.tracker.triggers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class SystemBroadcastTrigger(
    private val context: Context,
    private val ACTIONS: Array<String>,
    private val action: (intent: Intent) -> Unit
) {
    companion object {
        const val TAG = "SystemBroadcastTrigger"
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "receiver:onReceive")
            intent?.apply { action(this) }
        }
    }

    fun register() {
        context.registerReceiver(receiver, IntentFilter().apply{
            ACTIONS.forEach { addAction(it) }
        })
    }

    fun unregister() {
        context.unregisterReceiver(receiver)
    }
}