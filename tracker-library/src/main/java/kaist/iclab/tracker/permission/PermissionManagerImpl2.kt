package kaist.iclab.tracker.permission

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.accessibility.AccessibilityManagerCompat
import kaist.iclab.tracker.listeners.AccessibilityListener
import kaist.iclab.tracker.listeners.NotificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

class PermissionManagerImpl2(
    private val context: Context,
    permissions: List<String> = emptyList()
) : PermissionManagerInterface {
    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
        ).filterNotNull()
    }

    // 권한 상태를 관리하는 StateFlow
    private val _permissionsState = MutableStateFlow<List<PermissionState>>(
        permissions.map { PermissionState(it, isPermissionGranted(it)) }
    )
    val permissionsState: StateFlow<List<PermissionState>> = _permissionsState.asStateFlow()


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

    override fun isPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }

    override fun isPermissionGranted(permission: String): Boolean {
        when (permission) {
            Manifest.permission.PACKAGE_USAGE_STATS -> return isPackageUsageStatsGranted()
            Manifest.permission.BIND_ACCESSIBILITY_SERVICE -> return isBindAccessibilityServiceGranted()
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> return isBindNotificationListenerServiceGranted()
            else -> return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isPackageUsageStatsGranted(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isBindAccessibilityServiceGranted(): Boolean {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val enabledServicesList = TextUtils.split(enabledServices, ":")
        val fullServiceName = "${context.packageName}/${AccessibilityListener::class.java.canonicalName}"

        val isServiceRunning = accessibilityManager.getEnabledAccessibilityServiceList(
            FEEDBACK_ALL_MASK
        ).any { it.id == fullServiceName }

        return enabledServicesList.contains(fullServiceName) && isServiceRunning
    }

    private fun isBindNotificationListenerServiceGranted(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager.isNotificationListenerAccessGranted(ComponentName(context, NotificationListener::class.java))
        }
        else {
            return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
        }
    }

    override fun checkPermissions() {
        _permissionsState.value = _permissionsState.value.map { it.copy(isGranted = isPermissionGranted(it.permission)) }
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
            }
            else {
                requestPermissionWithRationale(arrayOf(permission), permission) { granted ->
                    request(permissions.drop(1).toTypedArray()) { ret ->
                        onResult?.invoke(granted + ret)
                    }
                }
            }
        }
    }

    private fun requestBindNotificationListenerService(
        onResult: PermissionResultCallback?
    ) {
        val activity = getActivity()
        activity.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        CoroutineScope(Dispatchers.IO){
            permissionsState.collect{

            }
            onResult.invoke()
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
            AlertDialog.Builder(activity)
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
//            showRationale(rationale) { accepted ->
//                if (accepted) {
//                    requestPermissions(permissions, onResult)
//                } else {
//                    onResult.invoke(permissions.associateWith { false }.toMap())
//                }
//            }
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
        } else {
            handleForegroundLocationPermission(permissions, onResult)
        }
    }

    private fun handleForegroundLocationPermission(
        permissions: Array<String>,
        onResult: PermissionResultCallback
    ) {
        if (!permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissionWithRationale(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                Manifest.permission.ACCESS_COARSE_LOCATION,
                onResult
            )
        } else {
            requestPermissionWithRationale(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) { grantedMap ->
                onResult.invoke(permissions.associateWith { grantedMap[it]!! }.toMap())
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
        handleForegroundLocationPermission(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) { grantedMap ->
            if (grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                requestPermissionWithRationale(
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) { it ->
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