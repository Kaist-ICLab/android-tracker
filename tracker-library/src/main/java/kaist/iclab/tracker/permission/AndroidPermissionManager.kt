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
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.error.AuthorizationException
import com.samsung.android.sdk.health.data.error.InvalidRequestException
import com.samsung.android.sdk.health.data.error.PlatformInternalException
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.request.DataTypes
import kaist.iclab.tracker.listener.AccessibilityListener
import kaist.iclab.tracker.listener.NotificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.collections.map
import kotlin.collections.toSet

class AndroidPermissionManager(
    private val context: Context
) : PermissionManager {
    private var activityWeakRef: WeakReference<ComponentActivity>? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private val permissions = Permission.supportedPermissions.flatMap { it.ids.toList() }.toList()
    private val permissionStateFlow = MutableStateFlow<Map<String, PermissionState>>(
        permissions.associate { it to PermissionState.NOT_REQUESTED }
    )

    val specialPermissions = mapOf(
        Manifest.permission.PACKAGE_USAGE_STATS to ::requestPackageUsageStat,
        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE to ::requestBindNotificationListenerService,
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE to ::requestBindAccessibilityService
    )

    val healthDataPermission = mapOf(
        DataTypes.STEPS.name to DataTypes.STEPS
    )
//
//    fun registerPermission(permissions: Array<String>) {
//        permissionStateFlow.value = permissionStateFlow.value.toMutableMap().apply {
//            putAll(permissions.associate { it to PermissionState.NOT_REQUESTED })
//        }
//    }

    /*Stores [activity] using a [WeakReference]. Call it on [Activity.onCreate]*/
    @MainThread
    override fun bind(activity: ComponentActivity) {
        activityWeakRef = WeakReference(activity)
        notifyChange()
        permissionLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                notifyChange()
            }
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                notifyChange() // Call notifyChange() every time activity resumes
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                activityWeakRef?.clear()
                activityWeakRef = null
                permissionLauncher = null
            }
        })
    }

    /**
     * Notify change to stateflow by checking the current state of permissions.
     *
     * 1. special permissions (e.g., Manifest.permission.PACKAGE_USAGE_STATS) that
     * cannot be enabled directly within the app and must be granted through the settings screen.
     * Since it is difficult to register a callback for these permissions, this method will be utilized for that
     * permission state updates are properly propagated when the Activity resumes.
     *
     * 2. run-time permissions provide callback for permission state updates. This method will be utilized for that
     * permission state updates are properly propagated to flow.
     */
    private fun notifyChange() {
        Log.d("PERMISSION", "notifyChange() called")
        permissionStateFlow.value = permissions
            .filter { it !in healthDataPermission.keys }
            .associate { it to getPermissionState(it) }

        val store = HealthDataService.getStore(context)
        val healthDataPermissionSet = healthDataPermission.values.map {
            com.samsung.android.sdk.health.data.permission.Permission.of(it, AccessType.READ)
        }.toSet()

        store.getGrantedPermissionsAsync(healthDataPermissionSet).setCallback(
            Looper.getMainLooper(),
            { res: Set<com.samsung.android.sdk.health.data.permission.Permission>
                -> setHealthDataPermissionState(healthDataPermissionSet, res)},
            {}
        )
    }

    override fun getPermissionFlow(permissions: Array<String>): StateFlow<Map<String, PermissionState>> {
        return permissionStateFlow.map { stateMap ->
            stateMap.filterKeys { it in permissions }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO), // Coroutine Scope 지정
            started = SharingStarted.Lazily, // 필요할 때만 실행
            initialValue = emptyMap() // 초기 값 설정
        )
    }

    private fun getActivity(): ComponentActivity {
        return activityWeakRef?.get()
            ?: throw IllegalStateException("ComponentActivity not attached")
    }

    private fun getPermissionState(permission: String): PermissionState {
        return when (permission) {
            Manifest.permission.PACKAGE_USAGE_STATS -> getPackageUsageStatsPermissionState()
            Manifest.permission.BIND_ACCESSIBILITY_SERVICE -> getBindAccessibilityServicePermissionState()
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> getBindNotificationListenerServicePermissionState()
            else -> getRuntimePermissionState(permission)
        }
    }

    private fun setHealthDataPermissionState(requestedPermission: Set<com.samsung.android.sdk.health.data.permission.Permission>, grantedPermission: Set<com.samsung.android.sdk.health.data.permission.Permission>) {
        val permissionMap = requestedPermission.associate { p ->
            p.dataType.name to if(p in grantedPermission) PermissionState.GRANTED else PermissionState.PERMANENTLY_DENIED
        }

        permissionStateFlow.value = permissionStateFlow.value.toMutableMap().apply {
            putAll(permissionMap)
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

    @SuppressLint("InlinedApi")
    override fun request(permissions: Array<String>) {
        /*Decide which permission to handle first\
        * 1. Special permission other than background location will handle first
        * 2. Background location permission will handle second
        * 3. If there is no special permission, handle normal permissions
        * */
        var permission: String = specialPermissions.keys.find { it in permissions } ?: ""
        var callback: () -> Unit = specialPermissions[permission] ?: {}

        if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions) {
            /* ACCESS_FINE_LOCATION is required before turning on this permission */
            permission =
                if (getPermissionState(Manifest.permission.ACCESS_FINE_LOCATION) != PermissionState.GRANTED) {
                    Manifest.permission.ACCESS_FINE_LOCATION
                } else {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                }
            callback = { requestNormalPermissions(arrayOf(permission)) }
        }

        if (permission != "") {
            CoroutineScope(Dispatchers.IO).launch {
                permissionStateFlow.collect {
                    Log.d("PERMISSION", "permissionStateFlow.collect: $it")
                    if (it[permission] == PermissionState.GRANTED) {
                        request(permissions.filter { it != permission }.toTypedArray())
                        this.cancel()
                    }
                }
            }
            callback()
        } else {
            val normalPermission = permissions.filter { it !in healthDataPermission.keys }
            val healthPermission = permissions.filter { it in healthDataPermission.keys }
            requestNormalPermissions(normalPermission.toTypedArray())
            requestHealthDataPermission(healthPermission.toTypedArray())

        }
    }

    private fun requestNormalPermissions(permissions: Array<String>) {
        permissionLauncher?.launch(permissions)
    }

    private fun requestHealthDataPermission(permissions: Array<String>) {
        val store = HealthDataService.getStore(context)
        val activity = getActivity()

        val possiblePermission = permissions
            .map { com.samsung.android.sdk.health.data.permission.Permission.of(healthDataPermission[it]!!, AccessType.READ) }
            .toSet()

        store.getGrantedPermissionsAsync(possiblePermission).setCallback(
            Looper.getMainLooper(),
            { res: Set<com.samsung.android.sdk.health.data.permission.Permission> ->
                if (!res.containsAll(possiblePermission)) {
                    store.requestPermissionsAsync(possiblePermission, activity).setCallback(
                        Looper.getMainLooper(),
                        { res: Set<com.samsung.android.sdk.health.data.permission.Permission> -> setHealthDataPermissionState(possiblePermission, res) },
                        { error:Throwable ->
                            if(error is ResolvablePlatformException && error.hasResolution){
                                error.resolve(activity)
                            }
                        }
                    )
                }
            },
            { error: Throwable ->
                when(error) {
                    is ResolvablePlatformException -> {
                        if(error.hasResolution) error.resolve(activity)
                    }
                    is AuthorizationException -> {} // Samsung Health Data Dev mode not activated
                    is InvalidRequestException -> {}
                    is PlatformInternalException -> {}
                }
            }
        )
    }

    private fun requestPackageUsageStat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        if (getPermissionState(Manifest.permission.PACKAGE_USAGE_STATS) == PermissionState.GRANTED) return
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        getActivity().startActivity(intent)
    }

    private fun requestBindAccessibilityService() {
        if (getPermissionState(Manifest.permission.BIND_ACCESSIBILITY_SERVICE) == PermissionState.GRANTED) return
        getActivity().startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestBindNotificationListenerService() {
        if (getPermissionState(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) == PermissionState.GRANTED) return
        getActivity().startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}