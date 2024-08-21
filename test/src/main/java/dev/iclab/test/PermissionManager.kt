import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class PermissionManager(
    caller: ActivityResultCaller,
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val shouldShowPermissionRationale: (permission: String) -> Boolean
) {
    private var onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null

    constructor(activity: ComponentActivity) : this(
        caller = activity,
        context = activity,
        fragmentManager = (activity as AppCompatActivity).supportFragmentManager,
        shouldShowPermissionRationale = { activity.shouldShowRequestPermissionRationale(it) }
    )

    constructor(fragment: Fragment) : this(
        caller = fragment,
        context = fragment.requireContext(),
        fragmentManager = fragment.parentFragmentManager,
        shouldShowPermissionRationale = { fragment.shouldShowRequestPermissionRationale(it) }
    )

    private val requestPermissionLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    private val requestMultiplePermissionsLauncher =
        caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val isGranted = result.values.all { it == true }
            if (!isGranted) {
                // As above: Handle the case where permissions are denied.
            }
            onPermissionsGranted?.invoke(isGranted)
        }

    fun checkPermissions(
        vararg permissions: String,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        this.onPermissionsGranted = onPermissionsGranted

        val permissionsToBeRequested = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }
        val shouldShowRequestPermissionRationale = permissionsToBeRequested.any {
            shouldShowPermissionRationale.invoke(it)
        }

        when {
            permissionsToBeRequested.isEmpty() -> onPermissionsGranted?.invoke(true)
            shouldShowRequestPermissionRationale -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                onPermissionsGranted?.invoke(false)
            }
            else -> requestPermissions(permissionsToBeRequested)
        }
    }

    fun checkMediaPermissions(
        vararg permissions: MediaPermission,
        onPermissionsGranted: ((isGranted: Boolean) -> Unit)? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(
                *permissions.map { it.getGranularMediaPermission() }.toTypedArray(),
                onPermissionsGranted = onPermissionsGranted
            )
        } else {
            checkPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                onPermissionsGranted = onPermissionsGranted
            )
        }
    }

    private fun requestPermissions(permissionsToBeRequested: List<String>) {
        if (permissionsToBeRequested.size > 1) {
            requestMultiplePermissionsLauncher.launch(permissionsToBeRequested.toTypedArray())
        } else {
            permissionsToBeRequested.firstOrNull()?.let { requestPermissionLauncher.launch(it) }
        }
    }

    enum class MediaPermission {
        IMAGES {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_IMAGES
        },
        VIDEO {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_VIDEO
        },
        AUDIO {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun getGranularMediaPermission() = Manifest.permission.READ_MEDIA_AUDIO
        };

        /**
         * Gets the corresponding granular media permission for Android Tiramisu (API level 33) and above.
         */
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        abstract fun getGranularMediaPermission(): String
    }

}