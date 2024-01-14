package kaist.iclab.wearablelogger

import android.app.Application
import kaist.iclab.wearablelogger.collector.Test.TestDao
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WearableLoggerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearableLoggerApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
        get<HealthTrackerRepo>().start()
    }
}