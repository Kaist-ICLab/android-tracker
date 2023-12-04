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
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import java.time.Duration
import java.time.Instant
class MainActivity : ComponentActivity(){
    private var count = 0
    private val dataClient by lazy { Wearable.getDataClient(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val collectorRepository by inject<CollectorRepository>()

        setContent {
            WearApp(
                collectorRepository,
                onSendDataClick = ::sendData
            )
        }
    }


    private fun sendData() {
        count += 1
        Log.d(TAG, "SEND DATA")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = PutDataMapRequest.create(DATA_PATH).apply {
                    dataMap.putInt(DATA_KEY, count)
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

    companion object {
        private const val TAG = "MainActivity"
        private const val DATA_PATH = "/data"
        private const val DATA_KEY = "data"

    }

}

@Composable
fun WearApp(
    collectorRepository: CollectorRepository,
    onSendDataClick: () -> Unit,
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
                Button(onClick = onSendDataClick) {
                    Text(text = "SEND")
                }
            }
        }
    }
}