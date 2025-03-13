package kaist.iclab.tracker.permission

import android.Manifest
import android.os.Build


/**
 * Data class for managing app permissions.
 *
 * @property name The user-friendly name of the permission (e.g., "Camera Permission", "Location Permission").
 * @property ids  A list of permission IDs to request (e.g., [Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO]).
 *                Some permissions must be requested together (e.g., [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]).
 * @property description A brief explanation of why the permission is needed, shown to the user.
 */
data class Permission(
    val name: String,
    val ids: Array<String>, /*Some permission required to requested together*/
    val description: String
) {
    companion object {
        val supportedPermissions: Array<Permission> = listOfNotNull(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Permission(
                name = "Post Notifications",
                ids = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                description = "Allows the app to post notifications"
            ) else null,
            Permission(
                name = "Access Location",
                ids = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                description = "Allows the app to access precise location"
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Permission(
                name = "Background Location",
                ids = arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                description = "Allows the app to access location in the background"
            ) else null,
            Permission(
                name = "Body Sensors",
                ids = arrayOf(Manifest.permission.BODY_SENSORS),
                description = "Allows the app to access data from body sensors like heart rate"
            ),
            Permission(
                name = "Read Users' Interaction",
                ids = arrayOf(Manifest.permission.BIND_ACCESSIBILITY_SERVICE),
                description = "Allows the app to monitor user interactions"
            ),
            Permission(
                name = "Read Notifications",
                ids = arrayOf(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE),
                description = "Allows the app to listen to notifications"
            ),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Permission(
                name = "Read App Usage Log",
                ids = arrayOf(Manifest.permission.PACKAGE_USAGE_STATS),
                description = "Allows the app to collect usage statistics of other applications"
            ) else null
        ).toTypedArray()
    }
}

