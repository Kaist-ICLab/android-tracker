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
import kaist.iclab.tracker.listener.AccessibilityListener
import kaist.iclab.tracker.listener.NotificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.lang.ref.WeakReference

class PermissionManagerImpl(
    private val context: Context
) : PermissionManagerInterface {
    private val permissions = Permission.supportedPermissions.map { it.ids[0] }.toList()

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
            else -> getRuntimePermissionState(permission)
        }
    }

    private fun getRuntimePermissionState(permission: String): PermissionState {
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
        val permissions_ = permissions.filter {
            setOf(PermissionState.NOT_REQUESTED, PermissionState.RATIONALE_REQUIRED).contains(
                permissionStateFlow.value[it]
            )
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
        } else if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions_) {
            Log.d("PERMISSION", "ACCESS_BACKGROUND_LOCATION requested")
            /*ACCESS_FINE_LOCATION is required before turning on this permission*/
            requestNormalPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            ) {
                if (it) {
                    requestBackgroundLocation(onResult)
                } else {
                    onResult?.invoke(false)
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
            while (true) {
                sleep(1000)
                if (getPackageUsageStatsPermissionState() == PermissionState.GRANTED) {
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
}