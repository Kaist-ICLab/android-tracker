package dev.iclab.tracker

import android.app.Application
import dev.iclab.tracker.collectors.BatteryCollector
import dev.iclab.tracker.collectors.LocationCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
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
        collectorController.addCollector(get<TestCollector>())
        collectorController.addCollector(get<BatteryCollector>())
        collectorController.addCollector(get<LocationCollector>())
        //        val filter: Filter = { data ->
//            data + ("custom" to "data")
//        }
//        get<BatteryCollector>().filters.add(filter)
    }
}