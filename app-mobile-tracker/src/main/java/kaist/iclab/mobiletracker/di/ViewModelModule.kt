package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.services.CampaignService
import kaist.iclab.mobiletracker.viewmodels.settings.AccountSettingsViewModel
import kaist.iclab.mobiletracker.viewmodels.settings.SettingsViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.controller.BackgroundController
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
            campaignService = get<CampaignService>()
        )
    }
}

