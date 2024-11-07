package kaist.iclab.field_tracker

import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.TrackerUtil
import kaist.iclab.tracker.collectors.AmbientLightCollector
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.controller.CollectorInterface
import kaist.iclab.tracker.notf.NotfManagerInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<NotfManagerInterface> {
        Tracker.getNotfManager()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }

    //    singleOf(::ActivityTransitionCollector)
    singleOf(::AmbientLightCollector)
//    singleOf(::AppUsageLogCollector)
//    singleOf(::BatteryCollector)
//    singleOf(::CallLogCollector)
//    singleOf(::DataTrafficStatCollector)
//    singleOf(::LocationCollector)
//    singleOf(::MessageLogCollector)
//    singleOf(::NotificationCollector)
//    singleOf(::ScreenCollector)
//    singleOf(::UserInteractionCollector)
//    singleOf(::WiFiScanCollector)

    single<Map<String, CollectorInterface>> {
        listOf(
            get<AmbientLightCollector>()
        ).map({ it.NAME to it }).toMap()
    }


    single<TrackerUtil> {
        TrackerUtil(androidContext())
    }

    viewModel<AbstractMainViewModel>{
        MainViewModelImpl(get(), get(), get<Map<String, CollectorInterface>>().keys.toTypedArray())
    }
}
