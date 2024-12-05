package kaist.iclab.tracker

import android.content.Context
import kaist.iclab.tracker.controller.CollectorControllerImpl
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.notf.NotfManagerImpl
import kaist.iclab.tracker.notf.NotfManagerInterface
//import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.permission.PermissionManagerImpl2
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

    @Volatile
    private var notfManager: NotfManagerInterface? = null

    @Synchronized
    fun initialize(context: Context,
                   permissionManager_: PermissionManagerInterface,
                   notfManager_: NotfManagerInterface,
                   collectorController_: CollectorControllerInterface){
        if (permissionManager?.get() == null) {
            permissionManager = WeakReference(permissionManager_)
        }
        if (collectorController?.get() == null) {
            collectorController = WeakReference(collectorController_)
        }
        if(notfManager== null){
            notfManager= notfManager_
            notfManager?.createServiceNotfChannel(context)
        }

    }

    @Synchronized
    fun initialize(context: Context){
        val notfManager_ = NotfManagerImpl()
        notfManager_.createServiceNotfChannel(context)
        initialize(context,PermissionManagerImpl2(context),notfManager_, CollectorControllerImpl(context))

    }

    fun getCollectorController(): CollectorControllerInterface {
        return collectorController?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getPermissionManager(): PermissionManagerInterface {
        return permissionManager?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getNotfManager(): NotfManagerInterface {
        return notfManager ?: throw IllegalStateException("TrackerService not initialized")
    }
}
