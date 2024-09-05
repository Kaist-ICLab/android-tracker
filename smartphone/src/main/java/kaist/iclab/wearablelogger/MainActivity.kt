package kaist.iclab.wearablelogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kaist.iclab.wearablelogger.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }


//    private val TAG = javaClass.simpleName
//    private val dataClient by lazy { Wearable.getDataClient(this) }
//    private val dataReceiver:DataReceiver by inject()
//    override fun onResume() {
//        super.onResume()
//        dataClient.addListener(dataReceiver)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        dataClient.removeListener(dataReceiver)
//    }
}


