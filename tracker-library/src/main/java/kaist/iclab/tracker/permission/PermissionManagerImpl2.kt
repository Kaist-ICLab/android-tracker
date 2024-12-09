package kaist.iclab.tracker.permission

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import kaist.iclab.tracker.listeners.AccessibilityListener
import kaist.iclab.tracker.listeners.NotificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.lang.ref.WeakReference


class PermissionManagerImpl2(
    private val context: Context
) : PermissionManagerInterface {
    private val permissions = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null,
        Manifest.permission.BODY_SENSORS,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_BASIC_PHONE_STATE else null,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.PACKAGE_USAGE_STATS else null,
    )
    private val _permissionStateFlow = MutableStateFlow(
        permissions.associate { it to PermissionState.NOT_REQUESTED }
    )
    override val permissionStateFlow: StateFlow<Map<String, PermissionState>> =
        _permissionStateFlow.asStateFlow()

    private var activityWeakRef: WeakReference<PermissionActivity>? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /*Stores [activity] using a [WeakReference]. Call it on [Activity.onStart]*/
    @MainThread
    override fun initialize(activity: PermissionActivity) {
        activityWeakRef = WeakReference(activity)
    }

    private fun getActivity(): PermissionActivity {
        return activityWeakRef?.get()
            ?: throw IllegalStateException("PermissionActivity not attached")
    }

    private fun getPermissionState(permission: String): PermissionState {
        return when (permission) {
            Manifest.permission.PACKAGE_USAGE_STATS -> getPackageUsageStatsPermissionState()
            Manifest.permission.BIND_ACCESSIBILITY_SERVICE -> getBindAccessibilityServicePermissionState()
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> getBindNotificationListenerServicePermissionState()
            else -> getNormalPermissionState(permission)
        }
    }

    private fun getNormalPermissionState(permission: String): PermissionState {
        return when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                PermissionState.GRANTED
            }

            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission) -> {
                PermissionState.RATIONALE_REQUIRED
            }

            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        getActivity(),
                        permission
                    ) -> {
                PermissionState.NOT_REQUESTED
            }

            else -> {
                PermissionState.PERMANENTLY_DENIED
            }
        }
    }

    private fun getPackageUsageStatsPermissionState(): PermissionState {
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
        return if (mode == AppOpsManager.MODE_ALLOWED) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    private fun getBindAccessibilityServicePermissionState(): PermissionState {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return PermissionState.NOT_REQUESTED

        val enabledServicesList = TextUtils.split(enabledServices, ":")
        val fullServiceName =
            "${context.packageName}/${AccessibilityListener::class.java.canonicalName}"

        val isServiceRunning = accessibilityManager.getEnabledAccessibilityServiceList(
            FEEDBACK_ALL_MASK
        ).any { it.id == fullServiceName }

        return if (enabledServicesList.contains(fullServiceName) && isServiceRunning) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    private fun getBindNotificationListenerServicePermissionState(): PermissionState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return if (notificationManager.isNotificationListenerAccessGranted(
                    ComponentName(
                        context,
                        NotificationListener::class.java
                    )
                )
            ) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
        } else {
            return if (NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(context.packageName)
            ) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
        }
    }

    override fun checkPermissions() {
        _permissionStateFlow.value = permissions.associate { it to getPermissionState(it) }
    }

    @SuppressLint("InlinedApi")
    override fun request(
        permissions: Array<String>,
        onResult: ((result: Boolean) -> Unit)?
    ) {
        val permissions_ = permissions.filter{
            permissionStateFlow.value[it] == PermissionState.NOT_REQUESTED
        }.toTypedArray()
        if (Manifest.permission.PACKAGE_USAGE_STATS in permissions_) {
            requestPackageUsageStat {
                if (it) {
                    Log.d("PERMISSION", "PACKAGE_USAGE_STATS granted")
                    request(permissions_.filter { it != Manifest.permission.PACKAGE_USAGE_STATS }
                        .toTypedArray(), onResult)
                } else {
                    onResult?.invoke(false)
                }
            }
        } else if (Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE in permissions_) {
            requestBindNotificationListenerService {
                if (it) {
                    Log.d("PERMISSION", "BIND_NOTIFICATION granted")
                    request(permissions_.filter { it != Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE }
                        .toTypedArray(), onResult)
                } else {
                    onResult?.invoke(false)
                }
            }
        } else if (Manifest.permission.BIND_ACCESSIBILITY_SERVICE in permissions_) {
            requestBindAccessibilityService {
                if (it) {
                    Log.d("PERMISSION", "BIND_ACCESSIBILITY granted")
                    request(permissions_.filter { it != Manifest.permission.BIND_ACCESSIBILITY_SERVICE }
                        .toTypedArray(), onResult)
                } else {
                    onResult?.invoke(false)
                }
            }
        } else if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions_
            && permissionStateFlow.value[Manifest.permission.ACCESS_BACKGROUND_LOCATION] != PermissionState.GRANTED) {
            if (Manifest.permission.ACCESS_FINE_LOCATION !in permissions_) {
                onResult?.invoke(false)
                // ACCESS_BACKGROUND_LOCATION requires ACCESS_FINE_LOCATION
            } else {
                requestNormalPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ) {
                    if (it) {
                        requestBackgroundLocation(onResult)
                    } else {
                        onResult?.invoke(false)
                    }
                }
            }
        } else {
            requestNormalPermissions(permissions_, onResult)
        }
    }

    private fun requestPackageUsageStat(
        onResult: ((result: Boolean) -> Unit)?
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            onResult?.invoke(true)
            return
        }
        if (permissionStateFlow.value[Manifest.permission.PACKAGE_USAGE_STATS] == PermissionState.GRANTED) {
            onResult?.invoke(true)
            return
        }
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            while(true){
                sleep(1000)
                if(getPackageUsageStatsPermissionState() == PermissionState.GRANTED){
                    checkPermissions()
                    Log.d("PERMISSION_NEW", "PACKAGE_USAGE_STATS granted")
                    onResult?.invoke(true)
                    scope.cancel()
                    break
                }
            }
        }
        val activity = getActivity()
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        activity.startActivity(intent)
    }


    fun requestBackgroundLocation(
        onResult: ((result: Boolean) -> Unit)?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Background 권한 요청
            val activity = getActivity()
            activity.permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            scope.launch {
                permissionStateFlow.collect {
                    if (it[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == PermissionState.GRANTED) {
                        onResult?.invoke(true)
                        scope.cancel()
                    }
                }
            }
        }
    }

    private fun requestNormalPermissions(
        permissions: Array<String>,
        onResult: ((result: Boolean) -> Unit)?
    ) {
        scope.launch {
            permissionStateFlow.collect {
                if (permissions.all { permission -> it[permission] == PermissionState.GRANTED }) {
                    onResult?.invoke(true)
                    scope.cancel()
                }
            }
        }
        val activity = getActivity()
        activity.permissionLauncher.launch(permissions)
    }

    private fun requestBindAccessibilityService(
        onResult: ((result: Boolean) -> Unit)?
    ) {
        if (permissionStateFlow.value[Manifest.permission.BIND_ACCESSIBILITY_SERVICE] == PermissionState.GRANTED) {
            onResult?.invoke(true)
            return
        }
        scope.launch {
            permissionStateFlow.collect {
                if (it[Manifest.permission.BIND_ACCESSIBILITY_SERVICE] == PermissionState.GRANTED) {
                    onResult?.invoke(true)
                    scope.cancel()
                }
            }
        }
        val activity = getActivity()
        activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestBindNotificationListenerService(
        onResult: ((result: Boolean) -> Unit)?
    ) {
        if (permissionStateFlow.value[Manifest.permission.BIND_ACCESSIBILITY_SERVICE] == PermissionState.GRANTED) {
            onResult?.invoke(true)
            return
        }

        scope.launch {
            permissionStateFlow.collect {
                if (it[Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE] == PermissionState.GRANTED) {
                    onResult?.invoke(true)
                    scope.cancel()
                }
            }
        }
        val activity = getActivity()
        activity.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

//    TODO: Remove Showing Rationale Dialog


    /*
    * Request multiple permissions at once:
    * It will control all about rationale, priority order of permission
    * */
//    override fun request(
//        permissions: Array<String>,
//        onResult: PermissionResultCallback?
//    ) {
//        /* Recursively handle the permissions */
//        if (permissions.isEmpty()) {
//            onResult?.invoke(emptyMap())
//        } else {
//            val permission = permissions.first()
//            if (locationPermissions.contains(permission)) {
//                val requiredLocationPermissions =
//                    permissions.filter { locationPermissions.contains(it) }
//                val remainedPermissions = permissions.filter { !locationPermissions.contains(it) }
//                handleLocationPermission(requiredLocationPermissions.toTypedArray()) { locationGrantedMap ->
//                    request(remainedPermissions.toTypedArray()) { grantedMap ->
//                        onResult?.invoke(locationGrantedMap + grantedMap)
//                    }
//                }
//            } else {
//                requestPermissionWithRationale(arrayOf(permission), permission) { granted ->
//                    request(permissions.drop(1).toTypedArray()) { ret ->
//                        onResult?.invoke(granted + ret)
//                    }
//                }
//            }
//        }
//    }


//    private fun requestPermissions(
//        permissions: Array<String>, onResult: PermissionResultCallback
//    ) {
//        val activity = getActivity()
//        onPermissionResult = onResult
//        activity.permissionLauncher.launch(permissions)
//    }

    /* Show Rationale Dialog if required
    * */
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun showRationale(
//        permission: String,
//        onResult: (accepted: Boolean) -> Unit,
//    ) {
//        val activity = getActivity()
//        if (activity.shouldShowRequestPermissionRationale(permission)) {
//            AlertDialog.Builder(activity)
//                .setTitle("Permission Required")
//                .setMessage(rationaleMap[permission])
//                .setPositiveButton("OK") { dialog, _ ->
//                    onResult.invoke(true)
//                }
//                .setNegativeButton("Cancel") { dialog, _ ->
//                    onResult.invoke(false)
//                    dialog.dismiss()
//                }
//                .show()
//        } else {
//            onResult.invoke(true)
//        }
//    }

//    /*
//    * Request Permission with rationale if required
//    * */
//    private fun requestPermissionWithRationale(
//        permissions: Array<String>, rationale: String, onResult: PermissionResultCallback
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            showRationale(rationale) { accepted ->
////                if (accepted) {
////                    requestPermissions(permissions, onResult)
////                } else {
////                    onResult.invoke(permissions.associateWith { false }.toMap())
////                }
////            }
//        } else {
//            requestPermissions(permissions, onResult)
//        }
//    }
//
//    private fun handleLocationPermission(
//        permissions: Array<String>,
//        onResult: PermissionResultCallback
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
//            permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        ) {
//            handleBackgroundLocationPermission(permissions, onResult)
//        } else {
//            handleForegroundLocationPermission(permissions, onResult)
//        }
//    }
//
//    private fun handleForegroundLocationPermission(
//        permissions: Array<String>,
//        onResult: PermissionResultCallback
//    ) {
//        if (!permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            requestPermissionWithRationale(
//                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                onResult
//            )
//        } else {
//            requestPermissionWithRationale(
//                arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ),
//                Manifest.permission.ACCESS_FINE_LOCATION,
//            ) { grantedMap ->
//                onResult.invoke(permissions.associateWith { grantedMap[it]!! }.toMap())
//            }
//        }
//    }
//
//    /* ACCESS_BACKGROUND_LOCATION requires ACCESS_FINE_LOCATION, but
//    * ACCESS_BACKGROUND_LOCATION can not be granted together with ACCESS_FINE_LOCATION
//    * https://developer.android.com/develop/sensors-and-location/location/permissions?hl=ko#request-only-foreground
//    * */
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun handleBackgroundLocationPermission(
//        permissions: Array<String>,
//        onResult: PermissionResultCallback
//    ) {
//        handleForegroundLocationPermission(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        ) { grantedMap ->
//            if (grantedMap[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
//                requestPermissionWithRationale(
//                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ) { it ->
//                    val ret = permissions.associateWith { true }.toMap() +
//                            mapOf(Manifest.permission.ACCESS_FINE_LOCATION to it[Manifest.permission.ACCESS_BACKGROUND_LOCATION]!!)
//                    onResult.invoke(ret)
//                }
//            } else {
//                onResult.invoke(permissions.associateWith { false }.toMap())
//            }
//        }
//    }


}