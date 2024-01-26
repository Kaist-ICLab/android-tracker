package kaist.iclab.wearablelogger

import androidx.room.Room
import kaist.iclab.wearablelogger.collector.ACC.AccCollector
import kaist.iclab.wearablelogger.collector.CollectorInterface
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.collector.HR.HRCollector
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgCollector
import kaist.iclab.wearablelogger.collector.SkinTemp.SkinTempCollector
import kaist.iclab.wearablelogger.collector.Test.TestCollector
import kaist.iclab.wearablelogger.config.ConfigRepository
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepository
import kaist.iclab.wearablelogger.ui.SettingsViewModel
import kaist.iclab.wearablelogger.uploader.UploaderRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MyDataRoomDB::class.java,
            "MyDataRoomDB"
        )
            .fallbackToDestructiveMigration() // For Dev Phase!
            .build()
    }

    single{
        ConfigRepository(androidContext())
    }

    single {
        HealthTrackerRepository(androidContext())
    }

    single {
        PpgCollector(androidContext(), get(), get(), get<MyDataRoomDB>().ppgDao())
    }
    single {
        AccCollector(androidContext(), get(), get(), get<MyDataRoomDB>().accDao())
    }
    single {
        HRCollector(androidContext(), get(), get(), get<MyDataRoomDB>().hrDao())
    }
    single {
        SkinTempCollector(androidContext(), get(), get(), get<MyDataRoomDB>().skinTempDao())
    }
    single {
        TestCollector(get<MyDataRoomDB>().testDao())
    }
    single {
        UploaderRepository(
            androidContext()
        )
    }
    single {
        CollectorRepository(
            listOf<CollectorInterface>(
                get<PpgCollector>(),
                get<AccCollector>(),
                get<HRCollector>(),
                get<SkinTempCollector>()
            ),
            get(),
            androidContext()
        )
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

}