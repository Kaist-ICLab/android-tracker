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
import dev.iclab.tracker.collectors.AbstractCollector
import java.util.Objects

class CouchbaseDBImpl(
    context: Context,
): DatabaseInterface{

    companion object{
        const val TAG = "CouchbaseDBImpl"
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

    override fun queryConfig(): Map<String, Boolean> {
        val collection = database.getCollection("CONFIG") ?: throw Exception("Collection not found")
        val query: Query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
        return query.execute().allResults().firstOrNull()?.toMap() as Map<String, Boolean>
    }
}