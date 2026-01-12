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
import kaist.iclab.mobiletracker.db.dao.watch.WatchAccelerometerDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchEDADao
import kaist.iclab.mobiletracker.db.dao.watch.WatchHeartRateDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchPPGDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchSkinTemperatureDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Implementation of HomeRepository that aggregates sensor count data from multiple DAOs.
 */
class HomeRepositoryImpl(
    // Phone sensor DAOs
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
    // Watch sensor DAOs
    private val watchHeartRateDao: WatchHeartRateDao,
    private val watchAccelerometerDao: WatchAccelerometerDao,
    private val watchEDADao: WatchEDADao,
    private val watchPPGDao: WatchPPGDao,
    private val watchSkinTemperatureDao: WatchSkinTemperatureDao,
    private val watchSensorRepository: WatchSensorRepository
) : HomeRepository {

    override fun getDailySensorCounts(startOfDay: Long): Flow<DailySensorCounts> {
        // Combine phone sensor flows
        val phoneFlow = combine(
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
        ) { args: Array<Int> -> args.toList() }

        // Combine watch sensor flows
        val watchFlow = combine(
            watchHeartRateDao.getDailyHeartRateCount(startOfDay),
            watchAccelerometerDao.getDailyAccelerometerCount(startOfDay),
            watchEDADao.getDailyEDACount(startOfDay),
            watchPPGDao.getDailyPPGCount(startOfDay),
            watchSkinTemperatureDao.getDailySkinTemperatureCount(startOfDay)
        ) { heartRate, accelerometer, eda, ppg, skinTemp ->
            listOf(heartRate, accelerometer, eda, ppg, skinTemp)
        }

        // Combine both flows into final result
        return combine(phoneFlow, watchFlow) { phone, watch ->
            DailySensorCounts(
                // Phone sensors
                locationCount = phone[0],
                appUsageCount = phone[1],
                activityCount = phone[2],
                batteryCount = phone[3],
                notificationCount = phone[4],
                screenCount = phone[5],
                connectivityCount = phone[6],
                bluetoothCount = phone[7],
                ambientLightCount = phone[8],
                appListChangeCount = phone[9],
                callLogCount = phone[10],
                dataTrafficCount = phone[11],
                deviceModeCount = phone[12],
                mediaCount = phone[13],
                messageLogCount = phone[14],
                userInteractionCount = phone[15],
                wifiScanCount = phone[16],
                // Watch sensors
                watchHeartRateCount = watch[0],
                watchAccelerometerCount = watch[1],
                watchEDACount = watch[2],
                watchPPGCount = watch[3],
                watchSkinTemperatureCount = watch[4]
            )
        }
    }

    override fun getWatchConnectionInfo(): Flow<WatchConnectionInfo> {
        return watchSensorRepository.getWatchConnectionInfo()
    }
}
