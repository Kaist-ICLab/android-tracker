package kaist.iclab.tracker.sync

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

abstract class SyncManager {
    val callbackList = mutableMapOf<String, MutableList<(String, JsonElement) -> Unit>>()

    suspend inline fun<reified T: @Serializable Any> send (key: String, value: T) {
        send(key, Json.encodeToString(value))
    }

    abstract suspend fun send(key: String, value: String)

    open fun addOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit) {
        for (key in keys) {
            callbackList.getOrPut(key) { mutableListOf() }.add(callback)
        }
    }

    open fun removeOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit) {
        for(key in keys) {
            callbackList[key]?.remove(callback)
        }
    }
}