package kaist.iclab.tracker.data.couchbase

import com.couchbase.lite.DataSource
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.google.gson.Gson
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.data.core.DataStat
import kaist.iclab.tracker.data.core.DataStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CouchbaseDataStorage(
    couchbase: CouchbaseDB,
    collectionName: String
) : DataStorage {
    override val NAME: String = collectionName

    private val _stateFlow = MutableStateFlow(DataStat(-1L, 0L))
    override val statFlow: StateFlow<DataStat>
        get() = _stateFlow

    private val gson = Gson()
    private val collection = couchbase.getCollection(collectionName)

    init {
        _stateFlow.value = DataStat(
            getLastReceived(),
            collection.count
        )
    }

    override fun insert(data: DataEntity) {
        _stateFlow.value = _stateFlow.value.copy(
            timestamp = System.currentTimeMillis(),
            count = _stateFlow.value.count + 1
        )
        collection.save(MutableDocument(gson.toJson(data)))
    }

    private fun getLastReceived(): Long {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(collection))
            .orderBy(Ordering.property("received").descending())
        val ret = query.execute().firstOrNull()?.getLong("received")
        return ret ?: -1L
    }
}


//    override fun getUnsynced(): List<T> {
//        val query = QueryBuilder.select(SelectResult.all())
//            .from(DataSource.collection(collection))
//            .where(Expression.property("synced").equalTo(Expression.intValue(0)))
//
//        val result = mutableListOf<T>()
//
//        query.execute().forEach { row ->
//            row.getDictionary(collection.name)?.toJSON()?.let { json ->
//                result.add(gson.fromJson(json, clazz))
//            }
//        }
//
//        return result
//    }
//
//    override fun updateSyncStatus(ids: List<String>, timestamp: Long) {
//        ids.forEach { id ->
//            collection.getDocument(id)?.let { document ->
//                val mutableDoc = document.toMutable()
//                mutableDoc.setLong("synced", timestamp) // synced 값 업데이트
//                collection.save(mutableDoc)
//            }
//        }
//
//        // 동기화 완료된 count 업데이트
//        _stateFlow.value = Pair(_stateFlow.value.first, _stateFlow.value.second - ids.size)
//    }

//    private fun extractName(className: String): String {
//        // Replace "Collector" with an empty string
//        val tmp = className.replace("Storage", "")
//
//        // Split the name into parts based on camel case
//        val parts =
//            tmp.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())
//
//        // Join the parts using whitespace and convert
//        return parts.joinToString(" ")
//    }