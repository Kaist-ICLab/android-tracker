package kaist.iclab.tracker.collector.phone

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface

class NotificationCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<NotificationCollector.Config, NotificationCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE else null
    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config: CollectorConfig()

    override val defaultConfig = Config()

    override fun isAvailable(): Availability {
        return if (context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)) {
            Availability(true)
        } else {
            Availability(false, "Notification access is not granted.")
        }
    }

    override fun start() {
        NotificationTrigger.instance?.let {
            it.listener = listener
            it.startListening()
        }
    }

    override fun stop() {
        NotificationTrigger.instance?.stopListening()
    }


    class NotificationTrigger: NotificationListenerService() {
        companion object{
            var instance: NotificationTrigger? = null
        }
        var isListening = false
        var listener: ((DataEntity) -> Unit)? = null
        fun startListening() {
            isListening = true
        }

        fun stopListening() {
            isListening = false
        }

        override fun onCreate() {
            super.onCreate()
            instance = this
        }

        override fun onDestroy() {
            super.onDestroy()
            instance = null
        }

        override fun onNotificationPosted(sbn: StatusBarNotification) {
            handleNotf(sbn, "POSTED")
        }

        override fun onNotificationRemoved(sbn: StatusBarNotification) {
            handleNotf(sbn, "REMOVED")
        }

        private fun handleNotf(sbn: StatusBarNotification, eventType: String) {
            if (isListening) {
                listener?.invoke(
                    Entity(
                        System.currentTimeMillis(),
                        sbn.postTime,
                        sbn.packageName,
                        eventType,
                        sbn.notification.extras.getString("android.title") ?: "",
                        sbn.notification.extras.getString("android.text") ?: "",
                        sbn.notification.visibility?: -1,
                        sbn.notification.category ?: ""
                    )
                )
            }
        }
    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val packageName: String,
        val eventType: String,
        val title: String,
        val text: String,
        val visibility: Int,
        val category: String
    ) : DataEntity(received)
}