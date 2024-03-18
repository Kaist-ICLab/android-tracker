package kaist.iclab.wearablelogger

import androidx.room.Room
import kaist.iclab.wearablelogger.db.RoomDB
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            RoomDB::class.java,
            "RoomDB"
        )
            .fallbackToDestructiveMigration() // For Dev Phase!
            .build()
    }
    single{
        get<RoomDB>().eventDao()
    }
    single{
        get<RoomDB>().recentDao()
    }

    single{
        DataReceiver(get())
    }

    viewModel {
        MainViewModel(get(), get())
    }
}