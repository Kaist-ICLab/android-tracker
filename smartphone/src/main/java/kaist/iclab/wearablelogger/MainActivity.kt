package kaist.iclab.wearablelogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.wearable.Wearable
import kaist.iclab.wearablelogger.ui.MainApp
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val TAG = javaClass.simpleName
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val dataReceiver:DataReceiver by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(dataReceiver)
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(dataReceiver)
    }
}


