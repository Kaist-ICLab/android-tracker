package kaist.iclab.tracker

import android.content.Context
import kaist.iclab.tracker.controller.CollectorControllerImpl
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.permission.PermissionManagerInterface
import java.lang.ref.WeakReference

// This class is a singleton that provides access to the database and collector controller instead of using Injection Library.
// It should be initialized in the MainApplication class.
// It use volatile and synchronized to ensure that the database and collector controller are initialized only once.
// WeakReference is used to avoid memory leaks when the context is passed to the database and collector controller.
object Tracker {
    @Volatile
    private var collectorController: WeakReference<CollectorControllerInterface>? = null
    @Volatile
    private var permissionManager: WeakReference<PermissionManagerInterface>? = null

    @Synchronized
    fun initialize(context: Context, permissionManager_: PermissionManagerInterface) {
        if (collectorController?.get() == null) {
            permissionManager = WeakReference(permissionManager_)
            collectorController = WeakReference(CollectorControllerImpl(context.applicationContext))

            /* Add notification channel to show collector is running as a foreground service... */
            CollectorControllerImpl.NotificationHandler.createNotificationChannel(context)
        }
    }

    @Synchronized
    fun initialize(context: Context){
        initialize(context, PermissionManagerImpl(context))
    }

    fun getCollectorController(): CollectorControllerInterface {
        return collectorController?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getPermissionManager(): PermissionManagerInterface {
        return permissionManager?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }
}
