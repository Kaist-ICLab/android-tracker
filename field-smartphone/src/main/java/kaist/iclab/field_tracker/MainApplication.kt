package kaist.iclab.field_tracker

import android.app.Application
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.BatteryCollector
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
        get<BatteryCollector>().listener = { data ->
            Log.d("BatteryCollector", "Battery data: $data")
            // Do something with battery data
        }
//        collectorController.addCollector(get<TestCollector>())
//        collectorController.addCollector(get<BatteryCollector>())
//        val locationCollector = get<LocationCollector>()
//        locationCollector.listener = { data ->
//            Log.d("LocationCollector", "Location data: $data")
//        }
//        collectorController.addCollector(locationCollector)
//        collectorController.enable(locationCollector.NAME, get())
        //        val filter: Filter = { data ->
//            data + ("custom" to "data")
//        }
//        get<BatteryCollector>().filters.add(filter)
    }
}