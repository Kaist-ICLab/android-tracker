package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector
import com.samsung.android.sdk.health.data.request.DataTypes

/**
 * Data class representing permission icon mapping
 */
private data class PermissionConfig(
    val permissionId: String,
    val icon: ImageVector
)

/**
 * Permission icon mappings - maps permission IDs to their icons
 */
private val permissionConfigs = buildList {
    // Add conditional permissions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(PermissionConfig(
            permissionId = Manifest.permission.POST_NOTIFICATIONS,
            icon = Icons.Filled.Notifications
        ))
    }
    add(PermissionConfig(
        permissionId = Manifest.permission.ACCESS_FINE_LOCATION,
        icon = Icons.Filled.LocationOn
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.READ_CONTACTS,
        icon = Icons.Filled.Contacts
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.CAMERA,
        icon = Icons.Filled.Camera
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.RECORD_AUDIO,
        icon = Icons.Filled.Mic
    ))
    // Android 13+ uses granular media permissions, Android 12 and below uses READ_EXTERNAL_STORAGE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(PermissionConfig(
            permissionId = Manifest.permission.READ_MEDIA_IMAGES,
            icon = Icons.Filled.Storage
        ))
        add(PermissionConfig(
            permissionId = Manifest.permission.READ_MEDIA_VIDEO,
            icon = Icons.Filled.Storage
        ))
        add(PermissionConfig(
            permissionId = Manifest.permission.READ_MEDIA_AUDIO,
            icon = Icons.Filled.Storage
        ))
    } else {
        add(PermissionConfig(
            permissionId = Manifest.permission.READ_EXTERNAL_STORAGE,
            icon = Icons.Filled.Storage
        ))
    }
    add(PermissionConfig(
        permissionId = Manifest.permission.READ_CALENDAR,
        icon = Icons.Filled.CalendarMonth
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.BODY_SENSORS,
        icon = Icons.Filled.FitnessCenter
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
        icon = Icons.Filled.Accessibility
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
        icon = Icons.Filled.Phone
    ))
    add(PermissionConfig(
        permissionId = Manifest.permission.PACKAGE_USAGE_STATS,
        icon = Icons.Filled.Settings
    ))
    // Samsung Health Steps
    add(PermissionConfig(
        permissionId = DataTypes.STEPS.name,
        icon = Icons.Filled.DirectionsWalk
    ))
}

/**
 * Gets permission icon by permission ID
 */
fun getPermissionIcon(permissionId: String): ImageVector {
    return permissionConfigs.find { it.permissionId == permissionId }?.icon
        ?: Icons.Filled.Settings
}

/**
 * Opens the appropriate settings page for a permission based on its ID.
 * This allows users to change or revoke granted permissions.
 */
fun openPermissionSettings(context: Context, permissionId: String) {
    val intent = when (permissionId) {
        Manifest.permission.PACKAGE_USAGE_STATS -> {
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE -> {
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }
        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        }
        DataTypes.STEPS.name -> {
            // Samsung Health permissions - open app details where user can manage permissions
            // Samsung Health permissions are managed through the Samsung Health app
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        else -> {
            // Regular runtime permissions - open app details page
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }
    
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: open general app settings
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(fallbackIntent)
    }
}
