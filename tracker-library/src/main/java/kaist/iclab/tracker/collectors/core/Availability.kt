package kaist.iclab.tracker.collectors.core

data class Availability(
    val status: Boolean,
    val reason: String? = null
)