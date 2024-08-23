package dev.iclab.tracker

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()

//        TrackerService.initialize(this@MainApplication)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
//        val collectorController = get<CollectorController>()
//        collectorController.addCollector(get<TestCollector>())
//        collectorController.addCollector(get<BatteryCollector>())
//        val filter: Filter = { data ->
//            data + ("custom" to "data")
//        }
//        get<BatteryCollector>().filters.add(filter)
    }


}