package kaist.iclab.tracker.notf

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class NotfManagerImpl: NotfManagerInterface {
    val SERVICE_CHANNEL_ID = "TRACKER_SERVICE"
    val SERVICE_CHANNEL_NUMBER = 1


    var SERVICE_NOTF_TITLE = "Tracker Service"
    var SERVICE_NOTF_DESCRIPTION = "Tracker Service is running"
    var SERVICE_NOTF_ICON = android.R.drawable.ic_dialog_info

    override fun setServiceNotfDescription(title: String, description: String, icon: Int) {
        SERVICE_NOTF_TITLE = title
        SERVICE_NOTF_DESCRIPTION = description
        SERVICE_NOTF_ICON = icon
    }

    override fun createServiceNotfChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                SERVICE_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun startForegroundService(service: Service, foregroundTypes: Int) {
        ServiceCompat.startForeground(
            service,
            SERVICE_CHANNEL_NUMBER,
            createServiceNotf(service),
            foregroundTypes
        )
    }

    private fun createServiceNotf(service: Service): Notification {
        Log.d("NOTIFICATION_SERVICE", "Creating Notification")
        val builder =  NotificationCompat.Builder(service, SERVICE_CHANNEL_ID)
        return builder
            .setSmallIcon(SERVICE_NOTF_ICON)
            .setContentTitle(SERVICE_NOTF_TITLE)
            .setContentText(SERVICE_NOTF_DESCRIPTION)
            .setOngoing(true)
            .build()
    }
}