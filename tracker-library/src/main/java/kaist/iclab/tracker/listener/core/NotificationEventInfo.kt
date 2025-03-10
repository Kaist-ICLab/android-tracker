package kaist.iclab.tracker.listener.core

import android.service.notification.NotificationListenerService.RankingMap
import android.service.notification.StatusBarNotification

sealed class NotificationEventInfo {
    data class Posted(val sbn: StatusBarNotification?, val rankingMap: RankingMap?): NotificationEventInfo()
    data class Removed(val sbn: StatusBarNotification?, val rankingMap: RankingMap?, val reason: Int): NotificationEventInfo()
}