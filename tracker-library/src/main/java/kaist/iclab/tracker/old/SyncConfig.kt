package kaist.iclab.tracker.old

data class SyncConfig(
    val syncEnabled: Boolean,
    val syncInterval: Long,
    val serverAddress: String?,
    val syncWiFiOnly: Boolean,
)
