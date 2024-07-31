package dev.iclab.tracker.database

import dev.iclab.tracker.collectors.AbstractCollector

interface DatabaseInterface {
    fun insert(data: String)
    fun getCollectorConfig(): List<Class<out AbstractCollector>>
}