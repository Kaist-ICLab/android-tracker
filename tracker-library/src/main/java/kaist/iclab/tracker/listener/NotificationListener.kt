package kaist.iclab.tracker.listener
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kaist.iclab.tracker.listener.core.Listener
import kaist.iclab.tracker.listener.core.NotificationEvent

class NotificationListener: Listener<NotificationEvent>, NotificationListenerService() {
    private val receivers = mutableListOf<(NotificationEvent) -> Unit>()

    override fun init() {}

    override fun addListener(listener: (NotificationEvent) -> Unit) {
        assert(!receivers.contains(listener))
        receivers.add(listener)
    }

    override fun removeListener(listener: (NotificationEvent) -> Unit) {
        assert(receivers.contains(listener))
        receivers.remove(listener)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
        for(callback in receivers) {
            callback(NotificationEvent.Removed())
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
    ) {
        super.onNotificationRemoved(sbn, rankingMap)
        for(callback in receivers) {
            callback(NotificationEvent(sbn, rankingMap, NotificationEventType.Removed))
        }
    }
}

