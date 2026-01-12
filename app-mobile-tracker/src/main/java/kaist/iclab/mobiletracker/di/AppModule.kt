package kaist.iclab.mobiletracker.di

import kaist.iclab.tracker.MetaData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Main app module that combines all feature modules.
 * This is the entry point for Koin dependency injection.
 */
val appModule = module {
    // MetaData
    single {
        MetaData(androidContext())
    }
}

