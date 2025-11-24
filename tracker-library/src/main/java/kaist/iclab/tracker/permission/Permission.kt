package kaist.iclab.tracker.permission

import android.Manifest
import android.os.Build
import com.samsung.android.sdk.health.data.request.DataTypes

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
            Permission(
                name = "Background Location",
                ids = arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                description = "Allows the app to access location in the background"
            ),
            Permission(
                name = "Read Contacts",
                ids = arrayOf(Manifest.permission.READ_CONTACTS),
                description = "Allows the app to read contact information"
            ),
            Permission(
                name = "Camera",
                ids = arrayOf(Manifest.permission.CAMERA),
                description = "Allows the app to take photos and videos"
            ),
            Permission(
                name = "Microphone",
                ids = arrayOf(Manifest.permission.RECORD_AUDIO),
                description = "Allows the app to record audio"
            ),
            // Android 13+ uses granular media permissions, Android 12 and below uses READ_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Permission(
                    name = "Media Images",
                    ids = arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    description = "Allows the app to access images on device"
                )
            } else {
                Permission(
                    name = "Storage",
                    ids = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    description = "Allows the app to access device storage"
                )
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Permission(
                name = "Media Video",
                ids = arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                description = "Allows the app to access videos on device"
            ) else null,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Permission(
                name = "Media Audio",
                ids = arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                description = "Allows the app to access audio files on device"
            ) else null,
            Permission(
                name = "Read Calendar",
                ids = arrayOf(Manifest.permission.READ_CALENDAR),
                description = "Allows the app to read calendar events"
            ),
            Permission(
                name = "Activity Recognition",
                ids = arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                description = "Allows the app to recognize physical activity"
            ),
            Permission(
                name = "Body Sensors",
                ids = listOfNotNull(
                    Manifest.permission.BODY_SENSORS,
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null
                ).toTypedArray(),
                description = "Allows the app to access data from body sensors like heart rate"
            ),
            Permission(
                name = "Accessibility Service",
                ids = arrayOf(Manifest.permission.BIND_ACCESSIBILITY_SERVICE),
                description = "Allows the app to monitor user interactions"
            ),
            Permission(
                name = "Notification Listener",
                ids = arrayOf(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE),
                description = "Allows the app to listen to notifications"
            ),
            Permission(
                name = "Usage Stats",
                ids = arrayOf(Manifest.permission.PACKAGE_USAGE_STATS),
                description = "Allows the app to collect usage statistics of other applications"
            ),
            Permission(
                name = "Read Steps (Samsung Health)",
                ids = arrayOf(DataTypes.STEPS.name),
                description = "Allows the app to read number of steps"
            )
        ).toTypedArray()
    }

    /**
     * Compares two Permission objects for equality.
     * Two Permission objects are considered equal if they have the same name, ids array content, and description.
     * 
     * @param other The object to compare with this Permission
     * @return true if the objects are equal, false otherwise
     * 
     * Note: Uses contentEquals() for the ids array to compare array contents rather than array references.
     */
    override fun equals(other: Any?): Boolean {
        // Same reference - objects are identical
        if (this === other) return true
        // Different class types - not equal
        if (javaClass != other?.javaClass) return false

        // Safe cast after type check
        other as Permission

        // Compare all fields: name, ids array content, and description
        if (name != other.name) return false
        if (!ids.contentEquals(other.ids)) return false
        if (description != other.description) return false

        return true
    }

    /**
     * Generates a hash code for this Permission object.
     * Used by hash-based collections (HashSet, HashMap) for efficient storage and lookup.
     * 
     * @return A hash code value for this object
     * 
     * Note: 
     * - Must be consistent with equals(): if two objects are equal, they must have the same hashCode
     * - Uses contentHashCode() for the ids array to hash based on array contents
     * - Uses prime number 31 for combining hash codes (common practice in Java/Kotlin)
     */
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + ids.contentHashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}

