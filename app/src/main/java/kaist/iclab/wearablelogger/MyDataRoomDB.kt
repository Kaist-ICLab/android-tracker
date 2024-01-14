package kaist.iclab.wearablelogger

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import kaist.iclab.wearablelogger.collector.ACC.AccDao
import kaist.iclab.wearablelogger.collector.ACC.AccEntity
import kaist.iclab.wearablelogger.collector.HR.HRDao
import kaist.iclab.wearablelogger.collector.HR.HREntity
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgDao
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgEntity
import kaist.iclab.wearablelogger.collector.SkinTemp.SkinTempDao
import kaist.iclab.wearablelogger.collector.SkinTemp.SkinTempEntity
import kaist.iclab.wearablelogger.collector.Test.TestDao
import kaist.iclab.wearablelogger.collector.Test.TestEntity

@Database(
    version = 15,
    entities = [
        TestEntity::class,
        PpgEntity::class,
        AccEntity::class,
        HREntity::class,
        SkinTempEntity::class,
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MyDataRoomDB:RoomDatabase() {
    abstract fun testDao(): TestDao
    abstract fun ppgDao(): PpgDao
    abstract fun accDao(): AccDao
    abstract fun hribiDao(): HRDao
    abstract fun skintempDao(): SkinTempDao
}

class Converters {
    @TypeConverter
    fun listToJson(value: List<Int>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value,Array<Int>::class.java).toList()
}