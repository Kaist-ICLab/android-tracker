package kaist.iclab.tracker

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.controller.CollectorControllerImpl
import kaist.iclab.tracker.controller.CollectorController
import kaist.iclab.tracker.data.core.StateStorage
import kaist.iclab.tracker.notification.NotfManagerImpl
import kaist.iclab.tracker.notification.NotfManager
import kaist.iclab.tracker.notification.ServiceNotification
//import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.permission.PermissionManager
import java.lang.ref.WeakReference

// This class is a singleton that provides access to the database and collector controller instead of using Injection Library.
// It should be initialized in the MainApplication class.
// It use volatile and synchronized to ensure that the database and collector controller are initialized only once.
// WeakReference is used to avoid memory leaks when the context is passed to the database and collector controller.
object Tracker {
    @Volatile
    private var collectorController: WeakReference<CollectorController>? = null

    @Volatile
    private var permissionManager: WeakReference<PermissionManager>? = null

    @Volatile
    private var notfManager: NotfManager? = null

    @Synchronized
    fun initialize(
        permissionManager_: PermissionManager,
        notfManager_: NotfManager,
        collectorController_: CollectorController
    ) {
        if (permissionManager?.get() == null) {
            permissionManager = WeakReference(permissionManager_)
        }
        if (collectorController?.get() == null) {
            collectorController = WeakReference(collectorController_)
        }
        if (notfManager == null) {
            notfManager = notfManager_
            notfManager?.init()
        }

    }

    @Synchronized
    fun initialize(context: Context, serviceNotf: ServiceNotification) {
        Log.d("Tracker", "Initializing Tracker")
        val notfManager_ = NotfManagerImpl(context, serviceNotf)
        notfManager_.init()
        initialize(
            PermissionManagerImpl(context),
            notfManager_,
            CollectorControllerImpl(context)
        )
    }

    fun getCollectorController(): CollectorController {
        return collectorController?.get()
            ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getPermissionManager(): PermissionManager {
        return permissionManager?.get()
            ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getNotfManager(): NotfManager {
        return notfManager ?: throw IllegalStateException("TrackerService not initialized")
    }
}
