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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.db.AccEntity
import kaist.iclab.wearablelogger.db.HRIBIEntity
import kaist.iclab.wearablelogger.db.MyDataRoomDB
import kaist.iclab.wearablelogger.db.PpgDao
import kaist.iclab.wearablelogger.db.PpgEntity
import kaist.iclab.wearablelogger.db.SkinTempEntity
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
        // TODO: Implement Another Sensor
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ppgDataMapList = loadAndFormatPpgData()
                val accDataMapList = loadAndFormatAccData()
                val hrDataMapList = loadAndFormatHrData()
                val skinTempDataMapList = loadAndFormatSkinTempData()
                Log.d("joinedTest", (ppgDataMapList + accDataMapList + hrDataMapList + skinTempDataMapList).toString())
                val joinedList = ArrayList(ppgDataMapList + accDataMapList + hrDataMapList + skinTempDataMapList)
                val request = PutDataMapRequest.create(DATA_PATH).apply {
                    dataMap.putDataMapArrayList(DATA_KEY, joinedList)
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
    private suspend fun loadAndFormatPpgData() : ArrayList<DataMap>{
        val ppgDao = db.ppgDao()
        val savedDataList: List<PpgEntity> = ppgDao.getAll()
        Log.d(TAG, "savedPpgDataList: ${savedDataList.toString()}")
        val alSavedDataList = (savedDataList.toTypedArray()).toCollection(ArrayList<PpgEntity>())
        val dataMapList = ArrayList<DataMap>()
        for (entity in alSavedDataList) {
            val longArrEntity = longArrayOf(entity.timestamp, entity.ppgData.toLong())
            val dataMap = DataMap().apply {
                putLongArray(
                    PPG_DATA_KEY,
                    longArrEntity
                )
            }
            Log.d("debuggingPpgDataType", "${longArrEntity.toList()}")
            dataMapList.add(dataMap)
        }
        return dataMapList
    }
    private suspend fun loadAndFormatAccData() : ArrayList<DataMap>{
        val accDao = db.accDao()
        val savedDataList: List<AccEntity> = accDao.getAll()
        Log.d(TAG, "savedAccDataList: ${savedDataList.toString()}")
        val alSavedDataList = (savedDataList.toTypedArray()).toCollection(ArrayList<AccEntity>())
        val dataMapList = ArrayList<DataMap>()
        for (entity in alSavedDataList) {
            val longArrEntity = longArrayOf(entity.timestamp, entity.accData.toLong())
            val dataMap = DataMap().apply {
                putLongArray(
                    ACC_DATA_KEY,
                    longArrEntity
                )
            }
            Log.d("debuggingAccDataType", "${longArrEntity.toList()}")
            dataMapList.add(dataMap)
        }
        return dataMapList
    }
    private suspend fun loadAndFormatHrData() : ArrayList<DataMap>{
        val hrDao = db.hribiDao()
        val savedDataList: List<HRIBIEntity> = hrDao.getAll()
        Log.d(TAG, "savedHrDataList: ${savedDataList.toString()}")
        val alSavedDataList = (savedDataList.toTypedArray()).toCollection(ArrayList<HRIBIEntity>())
        val dataMapList = ArrayList<DataMap>()
        for (entity in alSavedDataList) {
            val longArrEntity = longArrayOf(entity.timestamp, entity.hribiData.toLong())
            val dataMap = DataMap().apply {
                putLongArray(
                    HR_DATA_KEY,
                    longArrEntity
                )
            }
            Log.d("debuggingHrDataType", "${longArrEntity.toList()}")
            dataMapList.add(dataMap)
        }
        return dataMapList
    }
    private suspend fun loadAndFormatSkinTempData() : ArrayList<DataMap>{
        val skinTempDao = db.skintempDao()
        val savedDataList: List<SkinTempEntity> = skinTempDao.getAll()
        Log.d(TAG, "savedSkinTempDataList: ${savedDataList.toString()}")
        val alSavedDataList = (savedDataList.toTypedArray()).toCollection(ArrayList<SkinTempEntity>())
        val dataMapList = ArrayList<DataMap>()
        for (entity in alSavedDataList) {
            val longArrEntity = longArrayOf(entity.timestamp, entity.skinTempData.toLong())
            val dataMap = DataMap().apply {
                putLongArray(
                    SKIN_TEMP_DATA_KEY,
                    longArrEntity
                )
            }
            Log.d("debuggingSkinTempDataType", "${longArrEntity.toList()}")
            dataMapList.add(dataMap)
        }
        return dataMapList
    }
    private fun flushData() {
        Log.d(TAG, "Flush DATA")
        val ppgDao = db.ppgDao()
        val accDao = db.accDao()
        val hribiDao = db.hribiDao()
        val skintempDao = db.skintempDao()
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                launch {
                    ppgDao.deleteAll()
                }
                launch {
                    accDao.deleteAll()
                }
                launch {
                    hribiDao.deleteAll()
                }
                launch {
                    skintempDao.deleteAll()
                }
                Log.d(TAG, "deleteAll()")
            }.join()
            launch {
                val savedDataListPpg = ppgDao.getAll()
                val savedDataListAcc = accDao.getAll()
                val savedDataListHribi = hribiDao.getAll()
                val savedDataListSkinTemp = skintempDao.getAll()
                Log.d(TAG, "after deleteALl(): ${savedDataListPpg + savedDataListAcc + savedDataListHribi + savedDataListSkinTemp}")
            }
        }
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val DATA_PATH = "/data"
        private const val DATA_KEY = "data"
        private const val PPG_DATA_KEY = "ppg"
        private const val ACC_DATA_KEY = "acc"
        private const val HR_DATA_KEY = "hr"
        private const val SKIN_TEMP_DATA_KEY = "skintemp"

    }

}

@Composable
fun WearApp(
    collectorRepository: CollectorRepository,
    onSendDataClick: () -> Unit,
    onFlushDataClick: () -> Unit,
) {
    var isStartClicked by remember { mutableStateOf(false)}
    var buttonText by remember { mutableStateOf("Start")}
    var buttonColor = if (isStartClicked) MaterialTheme.colors.error else MaterialTheme.colors.primary
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        isStartClicked = !isStartClicked
                        buttonText = if (isStartClicked) "Stop" else "Start"
                        if (isStartClicked) {
                            buttonText = "Stop"
                            collectorRepository.start()
                        }
                        else {
                            buttonText = "Start"
                            collectorRepository.stop()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val icon = if (isStartClicked) Icons.Rounded.Stop else Icons.Rounded.PlayArrow
                        Icon(
                            imageVector = icon,
                            contentDescription = "toggles measuring action",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onSendDataClick) {
                    Text(text = "SYNC")
                }
                Button(onClick = onFlushDataClick) {
                    Text(text = "FLUSH")
                }
            }
        }
    }
}