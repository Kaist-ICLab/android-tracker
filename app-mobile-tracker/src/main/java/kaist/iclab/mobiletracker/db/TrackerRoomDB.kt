package kaist.iclab.mobiletracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kaist.iclab.mobiletracker.db.dao.AmbientLightDao
import kaist.iclab.mobiletracker.db.dao.AppListChangeDao
import kaist.iclab.mobiletracker.db.dao.AppUsageLogDao
import kaist.iclab.mobiletracker.db.dao.BatteryDao
import kaist.iclab.mobiletracker.db.dao.BluetoothScanDao
import kaist.iclab.mobiletracker.db.dao.CallLogDao
import kaist.iclab.mobiletracker.db.dao.ScreenDao
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.AppUsageLogEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity

@Database(
    version = 1,
    entities = [
        AmbientLightEntity::class,
        AppListChangeEntity::class,
        AppUsageLogEntity::class,
        BatteryEntity::class,
        BluetoothScanEntity::class,
        CallLogEntity::class,
        ScreenEntity::class,
    ],
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class TrackerRoomDB: RoomDatabase() {
    abstract fun ambientLightDao(): AmbientLightDao
    abstract fun appListChangeDao(): AppListChangeDao
    abstract fun appUsageLogDao(): AppUsageLogDao
    abstract fun batteryDao(): BatteryDao
    abstract fun bluetoothScanDao(): BluetoothScanDao
    abstract fun callLogDao(): CallLogDao
    abstract fun screenDao(): ScreenDao
}
