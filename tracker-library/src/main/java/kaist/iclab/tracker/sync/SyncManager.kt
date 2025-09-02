package kaist.iclab.tracker.sync

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

abstract class SyncManager {
    val callbackList = mutableMapOf<String, MutableList<(JsonElement) -> Unit>>()

    suspend fun send(key: String, value: Serializable) {
        send(key, Json.encodeToString(value))
    }

    abstract suspend fun send(key: String, value: String)

    open fun addOnReceivedListener(keys: List<String>, callback: (JsonElement) -> Unit) {
        for (key in keys) {
            callbackList.getOrPut(key) { mutableListOf() }.add(callback)
        }
    }

    open fun removeOnReceivedListener(keys: List<String>, callback: (JsonElement) -> Unit) {
        for(key in keys) {
            callbackList[key]?.remove(callback)
        }
    }
}