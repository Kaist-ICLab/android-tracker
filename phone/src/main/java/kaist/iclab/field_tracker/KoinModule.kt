package kaist.iclab.field_tracker

import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.phone.AmbientLightCollector
import kaist.iclab.tracker.collectors.phone.AppUsageLogCollector
import kaist.iclab.tracker.collectors.phone.BatteryCollector
import kaist.iclab.tracker.collectors.phone.BluetoothScanCollector
import kaist.iclab.tracker.collectors.phone.CallLogCollector
import kaist.iclab.tracker.collectors.phone.DataTrafficStatCollector
import kaist.iclab.tracker.collectors.phone.LocationCollector
import kaist.iclab.tracker.collectors.phone.MessageLogCollector
import kaist.iclab.tracker.collectors.phone.ScreenCollector
import kaist.iclab.tracker.collectors.phone.UserInteractionCollector
import kaist.iclab.tracker.collectors.phone.WifiScanCollector
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.collectors.core.CollectorInterface
import kaist.iclab.tracker.notification.NotificationManagerInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<NotificationManagerInterface> {
        Tracker.getNotfManager()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }

//    singleOf(::ActivityTransitionCollector)
    singleOf(::AmbientLightCollector)
//    singleOf(::ActivityRecognitionStatCollector)
    singleOf(::AppUsageLogCollector)
    singleOf(::BatteryCollector)
    singleOf(::CallLogCollector)
    singleOf(::DataTrafficStatCollector)
    singleOf(::LocationCollector)
    singleOf(::MessageLogCollector)
//    singleOf(::NotificationCollector)
    singleOf(::ScreenCollector)
    singleOf(::UserInteractionCollector)
    singleOf(::WifiScanCollector)
    singleOf(::BluetoothScanCollector)


    single<Map<String, CollectorInterface>> {
        listOf(
            get<AmbientLightCollector>(),
            get<BatteryCollector>(),
            get<ScreenCollector>(),
//            get<NotificationCollector>(),
            get<UserInteractionCollector>(),
            get<LocationCollector>(),
//            get<ActivityRecognitionStatCollector>(),
            get<AppUsageLogCollector>(),
            get<CallLogCollector>(),
//            get<ActivityTransitionCollector>()
            get<MessageLogCollector>(),
            get<DataTrafficStatCollector>(),
            get<WifiScanCollector>(),
            get<BluetoothScanCollector>()
        ).map({ it.NAME to it }).toMap()
    }

    viewModel<AbstractMainViewModel>{
        MainViewModelImpl(get(), get<Map<String, CollectorInterface>>().keys.toTypedArray())
    }
}