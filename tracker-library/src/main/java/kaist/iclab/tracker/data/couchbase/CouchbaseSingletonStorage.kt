package kaist.iclab.tracker.data.couchbase

import com.couchbase.lite.MutableDocument
import com.google.gson.Gson
import kaist.iclab.tracker.data.core.SingletonStorageInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CouchbaseSingletonStorage<T>(
    couchbaseLite: CouchbaseDB,
    private val defaultVal: T,
    private val clazz: Class<T>,
    private val collectionName: String
): SingletonStorageInterface<T> {
    private val _stateFlow = MutableStateFlow(defaultVal)
    override val stateFlow: StateFlow<T> get() = _stateFlow

    private val collection = couchbaseLite.getCollection(collectionName)
    private val gson = Gson()

    init {
        _stateFlow.value = get()
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