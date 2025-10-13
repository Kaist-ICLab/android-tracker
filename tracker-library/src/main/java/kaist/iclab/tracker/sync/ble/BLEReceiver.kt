package kaist.iclab.tracker.sync.ble

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kaist.iclab.tracker.sync.core.DataChannelReceiver
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * BLE data receiver for receiving data through Bluetooth Low Energy.
 * Uses Google Wearable DataLayer API for communication.
 */
class BLEReceiver : DataChannelReceiver() {
    companion object {
        private val TAG = BLEReceiver::class.simpleName
        private var localNodeId: String? = null
    }

    init {
        BLEReceiverService.callbackList = callbackList
    }

    fun initializeLocalNodeId(context: Context) {
        Wearable.getNodeClient(context).localNode.addOnSuccessListener { node ->
            localNodeId = node.id
        }
    }

    class BLEReceiverService : WearableListenerService() {
        companion object {
            private val TAG = BLEReceiverService::class.simpleName
            var callbackList = mutableMapOf<String, MutableList<(String, JsonElement) -> Unit>>()
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
                if (callbacks.isEmpty()) {
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
