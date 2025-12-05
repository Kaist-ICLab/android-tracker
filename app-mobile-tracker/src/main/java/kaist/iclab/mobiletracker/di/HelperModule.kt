package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import kaist.iclab.mobiletracker.services.CampaignService
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
    
    // CampaignService - injects SupabaseHelper
    single {
        CampaignService(
            supabaseHelper = get()
        )
    }
}

