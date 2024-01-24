package kaist.iclab.wearablelogger

import androidx.room.Room
import kaist.iclab.wearablelogger.collector.ACC.AccCollector
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.collector.HR.HRCollector
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgCollector
import kaist.iclab.wearablelogger.collector.Test.TestCollector
import kaist.iclab.wearablelogger.collector.SkinTemp.SkinTempCollector
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module{
    single {
        Room.databaseBuilder(
            androidContext(),
            MyDataRoomDB::class.java,
            "MyDataRoomDB"
        )
            .fallbackToDestructiveMigration() // For Dev Phase!
            .build()
    }

    single { ToggleStates() }
    single {
        HealthTrackerRepo(androidContext())
    }

    single {
        PpgCollector(get(), get<MyDataRoomDB>().ppgDao(), get<ToggleStates>())
    }
    single {
        AccCollector(get(), get<MyDataRoomDB>().accDao(), get<ToggleStates>())
    }
    single {
        HRCollector(get(), get<MyDataRoomDB>().hribiDao(), get<ToggleStates>())
    }
    single {
        SkinTempCollector(get(), get<MyDataRoomDB>().skintempDao(), get<ToggleStates>())
    }

    single {
        TestCollector(get(), get<MyDataRoomDB>().testDao())
    }
    single {
        CollectorRepository(
            listOf<AbstractCollector>(
                get<PpgCollector>(),
                get<AccCollector>(),
                get<HRCollector>(),
                get<SkinTempCollector>()
            ),
            androidContext()
        )
    }
}