package kaist.iclab.wearablelogger.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson

@Database(
    version = 3,
    entities = [
        TestEntity::class,
        PpgEntity::class,
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MyDataRoomDB:RoomDatabase() {
    abstract fun testDao(): TestDao
    abstract fun ppgDao(): PpgDao
}

class Converters {
    @TypeConverter
    fun listToJson(value: List<Int>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value,Array<Int>::class.java).toList()
}