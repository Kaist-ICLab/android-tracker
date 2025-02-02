package kaist.iclab.tracker.data.old

data class SyncConfig(
    val syncEnabled: Boolean,
    val syncInterval: Long,
    val serverAddress: String?,
    val syncWiFiOnly: Boolean,
)
