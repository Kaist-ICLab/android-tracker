package dev.iclab.tracker.database

import android.content.Context
import android.util.Log
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.collectionChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class CouchbaseDBImpl(
    context: Context,
) : DatabaseInterface {

    companion object {
        const val TAG = "CouchbaseDBImpl"
        const val LOG_COLLECTION = "LOG"
        const val DB = "tracker"
    }

    private val database: Database by lazy {
        Database(DB)
    }

    init {
        // Initialize Couchbase Lite
        CouchbaseLite.init(context)
    }

    override fun insert(collectionName: String, data: Map<String, Any>): String {
        val document = MutableDocument(data)
        val collection = getCollection(collectionName)
        collection.save(document)
        return document.id
    }

    override fun update(collectionName: String, data: Map<String, Any>) {
        val collection = getCollection(collectionName)
        val id = collection.indexes.firstOrNull()
        val document = MutableDocument(id, data)
        collection.save(document)
        Log.d(TAG, "$collectionName Updated: $data")
    }

    override fun sync() {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        database.delete()
    }

    override fun getAllDocs(collectionName: String): List<Map<String, Any>> {
        val collection = getCollection(collectionName)
        val query: Query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
        return query.execute().allResults().map { it.toMap() }
    }

    override fun getDocsFlow(collectionName: String): Flow<List<Map<String, Any>>> {
        val collection = getCollection(collectionName)
        return collection.collectionChangeFlow().map {
            getAllDocs(collectionName)
        }.onEmpty {
            emit(getAllDocs(collectionName))
        }
    }


    override fun getLastDocFlow(collectionName: String): Flow<Map<String, Any>> {
        val collection = getCollection(collectionName)
        return collection.collectionChangeFlow().map{
            getLastDoc(collectionName)
        }.onEmpty {
            getLastDoc(collectionName)
        }
    }

    override fun getLastDoc(collectionName: String): Map<String, Any> {
        val collection  = getCollection(collectionName)
        val query: Query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .orderBy(Ordering.property("timestamp").descending())
            .limit(Expression.intValue(1))
        return query.execute().firstOrNull()?.getDictionary(0)?.toMap() ?: mapOf()
    }

    override fun log(message: String) {
        insert(
            LOG_COLLECTION,
            mapOf("timestamp" to System.currentTimeMillis(), "message" to message)
        )
    }

    private fun getCollection(collectionName: String): com.couchbase.lite.Collection {
        val collection = database.getCollection(collectionName)
            ?: database.createCollection(collectionName)
        return collection
    }
}