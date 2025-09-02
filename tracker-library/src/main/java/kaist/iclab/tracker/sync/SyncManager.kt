package kaist.iclab.tracker.sync

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

abstract class SyncManager {
    private val callbackList = mutableMapOf<String, MutableList<(String) -> Unit>>()

    fun send(key: String, value: Serializable) {
        send(key, Json.encodeToString(value))
    }

    abstract fun send(key: String, value: String)

    fun addOnReceivedListener(keys: List<String>, callback: (String) -> Unit) {
        for (key in keys) {
            callbackList.getOrPut(key) { mutableListOf() }.add(callback)
        }
    }

    fun removeOnReceivedListener(keys: List<String>, callback: (String) -> Unit) {
        for(key in keys) {
            callbackList[key]?.remove(callback)
        }
    }
}