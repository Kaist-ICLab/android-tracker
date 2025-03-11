package com.example.notification_listener_app.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.listener.NotificationListener
import kaist.iclab.tracker.listener.core.NotificationEventInfo

class MainViewModel: ViewModel() {
    private val listener by mutableStateOf(NotificationListener())

    private val tag = "NotificationListenerApp"
    private val channelId = "test_channel"
    private val list = mutableListOf<(NotificationEventInfo) -> Unit>()

    fun addCallback() {
        val count = list.size
        val callback = { event: NotificationEventInfo ->
            when(event){
                is NotificationEventInfo.Posted -> Log.v(tag, "Callback $count: Notification posted")
                else -> Log.v(tag, "Callback $count: Notification removed")
            }
            Unit
        }

        list.add(callback)
        listener.addListener(callback)
    }

    fun removeCallback() {
        if(list.size > 0) {
            val lastCallback = list.last()
            list.remove(lastCallback)
            listener.removeListener(lastCallback)
        }
    }

    fun sendNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(1, notification)
    }
}