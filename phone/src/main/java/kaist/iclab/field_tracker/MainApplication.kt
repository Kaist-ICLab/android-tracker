package kaist.iclab.field_tracker

import android.app.Application
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.data.core.DataStorage
import kaist.iclab.tracker.notification.ServiceNotification
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Tracker.initialize(
            this@MainApplication,
            ServiceNotification(
                channelId = "TRACKER_SERVICE",
                channelName = "Tracker Service",
                icon = R.drawable.app_icon,
                title = "Tracker Service",
                description = "Tracker service is running now..."
            )
        )
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(realModule)
        }
        init()
    }

    fun init() {
        val collectors: Map<String, Collector> = get(named("collectors"))
        val storages: Map<String, DataStorage> = get(named("storages"))
        collectors.forEach { (name, collector) ->
            collector.addListener { entity ->
                val storage = storages.get(name) ?: error("Storage not found for $name")
                storage.insert(entity)
            }
        }
    }
}