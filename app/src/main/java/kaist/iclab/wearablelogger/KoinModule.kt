package kaist.iclab.wearablelogger

import androidx.room.Room
import androidx.room.Room.databaseBuilder
import kaist.iclab.wearablelogger.collector.ACCCollector
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.collector.HeartRateIBICollector
import kaist.iclab.wearablelogger.collector.PPGGreenCollector
import kaist.iclab.wearablelogger.collector.SkinTempCollector
import kaist.iclab.wearablelogger.db.MyDataRoomDB
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
    single {
        HealthTrackerRepo(androidContext())
    }

    single {
        get<MyDataRoomDB>().testDao()
    }
    single {
        PPGGreenCollector(androidContext(), get(), get<MyDataRoomDB>().ppgDao())
    }
    single {
        ACCCollector(androidContext(), get<MyDataRoomDB>().accDao())
    }
    single {
        HeartRateIBICollector(androidContext(), get(), get<MyDataRoomDB>().hribiDao())
    }
    single {
        SkinTempCollector(androidContext())
    }

    single {
        CollectorRepository(
            listOf<AbstractCollector>(
                get<PPGGreenCollector>(),
//                get<ACCCollector>(),
                get<HeartRateIBICollector>(),
//                get<SkinTempCollector>()
            ),
            androidContext()
        )
    }
}