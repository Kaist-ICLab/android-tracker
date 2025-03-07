package kaist.iclab.tracker.listener.core

import android.service.notification.NotificationListenerService.RankingMap
import android.service.notification.StatusBarNotification

sealed class NotificationEvent {
    data class Posted(val sbn: StatusBarNotification?, val rankingMap: RankingMap?)
    data class Removed(val sbn: StatusBarNotification?, val rankingMap: RankingMap?, val reason: Int?)
}