package kaist.iclab.tracker.notification

import android.app.Notification
import android.app.Service

interface NotfManager {
    fun init()
    fun createChannel(channelId: String, name: String)
    fun postForegroundService(service: Service, foregroundTypes: Int)
    fun post(notfId: Int, notification: Notification)
}

//fun createUserReportNotfChannel(context: Context)
//fun showUserReportNotf(context: Context, title: String, text: String, intent: Intent)