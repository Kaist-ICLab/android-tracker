package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import kaist.iclab.mobiletracker.repository.SensorDataRepositoryImpl
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService
import org.koin.dsl.module

val watchSensorModule = module {
    // Watch Sensor Services - inject SupabaseHelper
    single {
        LocationSensorService(supabaseHelper = get())
    }

    single {
        AccelerometerSensorService(supabaseHelper = get())
    }

    single {
        EDASensorService(supabaseHelper = get())
    }

    single {
        HeartRateSensorService(supabaseHelper = get())
    }

    single {
        PPGSensorService(supabaseHelper = get())
    }

    single {
        SkinTemperatureSensorService(supabaseHelper = get())
    }

    // SensorDataRepository - bind interface to implementation
    single<SensorDataRepository> {
        SensorDataRepositoryImpl(
            locationSensorService = get(),
            accelerometerSensorService = get(),
            edaSensorService = get(),
            heartRateSensorService = get(),
            ppgSensorService = get(),
            skinTemperatureSensorService = get()
        )
    }
}

