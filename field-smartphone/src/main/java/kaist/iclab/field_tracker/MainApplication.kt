package kaist.iclab.field_tracker

import android.app.Application
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.CollectorInterface
import kaist.iclab.tracker.notf.NotfManagerInterface
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
//        Tracker.initialize(this@MainApplication, get(), get(), get())
//        initConfiguration()
    }

//    fun initConfiguration() {
//        val collectorMap = get<Map<String, CollectorInterface>>()
//        collectorMap.forEach { (name, collector) ->
//            collector.listener = { data ->
//                Log.d(collector.NAME, "Data: $data")
//            }
//        }
//
//        val notfManager = get<NotfManagerInterface>()
//        notfManager.setServiceNotfDescription(
//            icon = R.drawable.ic_notf
//        )
//    }
}