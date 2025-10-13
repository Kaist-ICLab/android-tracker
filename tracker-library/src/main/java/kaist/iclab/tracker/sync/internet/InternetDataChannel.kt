package kaist.iclab.tracker.sync.internet

import kaist.iclab.tracker.sync.core.DataChannel
import kaist.iclab.tracker.sync.core.DataReceiver
import kaist.iclab.tracker.sync.core.DataSender
import okhttp3.Response

/**
 * A DataChannel that uses internet to transfer data.
 * Suitable for communication between server and clients, such as a mobile phone or a smartwatch.
 * If you are using HTTP connection, you have to allow cleartextTraffic in the Android Manifest. Please refer to test-sync module.
 *
 * One distinct characteristics of InternetDataChannel is that send() cam be used in 2 ways: for transferring data and for making a request.
 * In both cases, you can retrieve the response.
 *
 * Because maintaining socket connection on the background requires a lot of resource, we use Firebase Cloud Messaging (FCM) to receive messages.
 * This means that if you want to send information from the server to client, you need to use FCM.
 * 
 * This class now uses the separated sender/receiver pattern.
 */
class InternetDataChannel(
    private val keyParamName: String = "_key"
): DataChannel<Response>() {
    
    override val sender: DataSender<Response> = InternetSender()
    override val receiver: DataReceiver = InternetReceiver(keyParamName)

    /**
     * Send data with specific HTTP method
     */
    suspend fun send(key: String, value: String, method: InternetSender.Method): Response {
        return (sender as InternetSender).send(key, value, method)
    }

    /**
     * Add listener for Firebase token updates
     */
    fun addOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        (receiver as InternetReceiver).addOnNewFirebaseTokenListener(callback)
    }

    /**
     * Remove listener for Firebase token updates
     */
    fun removeOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        (receiver as InternetReceiver).removeOnNewFirebaseTokenListener(callback)
    }
}
