package kaist.iclab.mobiletracker

import android.app.Application
import android.content.res.Configuration
import kaist.iclab.mobiletracker.helpers.LanguageHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import java.util.Locale

/**
 * Application class for MobileTracker app.
 * Handles global initialization and setup that should happen when the app starts.
 */
class MobileTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Apply saved language preference before any UI is created
        applyLanguagePreference()
        
        // Initialize Koin Dependency Injection
        startKoin {
            androidLogger(level = Level.NONE)
            androidContext(this@MobileTrackerApplication)
            modules(appModule)
        }
        
        // Additional initialization
        initializeApp()
    }
    
    /**
     * Apply saved language preference to the application context
     */
    private fun applyLanguagePreference() {
        val languageHelper = LanguageHelper(this)
        val language = languageHelper.getLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    
    private fun initializeApp() {
        // Additional initialization can be added here:
        // - Crash reporting
        // - Analytics
        // - Global error handlers
        // - Third-party SDK initialization
    }
}

