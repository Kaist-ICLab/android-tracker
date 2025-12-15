package kaist.iclab.mobiletracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kaist.iclab.mobiletracker.db.dao.phone.AmbientLightDao
import kaist.iclab.mobiletracker.db.dao.phone.AppListChangeDao
import kaist.iclab.mobiletracker.db.dao.phone.AppUsageLogDao
import kaist.iclab.mobiletracker.db.dao.phone.BatteryDao
import kaist.iclab.mobiletracker.db.dao.phone.BluetoothScanDao
import kaist.iclab.mobiletracker.db.dao.phone.CallLogDao
import kaist.iclab.mobiletracker.db.dao.phone.DataTrafficDao
import kaist.iclab.mobiletracker.db.dao.phone.DeviceModeDao
import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.dao.phone.ScreenDao
import kaist.iclab.mobiletracker.db.dao.phone.WifiScanDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchAccelerometerDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchEDADao
import kaist.iclab.mobiletracker.db.dao.watch.WatchHeartRateDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchPPGDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchSkinTemperatureDao
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.AppUsageLogEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.LocationEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiScanEntity
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity

@Database(
    version = 1,
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
        LocationEntity::class,
        ScreenEntity::class,
        WifiScanEntity::class,

        // Watch sensor data
        WatchAccelerometerEntity::class,
        WatchEDAEntity::class,
        WatchHeartRateEntity::class,
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
    abstract fun locationDao(): LocationDao
    abstract fun screenDao(): ScreenDao
    abstract fun wifiDao(): WifiScanDao

    // Watch sensor data
    abstract fun watchAccelerometerDao(): WatchAccelerometerDao
    abstract fun watchEDADao(): WatchEDADao
    abstract fun watchHeartRateDao(): WatchHeartRateDao
    abstract fun watchPPGDao(): WatchPPGDao
    abstract fun watchSkinTemperatureDao(): WatchSkinTemperatureDao
}
