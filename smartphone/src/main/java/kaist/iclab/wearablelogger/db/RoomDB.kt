package kaist.iclab.wearablelogger.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    version = 5,
    entities = [
        EventEntity::class,
        RecentEntity::class
    ]
)
abstract class RoomDB : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun recentDao(): RecentDao
}