package kaist.iclab.tracker.collectors.core

data class CollectorState(
    val flag: FLAG,
    val message: String? = null
) {
    enum class FLAG {
        UNAVAILABLE, // The collector is not available (e.g., sensor not found)
        PERMISSION_REQUIRED, // The collector requires permission to collect data
        DISABLED, // The collector is not ready for collection (e.g., user not turned on it)
        ENABLED, // The collector is enabled for collection, but not running
        RUNNING // The collector is running
    }
}
