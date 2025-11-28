package kaist.iclab.mobiletracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kaist.iclab.mobiletracker.db.dao.AmbientLightDao
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity

@Database(
    version = 1,
    entities = [
        AmbientLightEntity::class,
    ],
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class TrackerRoomDB: RoomDatabase() {
    abstract fun ambientLightDao(): AmbientLightDao
}
