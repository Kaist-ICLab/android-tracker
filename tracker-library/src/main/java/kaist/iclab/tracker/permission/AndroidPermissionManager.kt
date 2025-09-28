package kaist.iclab.tracker.permission

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
import android.annotation.SuppressLint
import android.app.AlarmManager
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

class AndroidPermissionManager(
    private val context: Context
) : PermissionManager {
    companion object {
        private val TAG = AndroidPermissionManager::class.simpleName
    }
    private var activityWeakRef: WeakReference<ComponentActivity>? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    private val permissionStateFlow: MutableStateFlow<Map<String, PermissionState>> = MutableStateFlow(mapOf())

    val specialPermissions = buildMap {
        put(Manifest.permission.PACKAGE_USAGE_STATS, ::requestPackageUsageStat)
        put(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE, ::requestBindNotificationListenerService)
        put(Manifest.permission.BIND_ACCESSIBILITY_SERVICE, ::requestBindAccessibilityService)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) put(Manifest.permission.SCHEDULE_EXACT_ALARM, ::requestScheduleExactAlarm)
    }

    val healthDataPermission = mapOf(
        DataTypes.STEPS.name to DataTypes.STEPS
    )

    override fun registerPermission(newPermissions: Array<String>) {
        permissionStateFlow.value = permissionStateFlow.value.toMutableMap().apply {
            putAll(newPermissions.associateWith { p -> getPermissionState(p) })
        }
    }

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
        Log.d(TAG, "notifyChange() called")
        val permissions = permissionStateFlow.value.keys
        permissionStateFlow.value = permissions
            .filter { it !in healthDataPermission.keys }.associateWith { getPermissionState(it) }

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
        val initialValue = permissionStateFlow.value.filterKeys { it in permissions }

        return permissionStateFlow.map { stateMap ->
            stateMap.filterKeys { it in permissions.toList() }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO), // Coroutine Scope 지정
            started = SharingStarted.Eagerly, // 필요할 때만 실행
            initialValue = initialValue // 초기 값 설정
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
            Manifest.permission.SCHEDULE_EXACT_ALARM -> getScheduleExactAlarmPermissionState()
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
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
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
        val mode =
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        return if (mode == AppOpsManager.MODE_ALLOWED) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    private fun getBindAccessibilityServicePermissionState(): PermissionState {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return PermissionState.NOT_REQUESTED

        val enabledServicesList = TextUtils.split(enabledServices, ":")
        val fullServiceName =
            "${context.packageName}/${AccessibilityListener::class.java.canonicalName}$${AccessibilityListener.AccessibilityServiceAdaptor::class.simpleName}"

        val isServiceRunning = accessibilityManager.getEnabledAccessibilityServiceList(
            FEEDBACK_ALL_MASK
        ).any { it.id == fullServiceName }

        return if (enabledServicesList.contains(fullServiceName) && isServiceRunning) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    private fun getBindNotificationListenerServicePermissionState(): PermissionState {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (notificationManager.isNotificationListenerAccessGranted(
                ComponentName(
                    context,
                    NotificationListener.NotificationListenerServiceAdaptor::class.java
                )
            )
        ) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    private fun getScheduleExactAlarmPermissionState(): PermissionState {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return PermissionState.GRANTED
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if(alarmManager.canScheduleExactAlarms()) PermissionState.GRANTED else PermissionState.NOT_REQUESTED
    }

    @SuppressLint("InlinedApi")
    override fun request(permissions: Array<String>) {
        /*Decide which permission to handle first\
        * 1. Special permission other than background location will handle first
        * 2. Background location permission will handle second
        * 3. If there is no special permission, handle normal permissions
        * */
        var specialPermission: String = specialPermissions.keys.find { it in permissions } ?: ""
        var callback: () -> Unit = specialPermissions[specialPermission] ?: {}

        if (Manifest.permission.ACCESS_BACKGROUND_LOCATION in permissions) {
            /* ACCESS_FINE_LOCATION is required before turning on this permission */
            specialPermission =
                if (getPermissionState(Manifest.permission.ACCESS_FINE_LOCATION) != PermissionState.GRANTED) {
                    Manifest.permission.ACCESS_FINE_LOCATION
                } else {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                }
            callback = { requestNormalPermissions(arrayOf(specialPermission)) }
        } else if (Manifest.permission.BODY_SENSORS_BACKGROUND in permissions) {
            specialPermission =
                if(getPermissionState(Manifest.permission.BODY_SENSORS) != PermissionState.GRANTED) {
                    Manifest.permission.BODY_SENSORS
                } else {
                    Manifest.permission.BODY_SENSORS_BACKGROUND
                }
            callback = { requestNormalPermissions(arrayOf(specialPermission))}
        }

        if (specialPermission != "") {
            CoroutineScope(Dispatchers.IO).launch {
                permissionStateFlow.collect {
                    if (it[specialPermission] == PermissionState.GRANTED) {
                        // Request a normal permission first that is required to grant a special permission
                        request(permissions.filter { it != specialPermission }.toTypedArray())
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

    private fun requestScheduleExactAlarm() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        if (getPermissionState(Manifest.permission.SCHEDULE_EXACT_ALARM) == PermissionState.GRANTED) return
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.fromParts("package", context.packageName, null)
        getActivity().startActivity(intent)
    }
}