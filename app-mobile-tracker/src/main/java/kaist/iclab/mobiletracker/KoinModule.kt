package kaist.iclab.mobiletracker

import android.app.Activity
import kaist.iclab.mobiletracker.helpers.AuthPreferencesHelper
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.AuthRepository
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import kaist.iclab.mobiletracker.repository.SensorDataRepositoryImpl
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.GoogleAuth
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    // AuthRepository - bind interface to implementation
    single<AuthRepository> {
        AuthPreferencesHelper(context = androidContext())
    }
    
    // SupabaseHelper - singleton instance shared across all services
    single {
        SupabaseHelper()
    }
    
    // Sensor Services - inject SupabaseHelper
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
    
    // BLEHelper - injects SensorDataRepository
    single {
        BLEHelper(
            context = androidContext(),
            sensorDataRepository = get<SensorDataRepository>()
        )
    }
    
    // GoogleAuth - factory for creating with Activity and server client ID
    // Note: This is a factory because GoogleAuth needs Activity context
    factory { (activity: Activity, serverClientId: String) ->
        GoogleAuth(activity, serverClientId) as Authentication
    }
    
    // AuthViewModel - factory that creates GoogleAuth internally
    // This simplifies the injection by handling GoogleAuth creation inside the ViewModel factory
    viewModel { (activity: Activity, serverClientId: String) ->
        val authentication: Authentication = get(parameters = { parametersOf(activity, serverClientId) })
        AuthViewModel(
            authentication = authentication,
            authRepository = get<AuthRepository>()
        )
    }
}

