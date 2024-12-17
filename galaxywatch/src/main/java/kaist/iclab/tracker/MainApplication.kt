package kaist.iclab.tracker

import android.app.Application
import android.util.Log
import kaist.iclab.tracker.collectors.core.CollectorInterface
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.notification.NotificationManagerInterface
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Tracker.initialize(this@MainApplication)
        startKoin {
            androidContext(this@MainApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
        initConfiguration()
    }

    fun initConfiguration() {
        val collectorController = get<CollectorControllerInterface>()
        collectorController.initializeCollectors(get())

        val collectorMap = get<Map<String, CollectorInterface>>()
        collectorMap.forEach { (name, collector) ->
            collector.listener = { data ->
                Log.d(collector.NAME, "Data: $data")
            }
        }

        val notfManager = get<NotificationManagerInterface>()
        notfManager.setServiceNotfDescription(
            icon = R.drawable.ic_notf
        )
    }
}