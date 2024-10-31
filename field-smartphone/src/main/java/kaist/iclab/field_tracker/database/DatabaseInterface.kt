package kaist.iclab.tracker.database

import kotlinx.coroutines.flow.Flow

interface DatabaseInterface {
//    fun insert(collectionName: String, data: Map<String, Any>): String
//    fun update(collectionName: String, data: Map<String, Any>)
//
//    fun sync()
//    fun deleteAll()
//
//    fun getDocsFlow(collectionName: String): Flow<List<Map<String, Any>>>
//    fun getAllDocs(collectionName: String): List<Map<String,Any>>
//
//    fun getLastDocFlow(collectionName: String): Flow<Map<String, Any>>
//    fun getLastDoc(collectionName: String): Map<String, Any>
//
//    /* Function to Log some messages*/
//    fun log(message: String)

    /* Enable/Disable Collector */
    fun updateConfig(name: String, value: Boolean)
    fun getConfigFlow(): Flow<Map<String,Boolean>>
}