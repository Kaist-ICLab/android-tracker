package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.dao.phone.*
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
    private val bluetoothScanDao: BluetoothScanDao
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
            bluetoothScanDao.getDailyBluetoothCount(startOfDay)
        ) { args: Array<Int> ->
            DailySensorCounts(
                locationCount = args[0],
                appUsageCount = args[1],
                activityCount = args[2],
                batteryCount = args[3],
                notificationCount = args[4],
                screenCount = args[5],
                connectivityCount = args[6],
                bluetoothCount = args[7]
            )
        }
    }
}
