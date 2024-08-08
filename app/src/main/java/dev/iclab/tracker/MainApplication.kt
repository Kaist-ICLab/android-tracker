package dev.iclab.tracker

import android.app.Application
import dev.iclab.tracker.collectors.BatteryCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.filters.Filter
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin

class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        TrackerService.initialize(this@MainApplication)
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
        val collectorController = get<CollectorController>()
        collectorController.addCollector(get<TestCollector>())
        collectorController.addCollector(get<BatteryCollector>())
        val filter: Filter = { data ->
            data + ("custom" to "data")
        }
        get<BatteryCollector>().filters.add(filter)
    }


}