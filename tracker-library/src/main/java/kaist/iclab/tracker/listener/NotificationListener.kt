package kaist.iclab.tracker.listener
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kaist.iclab.tracker.listener.core.Listener
import kaist.iclab.tracker.listener.core.NotificationEventInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListener: Listener<NotificationEventInfo>, NotificationListenerService() {
    private val receivers = mutableListOf<(NotificationEventInfo) -> Unit>()

    override fun init() {}

    override fun addListener(listener: (NotificationEventInfo) -> Unit) {
        assert(!receivers.contains(listener))
        receivers.add(listener)
    }

    override fun removeListener(listener: (NotificationEventInfo) -> Unit) {
        assert(receivers.contains(listener))
        receivers.remove(listener)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)

        // Use coroutine to prevent listeners from blocking each other
        for(callback in receivers) {
            CoroutineScope(Dispatchers.IO).launch {
                callback(NotificationEventInfo.Posted(
                    sbn = sbn,
                    rankingMap = rankingMap
                ))
            }
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        super.onNotificationRemoved(sbn, rankingMap, reason)

        // Use coroutine to prevent listeners from blocking each other
        for(callback in receivers) {
            CoroutineScope(Dispatchers.IO).launch {
                callback(NotificationEventInfo.Removed(
                    sbn = sbn,
                    rankingMap = rankingMap,
                    reason = reason
                ))
            }
        }
    }
}

