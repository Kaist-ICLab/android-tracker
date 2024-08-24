package dev.iclab.tracker.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

typealias PermissionResult = Map<String, Boolean>
typealias PermissionResultCallback = (PermissionResult) -> Unit
private typealias ActivityWeakRef = WeakReference<PermissionActivity>


class PermissionManagerImpl(
    private val context: Context,
) : PermissionManagerInterface {
    companion object {
        const val TAG = "PermissionManager"
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
        ).filterNotNull()
    }

    private val rationaleMap: Map<String, String> = mapOf(
        Manifest.permission.ACCESS_FINE_LOCATION to "This feature requires fine-grained location",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            Manifest.permission.ACCESS_BACKGROUND_LOCATION to "This feature requires access for the location while running in the background"
        else "NONE" to "NONE"
    )
    private var activityWeakRef: ActivityWeakRef? = null
    override var onPermissionResult: PermissionResultCallback? = null

    /** Stores [activity] using a [WeakReference]. Call it on [Activity.onStart]
     */
    @MainThread
    override fun attach(activity: PermissionActivity) {
        activityWeakRef = WeakReference(activity)
    }

    private fun getActivity(): PermissionActivity {
        return activityWeakRef?.get()
            ?: throw IllegalStateException("PermissionActivity not attached")
    }


    override fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /*
    * Request multiple permissions at once:
    * It will control all about rationale, priority order of permission
    * */
    override fun request(
        permissions: Array<String>,
        onResult: PermissionResultCallback?
    ) {
        /* Recursively handle the permissions */
        if (permissions.isEmpty()) {
            onResult?.invoke(emptyMap())
        } else {
            val permission = permissions.first()
            if (locationPermissions.contains(permission)) {
                val requiredLocationPermissions =
                    permissions.filter { locationPermissions.contains(it) }
                val remainedPermissions = permissions.filter { !locationPermissions.contains(it) }
                handleLocationPermission(requiredLocationPermissions.toTypedArray()) { locationGrantedMap ->
                    request(remainedPermissions.toTypedArray()) { grantedMap ->
                        onResult?.invoke(locationGrantedMap + grantedMap)
                    }
                }
            } else {
                requestPermissionWithRationale(arrayOf(permission), permission) { granted ->
                    request(permissions.drop(1).toTypedArray()) { ret ->
                        onResult?.invoke(granted + ret)
                    }
                }
            }
        }
    }

    private fun requestPermissions(
        permissions: Array<String>, onResult: PermissionResultCallback
    ) {
        val activity = getActivity()
        onPermissionResult = onResult
        activity.permissionLauncher.launch(permissions)
    }

    /* Show Rationale Dialog if required
    * */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showRationale(
        permission: String,
        onResult: (accepted: Boolean) -> Unit,
    ) {
        val activity = getActivity()
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage(rationaleMap[permission])
                .setPositiveButton("OK") { dialog, _ ->
                    onResult.invoke(true)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    onResult.invoke(false)
                    dialog.dismiss()
                }
                .show()
        } else {
            onResult.invoke(true)
        }
    }

    /*
    * Request Permission with rationale if required
    * */
    private fun requestPermissionWithRationale(
        permissions: Array<String>, rationale: String, onResult: PermissionResultCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showRationale(rationale) { accepted ->
                if (accepted) {
                    requestPermissions(permissions, onResult)
                } else {
                    onResult.invoke(permissions.associateWith { false }.toMap())
                }
            }
        } else {
            requestPermissions(permissions, onResult)
        }
    }

    private fun handleLocationPermission(
        permissions: Array<String>,
        onResult: PermissionResultCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            handleBackgroundLocationPermission(permissions, onResult)
        }else {
            handleForegroundLocationPermission(permissions, onResult)
        }
    }

    private fun handleForegroundLocationPermission(
        permissions: Array<String>,
        onResult: PermissionResultCallback
    ) {
        if(!permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissionWithRationale(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                onResult
            )
        }else{
            requestPermissionWithRationale(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ){ grantedMap->
                onResult.invoke(permissions.associateWith { grantedMap[it]!!}.toMap())
            }
        }
    }

    /* ACCESS_BACKGROUND_LOCATION requires ACCESS_FINE_LOCATION, but
    * ACCESS_BACKGROUND_LOCATION can not be granted together with ACCESS_FINE_LOCATION
    * https://developer.android.com/develop/sensors-and-location/location/permissions?hl=ko#request-only-foreground
    * */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handleBackgroundLocationPermission(
        permissions: Array<String>,
        onResult: PermissionResultCallback
    ) {
        handleForegroundLocationPermission(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )) { grantedMap->
            if (grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                requestPermissionWithRationale(
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) { it ->
                    val ret = permissions.associateWith { true }.toMap() +
                            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to it[Manifest.permission.ACCESS_BACKGROUND_LOCATION]!!)
                    onResult.invoke(ret)
                }
            } else {
                onResult.invoke(permissions.associateWith { false }.toMap())
            }
        }
    }



}