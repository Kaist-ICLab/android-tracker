package kaist.iclab.tracker.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat

class NotificationManagerImpl : NotificationManagerInterface {
    val SERVICE_CHANNEL_ID = "TRACKER_SERVICE"
    val SERVICE_CHANNEL_NUMBER = 1
    val USER_REPORT_CHANNEL_ID = "USER_REPORT"
    val USER_REPORT_CHANNEL_NUMBER = 2


    var SERVICE_NOTF_TITLE = "Tracker Service"
    var SERVICE_NOTF_DESCRIPTION = "Tracker Service is running"
    var NOTF_ICON = android.R.drawable.ic_dialog_info

    override fun setServiceNotfDescription(title: String?, description: String?, icon: Int) {
        SERVICE_NOTF_TITLE = title ?: SERVICE_NOTF_TITLE
        SERVICE_NOTF_DESCRIPTION = description ?: SERVICE_NOTF_DESCRIPTION
        NOTF_ICON = icon
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
        Log.d("NOTIFICATION_SERVICE", "Starting Foreground Service: $foregroundTypes")
        ServiceCompat.startForeground(
            service,
            SERVICE_CHANNEL_NUMBER,
            createServiceNotf(service),
            foregroundTypes
        )
    }

    private fun createServiceNotf(service: Service): Notification {
        Log.d("NOTIFICATION_SERVICE", "Creating Notification")
        val builder = NotificationCompat.Builder(service, SERVICE_CHANNEL_ID)
        return builder
            .setSmallIcon(NOTF_ICON)
            .setContentTitle(SERVICE_NOTF_TITLE)
            .setContentText(SERVICE_NOTF_DESCRIPTION)
            .setOngoing(true)
            .build()
    }

    override fun createUserReportNotfChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                USER_REPORT_CHANNEL_ID,
                USER_REPORT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showUserReportNotf(context: Context,
                                    title:String,
                                    text: String,
                                    intent: Intent /*실행될 Activity 설정*/
    ) {
        // 알림 클릭 시 실행될 작업 설정
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 생성
        val builder = NotificationCompat.Builder(context, USER_REPORT_CHANNEL_ID)
            .setSmallIcon(NOTF_ICON) // 알림 아이콘
            .setContentTitle(title) // 제목
            .setContentText(text) // 내용
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 우선순위
            .setContentIntent(pendingIntent) // 클릭 이벤트
            .setAutoCancel(true) // 클릭 시 알림 닫힘

        // 알림 표시
        with(NotificationManagerCompat.from(context)) {
            notify(USER_REPORT_CHANNEL_NUMBER, builder.build())
        }

    }
}