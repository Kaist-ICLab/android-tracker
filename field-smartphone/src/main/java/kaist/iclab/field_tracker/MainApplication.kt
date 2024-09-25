package kaist.iclab.field_tracker

import android.app.Application
import kaist.iclab.tracker.collectors.BatteryCollector
import kaist.iclab.tracker.collectors.LocationCollector
import kaist.iclab.tracker.collectors.TestCollector
import kaist.iclab.tracker.collectors.controller.CollectorControllerInterface
import kaist.iclab.tracker.Tracker
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