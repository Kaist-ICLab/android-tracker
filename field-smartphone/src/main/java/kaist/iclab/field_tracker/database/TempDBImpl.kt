package kaist.iclab.tracker.database

import android.content.Context
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.collectionChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class TempDBImpl(context: Context): DatabaseInterface {
    companion object {
        const val DB = "CONFIG"
    }
    private val database: Database by lazy {
        Database(DB)
    }

    init {
        CouchbaseLite.init(context)
    }

    override fun updateConfig(name: String, value: Boolean) {
        val collection = getCollection()
        val document = collection.getDocument(DB)?.toMutable() ?: MutableDocument(DB)
        document.setBoolean(name, value)
        collection.save(document)
    }

    override fun getConfigFlow(): Flow<Map<String, Boolean>> {
        val collection = getCollection()
        return collection.collectionChangeFlow().map {
            val document = collection.getDocument(DB)
            document?.toMap()?.filterValues { it is Boolean }?.mapValues { it.value as Boolean } ?: emptyMap()
        }.onStart {
            val document = collection.getDocument(DB)
            emit(document?.toMap()?.filterValues { it is Boolean }?.mapValues { it.value as Boolean } ?: emptyMap())
        }
    }

    private fun getCollection(): com.couchbase.lite.Collection {
        val collection = database.getCollection(DB)
            ?: database.createCollection(DB)
        return collection
    }
}