package kaist.iclab.field_tracker

import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.*
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.database.TempDBImpl
import kaist.iclab.tracker.notf.NotfManagerInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }
    single<NotfManagerInterface> {
        Tracker.getNotfManager()
    }
    single<DatabaseInterface> {
        TempDBImpl(androidContext())
    }


    singleOf(::ActivityTransitionCollector)
    singleOf(::AmbientLightCollector)
    singleOf(::AppUsageLogCollector)
    singleOf(::BatteryCollector)
    singleOf(::CallLogCollector)
    singleOf(::DataTrafficStatCollector)
    singleOf(::LocationCollector)
    singleOf(::MessageLogCollector)
    singleOf(::NotificationCollector)
    singleOf(::ScreenCollector)
    singleOf(::UserInteractionCollector)
    singleOf(::WiFiScanCollector)

    viewModel<AbstractMainViewModel> {
        val collectors = mapOf(
            "ActivityTransitionCollector" to get<ActivityTransitionCollector>(),
            "AmbientLightCollector" to get<AmbientLightCollector>(),
            "AppUsageLogCollector" to get<AppUsageLogCollector>(),
            "BatteryCollector" to get<BatteryCollector>(),
            "CallLogCollector" to get<CallLogCollector>(),
            "DataTrafficStatCollector" to get<DataTrafficStatCollector>(),
            "LocationCollector" to get<LocationCollector>(),
            "MessageLogCollector" to get<MessageLogCollector>(),
            "NotificationCollector" to get<NotificationCollector>(),
            "ScreenCollector" to get<ScreenCollector>(),
            "UserInteractionCollector" to get<UserInteractionCollector>(),
            "WiFiScanCollector" to get<WiFiScanCollector>()
        )
        MainViewModelImpl(
            get(),
            get(),
            get(),
            collectors
        )
    }
}
