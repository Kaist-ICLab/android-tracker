package kaist.iclab.wearabletracker

import android.app.Application
import kaist.iclab.wearabletracker.sync.WearaBLEDataChannel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject

class WearableApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearableApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
        inject<WearaBLEDataChannel>(clazz = WearaBLEDataChannel::class.java)
    }
}