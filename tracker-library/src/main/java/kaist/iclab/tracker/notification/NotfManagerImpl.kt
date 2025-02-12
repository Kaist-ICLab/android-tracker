package kaist.iclab.tracker.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

data class ServiceNotification(
    val channelId: String,
    val channelName: String,
    val icon: Int,
    val title: String,
    val description: String,
)

class NotfManagerImpl(
    private val context: Context,
    private val serviceNotf: ServiceNotification
) : NotfManager {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun init() {
        createChannel(serviceNotf.channelId, serviceNotf.channelName)
    }

    override fun createChannel(channelId: String, name: String) {
        val channel = NotificationChannel(
            channelId,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }

    override fun postForegroundService(service: Service, foregroundTypes: Int) {
        val builder = NotificationCompat.Builder(context, serviceNotf.channelId)
        val notf = builder
            .setSmallIcon(serviceNotf.icon)
            .setContentTitle(serviceNotf.title)
            .setContentText(serviceNotf.description)
            .setOngoing(true)
            .build()
        ServiceCompat.startForeground(
            service,
            1,
            notf,
            foregroundTypes
        )
    }

    override fun post(notfId: Int, notification: Notification) {
        notificationManager.notify(notfId, notification)
    }
}

//override fun createUserReportNotfChannel(context: Context) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channel = NotificationChannel(
//            USER_REPORT_CHANNEL_ID,
//            USER_REPORT_CHANNEL_ID,
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        // Register the channel with the system
//        val notificationManager: NotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//}
//
//override fun showUserReportNotf(context: Context,
//                                title:String,
//                                text: String,
//                                intent: Intent /*실행될 Activity 설정*/
//) {
//    // 알림 클릭 시 실행될 작업 설정
//    val pendingIntent = PendingIntent.getActivity(
//        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
//
//    // 알림 생성
//    val builder = NotificationCompat.Builder(context, USER_REPORT_CHANNEL_ID)
//        .setSmallIcon(NOTF_ICON) // 알림 아이콘
//        .setContentTitle(title) // 제목
//        .setContentText(text) // 내용
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 우선순위
//        .setContentIntent(pendingIntent) // 클릭 이벤트
//        .setAutoCancel(true) // 클릭 시 알림 닫힘
//
//    // 알림 표시
//    with(NotificationManagerCompat.from(context)) {
//        notify(USER_REPORT_CHANNEL_NUMBER, builder.build())
//    }
//
//}