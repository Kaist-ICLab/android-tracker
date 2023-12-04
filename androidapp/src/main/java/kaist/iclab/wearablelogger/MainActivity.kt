package kaist.iclab.wearablelogger

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("VisibleForTests")
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val clientDataViewModel by viewModels<ClientDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(clientDataViewModel.events, sendData = {text->sendData(text)})
        }
    }

    private fun sendData(text: String) {
        val DATA_PATH = "/data"
        val DATA_KEY = "data"
        Log.d(TAG, "SEND DATA")
        lifecycleScope.launch {
            try {
                val request = PutDataMapRequest.create(DATA_PATH).apply {
                    dataMap.putString(DATA_KEY, text)
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

    override fun onResume() {
        super.onResume()
        dataClient.addListener(clientDataViewModel)

    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(clientDataViewModel)
    }
}

@Composable
fun MainApp(events: List<Event>, sendData: (String) -> Unit) {
    var text by remember { mutableStateOf("Hello") }
    MaterialTheme {
        LazyColumn {
            item {
                Column{
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Label") }
                    )
                    Button(
                        onClick = { sendData(text) }) {
                        Text("SENDDATA")
                    }
                }
            }
            items(events) {
                Column {
                    Text(it.title)
                    Text(it.text)
                    Divider()
                }
            }
        }
    }
}