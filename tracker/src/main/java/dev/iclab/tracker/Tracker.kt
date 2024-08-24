package dev.iclab.tracker

import android.content.Context
import dev.iclab.tracker.collectors.controller.CollectorController
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
import dev.iclab.tracker.collectors.controller.CollectorService
import dev.iclab.tracker.database.CouchbaseDBImpl
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.permission.PermissionManagerImpl
import dev.iclab.tracker.permission.PermissionManagerInterface
import java.lang.ref.WeakReference

// This class is a singleton that provides access to the database and collector controller instead of using Injection Library.
// It should be initialized in the MainApplication class.
// It use volatile and synchronized to ensure that the database and collector controller are initialized only once.
// WeakReference is used to avoid memory leaks when the context is passed to the database and collector controller.
object Tracker {
    @Volatile
    private var database: WeakReference<DatabaseInterface>? = null
    @Volatile
    private var collectorController: WeakReference<CollectorControllerInterface>? = null

    @Volatile
    private var permissionManager: WeakReference<PermissionManagerInterface>? = null

    @Synchronized
    fun initialize(context: Context, database_: DatabaseInterface, permissionManager_: PermissionManagerInterface) {
        if (database == null && collectorController?.get() == null) {
            database = WeakReference(database_)
            permissionManager = WeakReference(permissionManager_)
            collectorController = WeakReference(CollectorController(context.applicationContext))

            /* Add notification channel to show collector is running as a foreground service... */
            CollectorService.createNotificationChannel(context)
        }
    }

    @Synchronized
    fun initialize(context: Context){
        if (database == null && collectorController?.get() == null) {
            database = WeakReference(CouchbaseDBImpl(context))
            permissionManager = WeakReference(PermissionManagerImpl(context))
            collectorController = WeakReference(CollectorController(context.applicationContext))

            /* Add notification channel to show collector is running as a foreground service... */
            CollectorService.createNotificationChannel(context)
        }
    }

    fun getDatabase(): DatabaseInterface {
        return database?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getCollectorController(): CollectorControllerInterface {
        return collectorController?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getPermissionManager(): PermissionManagerInterface {
        return permissionManager?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }
}
