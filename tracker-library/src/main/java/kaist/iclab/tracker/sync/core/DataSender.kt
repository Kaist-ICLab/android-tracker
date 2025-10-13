package kaist.iclab.tracker.sync.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Interface for sending data through various channels.
 * 
 * @param R The return type of the send operation (e.g., Unit for fire-and-forget, Response for HTTP)
 */
interface DataSender<R> {
    /**
     * Send serializable data with a specific key
     */
    suspend inline fun <reified T : @Serializable Any> send(key: String, value: T): R {
        return send(key, Json.encodeToString(value))
    }

    /**
     * Send string data with a specific key
     */
    suspend fun send(key: String, value: String): R
}
