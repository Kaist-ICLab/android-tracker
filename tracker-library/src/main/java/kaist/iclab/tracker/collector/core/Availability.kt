package kaist.iclab.tracker.collector.core

data class Availability(
    val status: Boolean,
    val reason: String? = null
)