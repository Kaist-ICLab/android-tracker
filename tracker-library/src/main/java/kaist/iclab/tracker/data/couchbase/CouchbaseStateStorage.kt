package kaist.iclab.tracker.data.couchbase

import android.util.Log
import com.couchbase.lite.MutableDocument
import com.google.gson.Gson
import kaist.iclab.tracker.data.core.StateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CouchbaseStateStorage<T>(
    couchbase: CouchbaseDB,
    private val defaultVal: T,
    private val clazz: Class<T>,
    private val collectionName: String
): StateStorage<T> {
    private val _stateFlow = MutableStateFlow(defaultVal)
    override val stateFlow: StateFlow<T> get() = _stateFlow

    private val collection = couchbase.getCollection(collectionName)
    private val gson = Gson()

    init {
        _stateFlow.value = get()
        Log.d("CouchbaseStateStorage", "init: ${_stateFlow.value}")
    }

    override fun set(value: T) {
        val json = gson.toJson(value)
        collection.save(MutableDocument(collectionName, json))
        _stateFlow.value = value
    }

    override fun get(): T {
        val document = collection.getDocument(collectionName) ?: return defaultVal
        val json = document.toJSON()
        return gson.fromJson(json, clazz)
    }
}