package kaist.iclab.field_tracker

import android.app.Application
import kaist.iclab.tracker.Tracker
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
            modules(realModule)
        }
    }
}