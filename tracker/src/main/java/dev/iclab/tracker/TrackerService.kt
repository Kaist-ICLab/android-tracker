package dev.iclab.tracker

import android.content.Context
import dev.iclab.tracker.database.CouchbaseDBImpl
import java.lang.ref.WeakReference
import dev.iclab.tracker.database.DatabaseInterface

// This class is a singleton that provides access to the database and collector controller.
// It should be initialized in the MainApplication class.
// It use volatile and synchronized to ensure that the database and collector controller are initialized only once.
// WeakReference is used to avoid memory leaks.
object TrackerService {
    @Volatile
    private var database: WeakReference<DatabaseInterface>? = null
    @Volatile
    private var collectorControllerRef: WeakReference<CollectorController>? = null

    @Synchronized
    fun initialize(context: Context) {
        if (database == null && collectorControllerRef?.get() == null) {
//            database = FakeDBImpl()
            database = WeakReference(CouchbaseDBImpl(context))
            collectorControllerRef = WeakReference(CollectorController(context.applicationContext))
            /* Add notification channel to show collector is running as a foreground service... */
            CollectorService.createNotificationChannel(context)
        }
    }

    fun getDatabase(): DatabaseInterface {
        return database?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }

    fun getCollectorController(): CollectorController {
        return collectorControllerRef?.get() ?: throw IllegalStateException("TrackerService not initialized")
    }
}
