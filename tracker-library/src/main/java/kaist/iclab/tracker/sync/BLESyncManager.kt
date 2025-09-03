package kaist.iclab.tracker.sync

import android.content.Context
import android.content.Intent
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class BLESyncManager(
    private val context: Context
): SyncManager() {
    companion object {
        private val TAG = BLESyncManager::class.simpleName
    }

    init {
        BLESyncReceiverService.callbackList = callbackList
    }

    // In your sending class or a utility
    suspend fun logConnectedNodes() {
        try {
            val nodeClient = Wearable.getNodeClient(context)
            val nodes = nodeClient.connectedNodes.await()
            if (nodes.isEmpty()) {
                Log.d(TAG, "No connected wearable nodes found.")
            } else {
                Log.d(TAG, "Connected nodes: ${nodes.joinToString { it.displayName }}")
                nodes.forEach { node ->
                    Log.d(TAG, "Node: ${node.displayName}, ID: ${node.id}, isNearby: ${node.isNearby}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting connected nodes", e)
        }
    }

    override suspend fun send(key: String, value: String) {
        logConnectedNodes()
        val dataClient = Wearable.getDataClient(context)
        val path = context.getString(R.string.ble_sync_path)
        val asset = Asset.createFromBytes(value.toByteArray())
        val request = PutDataMapRequest.create("$path/$key").apply {
            dataMap.putAsset("data", asset)
        }.asPutDataRequest()

        val result = dataClient.putDataItem(request).await()
        Log.v(TAG, "send: $result")
    }

    class BLESyncReceiverService: WearableListenerService() {
        override fun onCreate() {
            super.onCreate()
            Log.v("BLESyncReceiverService", "onCreate")
        }

        companion object {
            var callbackList = mutableMapOf<String, MutableList<(JsonElement) -> Unit>>()
        }

        private fun onAssetSuccessListener(callbacks: List<(JsonElement) -> Unit>, assetFd: DataClient.GetFdForAssetResponse) {
            assetFd.inputStream.use { inputStream ->
                val jsonString = String(inputStream.readBytes())
                val jsonElement = Json.parseToJsonElement(jsonString)

                callbacks.forEach { it.invoke(jsonElement) }
            }
        }

        override fun onDataChanged(dataEvents: DataEventBuffer) {
            Log.v(TAG, "onDataChanged: ${dataEvents.count}")
            dataEvents.forEach { dataEvent ->
                val pathSegments = dataEvent.dataItem.uri.path?.split("/")
                if(pathSegments == null) return

                val key = pathSegments[1]
                val callbacks = callbackList[key] ?: listOf()
                if(callbacks.isEmpty()) {
                    return
                }

                val asset = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.getAsset("data")!!
                Wearable.getDataClient(this).getFdForAsset(asset).addOnSuccessListener {
                    onAssetSuccessListener(callbacks, it)
                }
            }
        }
    }
}