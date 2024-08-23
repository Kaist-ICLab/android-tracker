package dev.iclab.tracker.database

import dev.iclab.tracker.collectors.AbstractCollector

interface DatabaseInterface {
    fun insert(collectionName: String, data: Map<String, Any>): String
    fun update(collectionName: String, data: Map<String, Any>)
    fun queryAllDocs(collectionName: String): List<String>
    fun queryConfig(): Map<String, Boolean>

    fun sync()
    fun deleteAll()
}