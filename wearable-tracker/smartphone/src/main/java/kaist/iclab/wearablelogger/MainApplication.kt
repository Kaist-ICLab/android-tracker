package kaist.iclab.wearablelogger

import android.app.Application
import dev.iclab.tracker.Tracker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Tracker.initialize(this@MainApplication)
        startKoin {
            androidContext(this@MainApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
    }
}