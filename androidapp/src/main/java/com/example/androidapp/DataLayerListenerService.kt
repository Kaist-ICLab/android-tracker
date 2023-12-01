package com.example.androidapp

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataLayerListenerService : WearableListenerService() {

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            when (uri.path) {
                DATA_PATH -> {
                    Log.d(TAG, dataEvent.dataItem.toString())
                }
            }
        }
    }
    companion object {
        private const val TAG = "DataLayerService"

        private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"
        const val DATA_PATH = "/data"
    }
}