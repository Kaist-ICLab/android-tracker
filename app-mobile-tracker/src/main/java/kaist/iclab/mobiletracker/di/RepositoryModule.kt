package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.repository.DataRepository
import kaist.iclab.mobiletracker.repository.DataRepositoryImpl
import kaist.iclab.mobiletracker.repository.HomeRepository
import kaist.iclab.mobiletracker.repository.HomeRepositoryImpl
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.upload.PhoneSensorUploadService
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService
import kaist.iclab.tracker.sensor.core.Sensor
import org.koin.core.qualifier.named
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

    // DataRepository for Data screen sensor list
    single<DataRepository> {
        val db = get<TrackerRoomDB>()
        DataRepositoryImpl(
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
            // Services for upload and sync
            syncTimestampService = get<SyncTimestampService>(),
            phoneSensorUploadService = get<PhoneSensorUploadService>(),
            watchSensorUploadService = get<WatchSensorUploadService>(),
            sensors = get<List<Sensor<*, *>>>(named("phoneSensors"))
        )
    }
}
