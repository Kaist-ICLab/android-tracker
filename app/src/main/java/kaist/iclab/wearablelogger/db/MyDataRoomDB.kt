package kaist.iclab.wearablelogger.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson

@Database(
    version = 14,
    entities = [
        TestEntity::class,
        PpgEntity::class,
        AccEntity::class,
        HRIBIEntity::class,
        SkinTempEntity::class,
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MyDataRoomDB:RoomDatabase() {
    abstract fun testDao(): TestDao
    abstract fun ppgDao(): PpgDao
    abstract fun accDao(): AccDao
    abstract fun hribiDao(): HRIBIDao
    abstract fun skintempDao(): SkinTempDao
}

class Converters {
    @TypeConverter
    fun listToJson(value: List<Int>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value,Array<Int>::class.java).toList()
}