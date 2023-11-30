package kaist.iclab.wearablelogger.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TestEntity::class,
    ],
    exportSchema = false,
)
abstract class MyDataRoomDB:RoomDatabase() {
    abstract fun testDao(): TestDao
}