package kaist.iclab.field_tracker

import android.app.Application
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.controller.CollectorControllerInterface
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        Tracker.initialize(this@MainApplication)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
        setupCollector()
    }

    fun setupCollector() {
        val collectorController = get<CollectorControllerInterface>()
        collectorController.collectors.forEach { collector ->
            collector.listener = { data ->
                Log.d(collector.NAME, "Data: $data")
            }
        }
    }
}