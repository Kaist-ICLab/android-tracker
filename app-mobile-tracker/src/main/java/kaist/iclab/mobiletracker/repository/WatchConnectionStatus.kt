package kaist.iclab.mobiletracker.repository

/**
 * Possible connection states between the phone and watch apps.
 */
enum class WatchConnectionStatus {
    /** Watch is connected and tracker app is installed/reachable. */
    CONNECTED,
    
    /** Watch is paired but the tracker app (app-wearable-tracker) is not installed or unreachable. */
    NOT_INSTALLED,
    
    /** No watch is currently connected to the phone. */
    DISCONNECTED
}
