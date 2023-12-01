package com.example.androidapp

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import kotlinx.coroutines.launch

class ClientDataViewModel(
    application: Application
) : AndroidViewModel(application),
    DataClient.OnDataChangedListener {
    private val _events = mutableStateListOf<Event>()

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {

        // Do additional work for specific events
        dataEvents.forEach { dataEvent ->
            when (dataEvent.type) {
                DataEvent.TYPE_CHANGED -> {
                    when (dataEvent.dataItem.uri.path) {
                        DataLayerListenerService.DATA_PATH -> {
                            Log.d("ClientData", dataEvent.dataItem.toString())
                        }
                    }
                }
            }
        }
    }
}

/**
 * A data holder describing a client event.
 */
data class Event(
    @StringRes val title: Int,
    val text: String
)
