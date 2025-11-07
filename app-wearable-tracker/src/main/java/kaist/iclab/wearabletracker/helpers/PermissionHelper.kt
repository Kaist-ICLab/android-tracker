package kaist.iclab.wearabletracker.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kaist.iclab.tracker.permission.AndroidPermissionManager

/**
 * Helper utility for permission-related operations in the wearable tracker app.
 */
object PermissionHelper {
    /**
     * Check if notification permission is granted.
     * Returns true if granted or if running on Android < 13 (permission not required).
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Notification permission not required on Android < 13
            return true
        }
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request notification permission if not already granted.
     * Only requests on Android 13+ (TIRAMISU) and above.
     * 
     * @param context The context to check permission
     * @param permissionManager The AndroidPermissionManager instance to request permission
     */
    fun requestNotificationPermissionIfNeeded(
        context: Context,
        permissionManager: AndroidPermissionManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted(context)) {
                permissionManager.request(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }
}

