package kaist.iclab.tracker.listener

import android.util.Log
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kaist.iclab.tracker.listener.core.Listener
import kaist.iclab.tracker.listener.core.NotificationEventInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListener: Listener<NotificationEventInfo>, NotificationListenerService() {
    private var receivers = mutableListOf<(NotificationEventInfo) -> Unit>()
    private var count = 0

    companion object {
        const val TAG = "NotificationTrigger"
    }

    override fun init() {}

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "NotificationListener started")
    }

    override fun addListener(listener: (NotificationEventInfo) -> Unit) {
        assert(!receivers.contains(listener))
        receivers.add(listener)
        count += 1
        Log.v(TAG, "${receivers.size} ${receivers.hashCode()}")
        Log.v(TAG, "$this")
    }

    override fun removeListener(listener: (NotificationEventInfo) -> Unit) {
        assert(receivers.contains(listener))
        receivers.remove(listener)
        count -= 1
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        Log.v(TAG, "receiver: onNotificationPosted")
//        super.onNotificationPosted(sbn, rankingMap)

        Log.v(TAG, "${receivers.size} $count ${receivers.hashCode()}")
        Log.v(TAG, "$this")
        // Use coroutine to prevent listeners from blocking each other
        for(callback in receivers) {
            Log.v(TAG, "Coroutine Launching")
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
        Log.v(TAG, "receiver: onNotificationRemoved")

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

