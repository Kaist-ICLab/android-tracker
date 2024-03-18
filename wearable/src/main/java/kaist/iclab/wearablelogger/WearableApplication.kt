package kaist.iclab.wearablelogger

import android.app.Application
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WearableApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearableApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
        get<HealthTrackerRepository>().start()
    }
}