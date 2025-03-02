package kaist.iclab.tracker.notification

data class ServiceNotification(
    val channelId: String,
    val channelName: String,
    val icon: Int,
    val title: String,
    val description: String,
)