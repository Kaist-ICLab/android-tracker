package kaist.iclab.field_tracker

import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.phone.SampleCollector
import kaist.iclab.tracker.controller.CollectorController
import kaist.iclab.tracker.data.core.DataStorage
import kaist.iclab.tracker.data.core.StateStorage
import kaist.iclab.tracker.data.couchbase.CouchbaseDB
import kaist.iclab.tracker.data.couchbase.CouchbaseDataStorage
import kaist.iclab.tracker.data.couchbase.CouchbaseStateStorage
import kaist.iclab.tracker.notification.NotfManager
import kaist.iclab.tracker.permission.PermissionManager
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
    singleOf(::CouchbaseDB)
    single<StateStorage<TrackerState>>(named("TrackerState")) {
        CouchbaseStateStorage(
            get(),
            TrackerState(TrackerState.FLAG.DISABLED), TrackerState::class.java, "TrackerState"
        )

    }
    single<CollectorController> {
        Tracker.getCollectorController()
    }
    single<NotfManager> {
        Tracker.getNotfManager()
    }
    single<PermissionManager> {
        Tracker.getPermissionManager()
    }

    single<DataStorage>(named("SampleCollectorStorage")) {
        CouchbaseDataStorage(get(), "Sample")
    }
    single<SampleCollector> {
        val defaultConfig = SampleCollector.Config(5000L)
        SampleCollector(
            get<PermissionManager>(),
            CouchbaseStateStorage(
                get(), defaultConfig,
                SampleCollector.Config::class.java, "SampleCollectorConfig"
            ),
            CouchbaseStateStorage(
                get(),
                CollectorState(CollectorState.FLAG.UNAVAILABLE),
                CollectorState::class.java, "SampleCollectorState"
            ),
            defaultConfig
        )
    }

    single<Map<String, Collector>>(named("collectors")) {
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

    single<Map<String, DataStorage>>(named("storages")) {
        listOf(
            get<DataStorage>(named("SampleCollectorStorage"))
        ).map({ it.NAME to it }).toMap()
    }

    viewModel<AbstractMainViewModel> {
        MainViewModelImpl(get(), get(named("collectors")), get(named("storages")), get(named("TrackerState")), get())
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