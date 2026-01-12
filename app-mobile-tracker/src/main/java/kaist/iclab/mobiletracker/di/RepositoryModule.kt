package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.repository.HomeRepository
import kaist.iclab.mobiletracker.repository.HomeRepositoryImpl
import org.koin.dsl.module

/**
 * Koin module for Repository layer bindings.
 * Separates data layer concerns from ViewModels and Sensor configuration.
 */
val repositoryModule = module {
    // HomeRepository for Home screen dashboard
    single<HomeRepository> {
        val db = get<TrackerRoomDB>()
        HomeRepositoryImpl(
            // Phone sensor DAOs
            locationDao = db.locationDao(),
            appUsageLogDao = db.appUsageLogDao(),
            stepDao = db.stepDao(),
            batteryDao = db.batteryDao(),
            notificationDao = db.notificationDao(),
            screenDao = db.screenDao(),
            connectivityDao = db.connectivityDao(),
            bluetoothScanDao = db.bluetoothScanDao(),
            ambientLightDao = db.ambientLightDao(),
            appListChangeDao = db.appListChangeDao(),
            callLogDao = db.callLogDao(),
            dataTrafficDao = db.dataTrafficDao(),
            deviceModeDao = db.deviceModeDao(),
            mediaDao = db.mediaDao(),
            messageLogDao = db.messageLogDao(),
            userInteractionDao = db.userInteractionDao(),
            wifiScanDao = db.wifiDao(),
            // Watch sensor DAOs
            watchHeartRateDao = db.watchHeartRateDao(),
            watchAccelerometerDao = db.watchAccelerometerDao(),
            watchEDADao = db.watchEDADao(),
            watchPPGDao = db.watchPPGDao(),
            watchSkinTemperatureDao = db.watchSkinTemperatureDao(),
            watchSensorRepository = get()
        )
    }

    // Future repositories can be added here:
    // single<SomeOtherRepository> { SomeOtherRepositoryImpl(...) }
}
