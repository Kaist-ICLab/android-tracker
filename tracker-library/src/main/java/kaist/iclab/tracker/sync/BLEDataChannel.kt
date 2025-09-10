package kaist.iclab.tracker.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kaist.iclab.tracker.R
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * A DataChannel that uses BLE(Bluetooth low energy) channel to transfer data.
 * Suitable for communication between nearby devices, such as a mobile phone and a smartwatch.
 *
 * BLEDataChannel runs on top of DataLayer API, so the namespace and application ID of sending/receiving app **must be the same**.
 * */
class BLEDataChannel(
    private val context: Context
): DataChannel<Unit>() {
    companion object {
        private val TAG = BLEDataChannel::class.simpleName
    }

    val dataClient by lazy { Wearable.getDataClient(context) }

    init {
        BLESyncReceiverService.callbackList = callbackList
    }

    suspend inline fun<reified T: @Serializable Any> send (key: String, value: T, isUrgent: Boolean) {
        send(key, Json.encodeToString(value), isUrgent)
    }

    override suspend fun send(key: String, value: String) {
        send(key, value, false)
    }

    suspend fun send(key: String, value: String, isUrgent: Boolean) {
        val path = context.getString(R.string.ble_sync_path)
        val asset = Asset.createFromBytes(value.toByteArray())

        Log.d(TAG, "send: $key, $value")

        val request = PutDataMapRequest.create(path).apply {
            dataMap.putString("key", key)
            dataMap.putAsset("data", asset)
        }
            .asPutDataRequest()

        val dataItem = if(isUrgent) request.setUrgent() else request
        val result = dataClient.putDataItem(dataItem).await()

        Log.d(TAG, "DataItem saved: $result")
    }

    class BLESyncReceiverService: WearableListenerService() {
        companion object {
            private val TAG = BLESyncReceiverService::class.simpleName
            var callbackList = mutableMapOf<String, MutableList<(String, JsonElement) -> Unit>>()
            private var localNodeId: String? = null
        }

        override fun onCreate() {
            super.onCreate()
            initializeLocalNodeId()
        }

        private fun initializeLocalNodeId() {
            Wearable.getNodeClient(this).localNode.addOnSuccessListener { node ->
                localNodeId = node.id
            }
        }

        private fun onAssetSuccessListener(callbacks: List<(String, JsonElement) -> Unit>, key: String, assetFd: DataClient.GetFdForAssetResponse) {
            assetFd.inputStream.use { inputStream ->
                val jsonString = String(inputStream.readBytes())
                val jsonElement = Json.parseToJsonElement(jsonString)

                callbacks.forEach { it.invoke(key, jsonElement) }
            }
        }

        override fun onDataChanged(dataEvents: DataEventBuffer) {
            Log.v(TAG, "onDataChanged: ${dataEvents.count}")
            
            dataEvents.forEach { dataEvent ->
                // Skip if this is a message from the same device
                if (localNodeId != null && dataEvent.dataItem.uri.host == localNodeId) {
                    return@forEach
                }
                
                val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                val key = dataMapItem.dataMap.getString("key")!!

                val callbacks = callbackList[key] ?: listOf()
                if(callbacks.isEmpty()) {
                    return@forEach
                }

                val asset = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.getAsset("data")!!
                Wearable.getDataClient(this).getFdForAsset(asset).addOnSuccessListener {
                    onAssetSuccessListener(callbacks, key, it)
                }
            }
        }
    }
}