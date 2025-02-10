package kaist.iclab.field_tracker

import android.util.Log
import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.phone.SampleCollector
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.data.core.DataStorageInterface
import kaist.iclab.tracker.data.core.SingletonStorageInterface
import kaist.iclab.tracker.data.couchbase.CouchbaseDB
import kaist.iclab.tracker.data.couchbase.CouchbaseDataStorage
import kaist.iclab.tracker.data.couchbase.CouchbaseSingletonStorage
import kaist.iclab.tracker.notification.NotificationManagerInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

//val fakeModule = module {
//    single<PermissionManagerInterface>{
//        PermissionManagerFakeImpl()
//    }
//    singleOf(::SampleCollector)
//
//    viewModel<AbstractMainViewModel>{
//        MainViewModelFakeImpl(mapOf("Sample" to get<SampleCollector>()))
//    }
//}

val realModule = module {
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<NotificationManagerInterface> {
        Tracker.getNotfManager()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }

    singleOf(::CouchbaseDB)
    single<SingletonStorageInterface<SampleCollector.Config>> {
        CouchbaseSingletonStorage(get(),SampleCollector.defaultConfig, SampleCollector.Config::class.java, "SampleCollectorConfig")
    }
    single<SampleCollector> {
        SampleCollector(get(), get(), get(),
            CouchbaseSingletonStorage(get(), AbstractCollector.defaultState, CollectorState::class.java, "SampleCollectorState"))
    }

    single<Map<String, CollectorInterface>>(named("collectors")){
        listOf(
            get<SampleCollector>()
//            get<AmbientLightCollector>(),
//            get<BatteryCollector>(),
//            get<ScreenCollector>(),
//            get<NotificationCollector>(),
//            get<UserInteractionCollector>(),
//            get<LocationCollector>(),
//            get<AppUsageLogCollector>(),
//            get<CallLogCollector>(),
//            get<MessageLogCollector>(),
//            get<DataTrafficStatCollector>(),
//            get<WifiScanCollector>(),
//            get<BluetoothScanCollector>()
        ).map({ it.NAME to it }).toMap()
    }

    single<CouchbaseDataStorage<SampleCollector.Entity>> {
        CouchbaseDataStorage(get(), get<SampleCollector>().NAME, SampleCollector.Entity::class.java)
    }

    single<Map<String, DataStorageInterface>>(named("storages")) {
        listOf(
            get<CouchbaseDataStorage<SampleCollector.Entity>>()
        ).map({ it.NAME to it }).toMap()
    }

    viewModel<AbstractMainViewModel>{
        MainViewModelImpl(get(), get(named("collectors")), get(named("storages")), get())
    }
}


////    singleOf(::ActivityTransitionCollector)
////    singleOf(::ActivityRecognitionStatCollector)
//    singleOf(::AmbientLightCollector)
//    singleOf(::AppUsageLogCollector)
//    singleOf(::BatteryCollector)
//    singleOf(::CallLogCollector)
//    singleOf(::DataTrafficStatCollector)
//    singleOf(::LocationCollector)
//    singleOf(::MessageLogCollector)
//    singleOf(::NotificationCollector)
//    singleOf(::ScreenCollector)
//    singleOf(::UserInteractionCollector)
//    singleOf(::WifiScanCollector)
//    singleOf(::BluetoothScanCollector)
//    single<Map<String, CollectorInterface>> {
//        listOf(
//            get<AmbientLightCollector>(),
//            get<BatteryCollector>(),
//            get<ScreenCollector>(),
//            get<NotificationCollector>(),
//            get<UserInteractionCollector>(),
//            get<LocationCollector>(),
//            get<AppUsageLogCollector>(),
//            get<CallLogCollector>(),
//            get<MessageLogCollector>(),
//            get<DataTrafficStatCollector>(),
//            get<WifiScanCollector>(),
//            get<BluetoothScanCollector>()
//        ).map({ it.NAME to it }).toMap()
//    }
//