package dev.iclab.tracker.database

import kotlinx.coroutines.flow.Flow

interface DatabaseInterface {
    fun insert(collectionName: String, data: Map<String, Any>): String
    fun update(collectionName: String, data: Map<String, Any>)
    fun queryAllDocs(collectionName: String): List<String>

    fun sync()
    fun deleteAll()

    fun getCollectionFlow(collectionName: String): Flow<List<Map<String, Any>>>
    fun getCollectionLastFlow(collectionName: String): Flow<Map<String, Any>>

    fun getCollectionLast(collectionName: String): Map<String, Any>

    /* Function to Log some messages*/
    fun log(message: String)
}