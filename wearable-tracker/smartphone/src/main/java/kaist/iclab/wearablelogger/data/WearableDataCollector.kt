package kaist.iclab.wearablelogger.data

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.database.DatabaseInterface

class WearableDataCollector(
    context: Context,
    database: DatabaseInterface
) : AbstractCollector(context, database) {

    override val NAME: String ="Wearable"
    override val permissions: Array<String> = arrayOf()

    private val dataClient by lazy { Wearable.getDataClient(context) }


    private val trigger = object : DataClient.OnDataChangedListener {
        override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
            dataEventBuffer.map { dataEvent ->
                val data = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                database.insert(
                    NAME,
                    mapOf(
                        "timestamp" to (data.getLong("timestamp") ?: ""),
                        "acc" to (data.getString("acc") ?: ""),
                        "ppg" to (data.getString("ppg") ?: ""),
                        "hr" to (data.getString("hr") ?: ""),
                        "ibi" to (data.getString("ibi") ?: "")
                    )
                )
            }
        }
    }

    override fun isAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun start() {
        dataClient.addListener(trigger)
    }

    override fun stop() {
        dataClient.removeListener(trigger)
    }
}

