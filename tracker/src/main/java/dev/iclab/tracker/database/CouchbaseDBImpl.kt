package dev.iclab.tracker.database

import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.collectionChangeFlow
import com.couchbase.lite.databaseChangeFlow
import dev.iclab.tracker.collectors.AbstractCollector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Objects

class CouchbaseDBImpl(
    context: Context,
): DatabaseInterface{

    companion object{
        const val TAG = "CouchbaseDBImpl"
        const val LOG_COLLECTION = "LOG"
    }

    private val database: Database by lazy {
        Database("tracker")
    }

    init {
        // Initialize Couchbase Lite
        CouchbaseLite.init(context)
    }

    override fun insert(collectionName: String, data: Map<String, Any>): String {
        val document = MutableDocument(data)
        val collection = database.getCollection(collectionName)
            ?: database.createCollection(collectionName)
        collection.save(document)
        return document.id
    }

    override fun queryAllDocs(collectionName: String): List<String> {
        val collection = database.getCollection(collectionName) ?: throw Exception("Collection not found")
        val query: Query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
        return query.execute().allResults().map {
            it.toMap().toString()
        }
    }

    override fun update(collectionName: String, data: Map<String,Any>){
        val document = MutableDocument(data)
        val collection = database.getCollection(collectionName)
            ?: database.createCollection(collectionName)
        val id = collection.indexes.firstOrNull()
        collection.save(document)
    }

    override fun sync() {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        database.delete()
    }

    override fun getCollectionFlow(collectionName: String): Flow<List<Map<String, Any>>> {
        TODO("Not yet implemented")
    }

    override fun getCollectionLastFlow(collectionName: String): Flow<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override fun getCollectionLast(collectionName: String): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override fun log(message: String) {
        insert(LOG_COLLECTION, mapOf("timestamp" to System.currentTimeMillis(), "message" to message))
    }
}