package kaist.iclab.wearablelogger.config

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat

class Permission(
    val androidContext: Context
) {
    fun checkPermissions() {
    //        ContextCompat.checkSelfPermission(androidContext, Manifest.permission.)
    }
}