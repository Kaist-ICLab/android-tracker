package kaist.iclab.tracker.data.couchbase

import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.google.gson.Gson
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.data.core.DataStorageInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CouchbaseDataStorage<T:DataEntity>(
    couchbaseLite: CouchbaseDB,
    collectionName: String,
    private val clazz: Class<T>,
): DataStorageInterface {
    override val NAME: String = collectionName

    private val _stateFlow = MutableStateFlow(Pair(0L, 0L))
    override val statFlow: StateFlow<Pair<Long, Long>>
        get() = _stateFlow

    private val gson = Gson()
    private val collection = couchbaseLite.getCollection(collectionName)

    override fun insert(data: DataEntity) {
        _stateFlow.value = Pair(System.currentTimeMillis(), _stateFlow.value.second + 1)
        collection.save(MutableDocument(gson.toJson(data)))
    }

    override fun getUnsynced(): List<T> {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .where(Expression.property("synced").equalTo(Expression.intValue(0)))

        val result = mutableListOf<T>()

        query.execute().forEach { row ->
            row.getDictionary(collection.name)?.toJSON()?.let { json ->
                result.add(gson.fromJson(json, clazz))
            }
        }

        return result
    }

    override fun updateSyncStatus(ids: List<String>, timestamp: Long) {
        ids.forEach { id ->
            collection.getDocument(id)?.let { document ->
                val mutableDoc = document.toMutable()
                mutableDoc.setLong("synced", timestamp) // synced 값 업데이트
                collection.save(mutableDoc)
            }
        }

        // 동기화 완료된 count 업데이트
        _stateFlow.value = Pair(_stateFlow.value.first, _stateFlow.value.second - ids.size)
    }

    private fun extractName(className: String): String {
        // Replace "Collector" with an empty string
        val tmp = className.replace("Storage", "")

        // Split the name into parts based on camel case
        val parts =
            tmp.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())

        // Join the parts using whitespace and convert
        return parts.joinToString(" ")
    }
}