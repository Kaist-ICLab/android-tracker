package kaist.iclab.wearabletracker

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class WearableApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearableApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
    }
}