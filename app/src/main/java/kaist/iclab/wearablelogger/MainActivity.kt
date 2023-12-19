/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package kaist.iclab.wearablelogger

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.db.MyDataRoomDB
import kaist.iclab.wearablelogger.db.PpgDao
import kaist.iclab.wearablelogger.db.PpgEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import java.time.Duration
import java.time.Instant
class MainActivity : ComponentActivity(){
    private val dataClient by lazy { Wearable.getDataClient(this) }
    lateinit var db: MyDataRoomDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val collectorRepository by inject<CollectorRepository>()
        db = Room.databaseBuilder(
            this,
            MyDataRoomDB::class.java,
            "MyDataRoomDB"
        )
            .fallbackToDestructiveMigration() // For Dev Phase!
            .build()
        setContent {
            WearApp(
                collectorRepository,
                onSendDataClick = ::sendData,
                onFlushDataClick = ::flushData,
            )
        }
    }


    private fun sendData() {
        Log.d(TAG, "SEND DATA")
        val ppgDao = db.ppgDao()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedDataList: List<PpgEntity> = async {
                    ppgDao.getAll()
                }.await()
//
                Log.d(TAG, "savedDataList: ${savedDataList.toString()}")
                val alSavedDataList = (savedDataList.toTypedArray()).toCollection(ArrayList<PpgEntity>())
                val dataMapList = ArrayList<DataMap>()
                for (entity in alSavedDataList) {
//                    val intTimeStamp = entity.timestamp.toInt()
//                    val intPpgData = entity.ppgData
//                    val intArrList : ArrayList<Int> = ArrayList<Int>().apply {
//                        add(entity.timestamp.toInt())
//                        add(entity.ppgData)
//                    }
                    val longArrEntity = longArrayOf(entity.timestamp, entity.ppgData.toLong())
                    val dataMap = DataMap().apply {
                        putLongArray(
                            PPG_DATA_KEY,
                            longArrEntity
                        )
                    }

                    Log.d("debuggingDataType", "${longArrEntity.toList()}")
                    dataMapList.add(dataMap)
                }
                val request = PutDataMapRequest.create(DATA_PATH).apply {
                    dataMap.putDataMapArrayList(DATA_KEY, dataMapList)
                }
                    .asPutDataRequest()
                    .setUrgent()

                val result = dataClient.putDataItem(request).await()

                Log.d(TAG, "DataItem saved: $result")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Saving DataItem failed: $exception")
            }
        }

    }
    private fun flushData() {
        Log.d(TAG, "Flush DATA")
        val ppgDao = db.ppgDao()
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                ppgDao.deleteAll()
                Log.d(TAG, "deleteAll()")
            }.join()
            launch {
                val savedDataList = ppgDao.getAll()
                Log.d(TAG, "after deleteALl(): ${savedDataList.toString()}")
            }

        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DATA_PATH = "/data"
        private const val DATA_KEY = "data"
        private const val PPG_DATA_KEY = "ppg"

    }

}

@Composable
fun WearApp(
    collectorRepository: CollectorRepository,
    onSendDataClick: () -> Unit,
    onFlushDataClick: () -> Unit,
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {collectorRepository.start()}) {
                    Text("START")
                }
                Button(onClick = { collectorRepository.stop() }) {
                    Text(text = "STOP")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onSendDataClick) {
                    Text(text = "SEND")
                }
                Button(onClick = onSendDataClick) {
                    Text(text = "SYNC")
                }
                Button(onClick = onFlushDataClick) {
                    Text(text = "Flush")
                }
            }
        }
    }
}