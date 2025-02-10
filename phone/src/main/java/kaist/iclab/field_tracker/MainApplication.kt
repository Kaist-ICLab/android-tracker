package kaist.iclab.field_tracker

import android.app.Application
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.phone.SampleCollector
import kaist.iclab.tracker.data.core.DataStorageInterface
import kaist.iclab.tracker.data.couchbase.CouchbaseDataStorage
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Tracker.initialize(this@MainApplication)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(realModule)
        }
        initializeConfiguration()
    }

    fun initializeConfiguration() {
        val collectors: Map<String, CollectorInterface> = get(named("collectors"))
        val storages: Map<String, DataStorageInterface> = get(named("storages"))

        collectors.forEach{ (name, collector) ->
            collector.listener = { entity->
                val storage = storages.get(name) ?: error("Storage not found for $name")
                storage.insert(entity)
            }
        }

    }
}