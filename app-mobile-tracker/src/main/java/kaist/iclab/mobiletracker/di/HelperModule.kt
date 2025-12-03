package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val helperModule = module {
    // BLEHelper - injects SensorDataRepository
    single {
        BLEHelper(
            context = androidContext(),
            sensorDataRepository = get<SensorDataRepository>()
        )
    }
}

