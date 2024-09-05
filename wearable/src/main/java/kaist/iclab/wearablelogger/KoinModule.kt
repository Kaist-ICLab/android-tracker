package kaist.iclab.wearablelogger

import org.koin.dsl.module

val koinModule = module {
//    single {
//        Room.databaseBuilder(
//            androidContext(),
//            MyDataRoomDB::class.java,
//            "MyDataRoomDB"
//        )
//            .fallbackToDestructiveMigration() // For Dev Phase!
//            .build()
//    }
//
//    single{
//        ConfigRepository(androidContext())
//    }
//
//    single {
//        HealthTrackerRepository(androidContext())
//    }
//
//    single {
//        PpgCollector(androidContext(), get(), get(), get<MyDataRoomDB>().ppgDao())
//    }
//    single {
//        AccCollector(androidContext(), get(), get(), get<MyDataRoomDB>().accDao())
//    }
//    single {
//        HRCollector(androidContext(), get(), get(), get<MyDataRoomDB>().hrDao())
//    }
//    single {
//        SkinTempCollector(androidContext(), get(), get(), get<MyDataRoomDB>().skinTempDao())
//    }
//    single {
//        TestCollector(get<MyDataRoomDB>().testDao())
//    }
//    single {
//        UploaderRepository(
//            androidContext()
//        )
//    }
//    single {
//        CollectorRepository(
//            listOf<CollectorInterface>(
//                get<PpgCollector>(),
//                get<AccCollector>(),
//                get<HRCollector>(),
//                get<SkinTempCollector>()
//            ),
//            get(),
//            androidContext()
//        )
//    }
//
//    viewModel {
//        SettingsViewModel(get(), get())
//    }

}