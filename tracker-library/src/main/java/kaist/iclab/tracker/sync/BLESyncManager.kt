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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class BLESyncManager(
    private val context: Context
): SyncManager() {
    companion object {
        private val TAG = BLESyncManager::class.simpleName
    }

    val dataClient by lazy { Wearable.getDataClient(context) }

    init {
        BLESyncReceiverService.callbackList = callbackList
    }

    override suspend fun send(key: String, value: String) {
        val path = context.getString(R.string.ble_sync_path)
        val asset = Asset.createFromBytes(value.toByteArray())

        Log.d(TAG, "send: $key, $value")

        val request = PutDataMapRequest.create(path).apply {
            dataMap.putString("key", key)
            dataMap.putAsset("data", asset)
        }
            .asPutDataRequest()
            .setUrgent()

        val result = dataClient.putDataItem(request).await()

        Log.d(TAG, "DataItem saved: $result")
    }

    class BLESyncReceiverService: WearableListenerService() {
        companion object {
            private val TAG = BLESyncReceiverService::class.simpleName
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
                val dataMapItem = DataMapItem.fromDataItem(dataEvent.dataItem)
                val key = dataMapItem.dataMap.getString("key")!!

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