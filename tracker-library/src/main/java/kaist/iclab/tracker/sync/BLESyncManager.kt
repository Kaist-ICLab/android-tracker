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
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class BLESyncManager(
    private val context: Context
): SyncManager() {
    companion object {
        private const val PATH = "AndroidTracker"
        private val TAG = BLESyncManager::class.simpleName
    }

    init {
        BLESyncReceiverService.callbackList = callbackList
    }

    private val dataClient by lazy { Wearable.getDataClient(context) }

    override suspend fun send(key: String, value: String) {
        val asset = Asset.createFromBytes(value.toByteArray())
        val request = PutDataMapRequest.create("/${PATH}/${key}/${System.currentTimeMillis()}").apply {
            dataMap.putAsset("data", asset)
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request).await()
        Log.v(TAG, "send: $key")
    }

    class BLESyncReceiverService: WearableListenerService() {
        companion object {
            var callbackList = mutableMapOf<String, MutableList<(JsonElement) -> Unit>>()
        }

        private val dataClient by lazy { Wearable.getDataClient(this) }

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
                dataClient.getFdForAsset(asset).addOnSuccessListener {
                    onAssetSuccessListener(callbacks, it)
                }
            }
        }
    }
}