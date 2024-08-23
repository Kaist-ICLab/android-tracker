package dev.iclab.tracker

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class PermissionManager(
    private val caller: ActivityResultCaller,
    private val context: Context,
    private val shouldShowRationale: (permission: String) -> Boolean
) {
    companion object {
        const val TAG = "PermissionManager"
    }

    constructor(activity: ComponentActivity) : this(
        caller = activity,
        context = activity,
        shouldShowRationale = { it ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.shouldShowRequestPermissionRationale(it)
            } else {
                // If the device is running a version lower than Android 6.0, the permission do not need to be explained
                false
            }
        }
    )

    private var onPermissionRequestResult: ((grantedMap: Map<String, Boolean>) -> Unit)? = null
    private val requestPermissionLauncher = caller.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { GrantedMap -> onPermissionRequestResult?.invoke(GrantedMap) }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun request(
        permissions: Array<String>,
        onResult: ((grantedMap: Map<String, Boolean>) -> Unit)? = null
    ) {
        /* Designed to recursively try to get all permission */
        val grantedMap: MutableMap<String, Boolean> =
            permissions.associateWith { false }.toMutableMap()
        // Check the permissions are already granted or not
        var remains = permissions.filter {
            grantedMap[it] = isPermissionGranted(it)
            !isPermissionGranted(it)
        }
        Log.d(TAG, "Remaining permission: $remains")

        // Location Handling
        val requiredLocationPermission = listOfNotNull(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
        ).filter { remains.contains(it) }
        remains = remains.filter { !requiredLocationPermission.contains(it) }
        if (requiredLocationPermission.isNotEmpty()) {
            handleLocationPermission(requiredLocationPermission.toTypedArray()) { ret ->
                grantedMap.putAll(ret)
                request(remains.toTypedArray()) { ret2 ->
                    onResult?.invoke(grantedMap + ret2)
                }
            }
        } else {
            // Others
            if (remains.isEmpty()) {
                onResult?.invoke(grantedMap)
            } else {
                val first = remains.first()
                requestPermissionWithRationale(first) { granted ->
                    grantedMap[first] = granted
                    remains = remains.drop(1)
                    request(remains.toTypedArray()) { ret ->
                        onResult?.invoke(grantedMap + ret)
                    }
                }
            }
        }
    }

    private val rationaleMap: Map<String, String> = mapOf(
        Manifest.permission.ACCESS_FINE_LOCATION to "This feature requires fine-grained location",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            Manifest.permission.ACCESS_BACKGROUND_LOCATION to "This feature requires access for the location while running in the background"
        else "NONE" to "NONE"
    )

    private fun handleLocationPermission(
        permissions: Array<String>,
        onResult: (grantedMap: Map<String, Boolean>) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
            permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            /* ACCESS_BACKGROUND_LOCATION requires ACCESS_FINE_LOCATION, but
            * ACCESS_BACKGROUND_LOCATION can not be granted together with ACCESS_FINE_LOCATION
            * https://developer.android.com/develop/sensors-and-location/location/permissions?hl=ko#request-only-foreground
            * */
            if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                /* If ACCESS_FINE_LOCATION is not granted, request it first
                * */
                requestPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION) { granted ->
                    if (granted) {
                        requestBackgroundLocationPermission { onResult.invoke(it) }
                    } else {
                        onResult.invoke(
                            mapOf(
                                Manifest.permission.ACCESS_FINE_LOCATION to false,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION to false
                            )
                        )
                    }
                }
            }
            else {
                requestBackgroundLocationPermission { onResult.invoke(it) }
            }
        } else {
            requestPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION) { granted ->
                onResult.invoke(
                    mapOf(
                        Manifest.permission.ACCESS_FINE_LOCATION to granted,
                    )
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermission(onResult: (grantedMap: Map<String, Boolean>) -> Unit) {
        requestPermissionWithRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) { granted ->
            if (granted) {
                onResult.invoke(
                    mapOf(
                        Manifest.permission.ACCESS_FINE_LOCATION to true,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION to true
                    )
                )
            } else {
                onResult.invoke(
                    mapOf(
                        Manifest.permission.ACCESS_FINE_LOCATION to true,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION to false
                    )
                )
            }
        }

    }

    private fun requestPermission(
        permission: String, onResult: (granted: Boolean) -> Unit
    ) {
        val tmp = onPermissionRequestResult
        requestPermissionLauncher.launch(arrayOf(permission))
        onPermissionRequestResult = { grantedMap ->
            onPermissionRequestResult = tmp
            onResult.invoke(grantedMap[permission] == true)
        }
    }

    private fun showRationale(
        permission: String,
        onResult: (granted: Boolean) -> Unit,
    ) {
        if (shouldShowRationale(permission)) {
            AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage(rationaleMap[permission])
                .setPositiveButton("OK") { dialog, _ ->
                    Log.d(TAG, "Dialog OK")
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


    /*Using for permission groups, such as ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION*/
//    private fun requestPermissionsWithRationale(
//        permissions: Array<String>, onResult: (grantedMap: Map<String,Boolean>) -> Unit
//    ){
//        permissions.forEach {
//            showRationale(it){ accepted ->
//                if(accepted){
//                    requestPermission(it){ granted ->
//                        onResult.invoke(mapOf(it to granted))
//                    }
//                }else{
//                    onResult.invoke(mapOf(it to false))
//                }
//            }
//        }
//    }

    private fun requestPermissionWithRationale(
        permission: String, onResult: (granted: Boolean) -> Unit
    ) {
        showRationale(permission) { accepted ->
            if (accepted) {
                requestPermission(permission) { granted ->
                    onResult.invoke(granted)
                }
            } else {
                onResult.invoke(false)
            }
        }
    }
    }