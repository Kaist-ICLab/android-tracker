package kaist.iclab.tracker.collector.core

interface CollectorConfig {
    fun copy(property: String, setValue: String): CollectorConfig
}