package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.UserProfileRepository
import kaist.iclab.mobiletracker.services.CampaignService
import kaist.iclab.mobiletracker.services.ProfileService
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.viewmodels.settings.AccountSettingsViewModel
import kaist.iclab.mobiletracker.viewmodels.settings.DataSyncSettingsViewModel
import kaist.iclab.mobiletracker.viewmodels.settings.SettingsViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // SettingsViewModel
    viewModel {
        SettingsViewModel(
            backgroundController = get(),
            permissionManager = get<AndroidPermissionManager>(),
            context = androidContext()
        )
    }
    
    // AccountSettingsViewModel
    viewModel {
        AccountSettingsViewModel(
            campaignService = get<CampaignService>(),
            profileService = get<ProfileService>(),
            supabaseHelper = get<SupabaseHelper>(),
            userProfileRepository = get<UserProfileRepository>(),
            context = androidContext()
        )
    }
    
    // ServerSyncSettingsViewModel
    viewModel {
        DataSyncSettingsViewModel(
            phoneSensorRepository = get<PhoneSensorRepository>(),
            context = androidContext()
        )
    }
}

