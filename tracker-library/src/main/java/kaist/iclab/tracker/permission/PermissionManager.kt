package kaist.iclab.tracker.permission

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing app permissions at runtime.
 * This interface provides methods to check, request, and track the state of permissions.
 */
interface PermissionManager {
    /**
     * Binds the permission manager to the given activity.
     * Since permission requests depend on the activity lifecycle, an activity reference is required.
     * This method must be called before requesting permissions.
     *
     * @param activity The [ComponentActivity] where permissions will be managed.
     */
    fun bind(activity: ComponentActivity)

    /**
     * A function that provides a Flow to track the state of requested permissions.
     *
     * @param permissions An array of permission IDs to monitor.
     * @return A Flow emitting a map where each key is a permission string,
     *         and the value represents its corresponding PermissionState.
     */
    fun getPermissionFlow(
        permissions: Array<String>
    ): StateFlow<Map<String, PermissionState>>

    /**
     * Requests one or more permissions from the user at runtime.
     * Note: special permissions need to be separately called from the runtime permission.
     *
     * @param permissions An array of permission IDs to request
     *                    (e.g., `[Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO]`).
     * @param onResult An optional callback that receives a `Boolean` indicating whether
     *                 all requested permissions were granted (`true`) or at least one was denied (`false`).
     */
    fun request(permissions: Array<String>)
}
