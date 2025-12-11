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
import kaist.iclab.mobiletracker.db.dao.DataTrafficDao
import kaist.iclab.mobiletracker.db.dao.DeviceModeDao
import kaist.iclab.mobiletracker.db.dao.ScreenDao
import kaist.iclab.mobiletracker.db.dao.WifiDao
import kaist.iclab.mobiletracker.db.dao.WatchAccelerometerDao
import kaist.iclab.mobiletracker.db.dao.WatchEDADao
import kaist.iclab.mobiletracker.db.dao.WatchHeartRateDao
import kaist.iclab.mobiletracker.db.dao.WatchLocationDao
import kaist.iclab.mobiletracker.db.dao.WatchPPGDao
import kaist.iclab.mobiletracker.db.dao.WatchSkinTemperatureDao
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.AppUsageLogEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.WatchLocationEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity

@Database(
    version = 2,
    entities = [

        // Phone sensor data
        AmbientLightEntity::class,
        AppListChangeEntity::class,
        AppUsageLogEntity::class,
        BatteryEntity::class,
        BluetoothScanEntity::class,
        CallLogEntity::class,
        DataTrafficEntity::class,
        DeviceModeEntity::class,
        ScreenEntity::class,
        WifiEntity::class,

        // Watch sensor data
        WatchAccelerometerEntity::class,
        WatchEDAEntity::class,
        WatchHeartRateEntity::class,
        WatchLocationEntity::class,
        WatchPPGEntity::class,
        WatchSkinTemperatureEntity::class,
    ],
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class TrackerRoomDB: RoomDatabase() {

    // Phone sensor data
    abstract fun ambientLightDao(): AmbientLightDao
    abstract fun appListChangeDao(): AppListChangeDao
    abstract fun appUsageLogDao(): AppUsageLogDao
    abstract fun batteryDao(): BatteryDao
    abstract fun bluetoothScanDao(): BluetoothScanDao
    abstract fun callLogDao(): CallLogDao
    abstract fun dataTrafficDao(): DataTrafficDao
    abstract fun deviceModeDao(): DeviceModeDao
    abstract fun screenDao(): ScreenDao
    abstract fun wifiDao(): WifiDao

    // Watch sensor data
    abstract fun watchAccelerometerDao(): WatchAccelerometerDao
    abstract fun watchEDADao(): WatchEDADao
    abstract fun watchHeartRateDao(): WatchHeartRateDao
    abstract fun watchLocationDao(): WatchLocationDao
    abstract fun watchPPGDao(): WatchPPGDao
    abstract fun watchSkinTemperatureDao(): WatchSkinTemperatureDao
}
