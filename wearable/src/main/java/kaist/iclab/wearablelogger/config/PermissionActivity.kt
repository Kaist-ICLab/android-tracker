package kaist.iclab.wearablelogger.config

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

open class PermissionActivity:ComponentActivity() {
    private val TAG = javaClass.simpleName

    val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        Log.d(TAG, it.toString())
    }
}