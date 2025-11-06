package kaist.iclab.wearabletracker

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import kaist.iclab.wearabletracker.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import java.lang.Thread.UncaughtExceptionHandler

class WearableApplication: Application() {
    companion object {
        private const val TAG = "WearableApplication"
    }

    private var defaultExceptionHandler: UncaughtExceptionHandler? = null

    override fun onCreate() {
        super.onCreate()
        
        // Set up global exception handler
        setupGlobalExceptionHandler()
        
        startKoin {
            androidContext(this@WearableApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }
    }

    private fun setupGlobalExceptionHandler() {
        // Save the default exception handler
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        // Set our custom exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            try {
                // Log the exception
                Log.e(TAG, "Uncaught exception in thread ${thread.name}", exception)

                // Show notification on the main thread
                Handler(Looper.getMainLooper()).post {
                    try {
                        NotificationHelper.showException(
                            context = this@WearableApplication,
                            exception = exception,
                            contextInfo = "Uncaught exception in ${thread.name}"
                        )
                    } catch (e: Exception) {
                        // If showing notification fails, at least log it
                        Log.e(TAG, "Failed to show error notification", e)
                    }
                }

                // Call the default exception handler to maintain default behavior
                defaultExceptionHandler?.uncaughtException(thread, exception)
            } catch (e: Exception) {
                // If our handler fails, fall back to default
                Log.e(TAG, "Error in custom exception handler", e)
                defaultExceptionHandler?.uncaughtException(thread, exception)
            }
        }
    }
}