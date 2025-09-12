package kaist.iclab.tracker.sync

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * A bi-direction data channel that can be used to send and receive data.
 *
 * While using a single channel network, one can distinguish the type of data transferred by key.
 */
abstract class DataChannel<R> {
    val callbackList = mutableMapOf<String, MutableList<(String, JsonElement) -> Unit>>()

    suspend inline fun<reified T: @Serializable Any> send (key: String, value: T): R {
        return send(key, Json.encodeToString(value))
    }

    abstract suspend fun send(key: String, value: String): R

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