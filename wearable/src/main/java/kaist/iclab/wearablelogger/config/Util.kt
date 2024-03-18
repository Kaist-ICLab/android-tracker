package kaist.iclab.wearablelogger.config

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

object Util {
    private val TAG = javaClass.simpleName
    fun isPermissionAllowed(androidContext: Context, permissions: List<String>): Boolean {
        return permissions.all { permission ->
            val permission_status = ContextCompat.checkSelfPermission(androidContext, permission)
            when (permission_status) {
                PackageManager.PERMISSION_GRANTED -> true
                PackageManager.PERMISSION_DENIED -> false
                else -> {
                    Log.d(TAG, "Unknown permission_status: $permission_status")
                    false
                }
            }
        }
    }
}

