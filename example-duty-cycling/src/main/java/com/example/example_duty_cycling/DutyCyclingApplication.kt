package com.example.example_duty_cycling

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DutyCyclingApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@DutyCyclingApplication)
            modules(koinModule)
        }
    }
}
