package dev.iclab.tracker.database

import dev.iclab.tracker.collectors.AbstractCollector

interface DatabaseInterface {
    fun insert(collectionName: String, data: Map<String, Any>): String
    fun queryAllDocs(collectionName: String): List<String>
}