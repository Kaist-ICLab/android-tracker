package kaist.iclab.mobiletracker

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

/**
 * Application class for MobileTracker app.
 * Handles global initialization and setup that should happen when the app starts.
 */
class MobileTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin Dependency Injection
        startKoin {
            androidLogger(level = Level.NONE)
            androidContext(this@MobileTrackerApplication)
            modules(appModule)
        }
        
        // Initialize BLEHelper after Koin is set up
        initializeApp()
    }
    
    private fun initializeApp() {
        // BLEHelper initialization is now handled by Koin
        // Additional initialization can be added here:
        // - Crash reporting
        // - Analytics
        // - Global error handlers
        // - Third-party SDK initialization
    }
}

