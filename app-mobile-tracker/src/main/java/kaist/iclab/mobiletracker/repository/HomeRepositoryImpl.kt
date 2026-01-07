package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.dao.phone.AmbientLightDao
import kaist.iclab.mobiletracker.db.dao.phone.AppListChangeDao
import kaist.iclab.mobiletracker.db.dao.phone.AppUsageLogDao
import kaist.iclab.mobiletracker.db.dao.phone.BatteryDao
import kaist.iclab.mobiletracker.db.dao.phone.BluetoothScanDao
import kaist.iclab.mobiletracker.db.dao.phone.CallLogDao
import kaist.iclab.mobiletracker.db.dao.phone.ConnectivityDao
import kaist.iclab.mobiletracker.db.dao.phone.DataTrafficDao
import kaist.iclab.mobiletracker.db.dao.phone.DeviceModeDao
import kaist.iclab.mobiletracker.db.dao.phone.MediaDao
import kaist.iclab.mobiletracker.db.dao.phone.MessageLogDao
import kaist.iclab.mobiletracker.db.dao.phone.NotificationDao
import kaist.iclab.mobiletracker.db.dao.phone.ScreenDao
import kaist.iclab.mobiletracker.db.dao.phone.StepDao
import kaist.iclab.mobiletracker.db.dao.phone.UserInteractionDao
import kaist.iclab.mobiletracker.db.dao.phone.WifiScanDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Implementation of HomeRepository that aggregates sensor count data from multiple DAOs.
 */
class HomeRepositoryImpl(
    private val locationDao: LocationDao,
    private val appUsageLogDao: AppUsageLogDao,
    private val stepDao: StepDao,
    private val batteryDao: BatteryDao,
    private val notificationDao: NotificationDao,
    private val screenDao: ScreenDao,
    private val connectivityDao: ConnectivityDao,
    private val bluetoothScanDao: BluetoothScanDao,
    private val ambientLightDao: AmbientLightDao,
    private val appListChangeDao: AppListChangeDao,
    private val callLogDao: CallLogDao,
    private val dataTrafficDao: DataTrafficDao,
    private val deviceModeDao: DeviceModeDao,
    private val mediaDao: MediaDao,
    private val messageLogDao: MessageLogDao,
    private val userInteractionDao: UserInteractionDao,
    private val wifiScanDao: WifiScanDao,
    private val watchSensorRepository: WatchSensorRepository
) : HomeRepository {

    override fun getDailySensorCounts(startOfDay: Long): Flow<DailySensorCounts> {
        return combine(
            locationDao.getDailyLocationCount(startOfDay),
            appUsageLogDao.getDailyAppUsageCount(startOfDay),
            stepDao.getDailyStepCount(startOfDay),
            batteryDao.getDailyBatteryCount(startOfDay),
            notificationDao.getDailyNotificationCount(startOfDay),
            screenDao.getDailyScreenCount(startOfDay),
            connectivityDao.getDailyConnectivityCount(startOfDay),
            bluetoothScanDao.getDailyBluetoothCount(startOfDay),
            ambientLightDao.getDailyAmbientLightCount(startOfDay),
            appListChangeDao.getDailyAppListChangeCount(startOfDay),
            callLogDao.getDailyCallLogCount(startOfDay),
            dataTrafficDao.getDailyDataTrafficCount(startOfDay),
            deviceModeDao.getDailyDeviceModeCount(startOfDay),
            mediaDao.getDailyMediaCount(startOfDay),
            messageLogDao.getDailyMessageLogCount(startOfDay),
            userInteractionDao.getDailyUserInteractionCount(startOfDay),
            wifiScanDao.getDailyWifiScanCount(startOfDay)
        ) { args: Array<Int> ->
            DailySensorCounts(
                locationCount = args[0],
                appUsageCount = args[1],
                activityCount = args[2],
                batteryCount = args[3],
                notificationCount = args[4],
                screenCount = args[5],
                connectivityCount = args[6],
                bluetoothCount = args[7],
                ambientLightCount = args[8],
                appListChangeCount = args[9],
                callLogCount = args[10],
                dataTrafficCount = args[11],
                deviceModeCount = args[12],
                mediaCount = args[13],
                messageLogCount = args[14],
                userInteractionCount = args[15],
                wifiScanCount = args[16]
            )
        }
    }

    override fun getWatchConnectionStatus(): Flow<WatchConnectionStatus> {
        return watchSensorRepository.getWatchConnectionStatus()
    }
}
