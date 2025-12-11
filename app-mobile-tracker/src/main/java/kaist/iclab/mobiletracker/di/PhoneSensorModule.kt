package kaist.iclab.mobiletracker.di

import androidx.room.Room
import com.google.android.gms.location.Priority
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.dao.BaseDao
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.repository.PhoneSensorRepositoryImpl
import kaist.iclab.mobiletracker.services.SensorServiceRegistry
import kaist.iclab.mobiletracker.services.SensorServiceRegistryImpl
import kaist.iclab.mobiletracker.services.supabase.AmbientLightSensorService
import kaist.iclab.mobiletracker.services.supabase.BatterySensorService
import kaist.iclab.mobiletracker.services.supabase.BluetoothScanSensorService
import kaist.iclab.mobiletracker.services.supabase.DataTrafficSensorService
import kaist.iclab.mobiletracker.services.supabase.DeviceModeSensorService
import kaist.iclab.mobiletracker.services.upload.PhoneSensorUploadService
import kaist.iclab.mobiletracker.services.supabase.ScreenSensorService
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.supabase.WifiSensorService
import kaist.iclab.mobiletracker.storage.CouchbaseSensorStateStorage
import kaist.iclab.mobiletracker.storage.SimpleStateStorage
import kaist.iclab.tracker.listener.SamsungHealthDataInitializer
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.common.LocationSensor
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.AppListChangeSensor
import kaist.iclab.tracker.sensor.phone.AppUsageLogSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor
import kaist.iclab.tracker.sensor.phone.CallLogSensor
import kaist.iclab.tracker.sensor.phone.DataTrafficStatSensor
import kaist.iclab.tracker.sensor.phone.DeviceModeSensor
import kaist.iclab.tracker.sensor.phone.MediaSensor
import kaist.iclab.tracker.sensor.phone.MessageLogSensor
import kaist.iclab.tracker.sensor.phone.NetworkChangeSensor
import kaist.iclab.tracker.sensor.phone.NotificationSensor
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kaist.iclab.tracker.sensor.phone.StepSensor
import kaist.iclab.tracker.sensor.phone.UserInteractionSensor
import kaist.iclab.tracker.sensor.phone.WifiScanSensor
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB
import kaist.iclab.tracker.storage.couchbase.CouchbaseStateStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val phoneSensorModule = module {
    // Sensor Management Dependencies
    single {
        SamsungHealthDataInitializer(context = androidContext())
    }

    single {
        AndroidPermissionManager(context = androidContext())
    }

    // Phone Sensors
    single {
        AmbientLightSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                AmbientLightSensor.Config(interval = 100L)
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = AmbientLightSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        AppUsageLogSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                AppUsageLogSensor.Config(interval = 100L)
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = AppUsageLogSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        AppListChangeSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                AppListChangeSensor.Config(
                    periodicIntervalMillis = TimeUnit.SECONDS.toMillis(10),
                    includeSystemApps = false,
                    includeDisabledApps = false
                )
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = AppListChangeSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        BatterySensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(BatterySensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = BatterySensor::class.simpleName ?: ""
            )
        )
    }

    single {
        BluetoothScanSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                BluetoothScanSensor.Config(
                    doScan = true,
                    interval = TimeUnit.SECONDS.toMillis(10),
                    scanDuration = TimeUnit.SECONDS.toMillis(1)
                )
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = BluetoothScanSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        CallLogSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                CallLogSensor.Config(TimeUnit.MINUTES.toMillis(1))
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = CallLogSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        DataTrafficStatSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                DataTrafficStatSensor.Config(interval = TimeUnit.MINUTES.toMillis(1))
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = DataTrafficStatSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        DeviceModeSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(DeviceModeSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = DeviceModeSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        LocationSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                LocationSensor.Config(
                    interval = TimeUnit.SECONDS.toMillis(1),
                    maxUpdateAge = 0,
                    maxUpdateDelay = 0,
                    minUpdateDistance = 0.0f,
                    minUpdateInterval = 0,
                    priority = Priority.PRIORITY_HIGH_ACCURACY,
                    waitForAccurateLocation = false,
                )
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = LocationSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        MediaSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(MediaSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = MediaSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        MessageLogSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                MessageLogSensor.Config(interval = TimeUnit.SECONDS.toMillis(10))
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = MessageLogSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        NotificationSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(NotificationSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = NotificationSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        ScreenSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(ScreenSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = ScreenSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        StepSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                StepSensor.Config(
                    syncPastLimitSeconds = TimeUnit.DAYS.toSeconds(7),
                    timeMarginSeconds = TimeUnit.HOURS.toSeconds(1),
                    bucketSizeMinutes = 10,
                    readIntervalMillis = TimeUnit.SECONDS.toMillis(10)
                )
            ),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = StepSensor::class.simpleName ?: ""
            ),
            samsungHealthDataInitializer = get()
        )
    }

    single {
        UserInteractionSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(UserInteractionSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = UserInteractionSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        WifiScanSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(WifiScanSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = WifiScanSensor::class.simpleName ?: ""
            )
        )
    }

    single {
        NetworkChangeSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(NetworkChangeSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = NetworkChangeSensor::class.simpleName ?: ""
            )
        )
    }

    // Sensors list
    single(named("sensors")) {
        listOf(
            get<AmbientLightSensor>(),
            get<AppListChangeSensor>(),
            get<AppUsageLogSensor>(),
            get<BatterySensor>(),
            get<BluetoothScanSensor>(),
            get<CallLogSensor>(),
            get<DataTrafficStatSensor>(),
            get<DeviceModeSensor>(),
            get<LocationSensor>(),
            get<MediaSensor>(),
            get<MessageLogSensor>(),
            get<NetworkChangeSensor>(),
            get<NotificationSensor>(),
            get<ScreenSensor>(),
            get<StepSensor>(),
            get<UserInteractionSensor>(),
            get<WifiScanSensor>(),
        )
    }

    // BackgroundController
    single {
        val context = androidContext()
        BackgroundController.ServiceNotification(
            channelId = "BackgroundControllerService",
            channelName = "MobileTracker",
            notificationId = 1,
            title = context.getString(R.string.notification_title),
            description = context.getString(R.string.notification_description),
            icon = R.drawable.ic_launcher_foreground
        )
    }

    single {
        BackgroundController(
            context = androidContext(),
            controllerStateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = ControllerState(ControllerState.FLAG.DISABLED),
                clazz = ControllerState::class.java,
                collectionName = BackgroundController::class.simpleName ?: ""
            ),
            sensors = get(qualifier("sensors")),
            serviceNotification = get<BackgroundController.ServiceNotification>(),
            allowPartialSensing = true,
        )
    }

    // Map of sensor IDs to DAOs for storing phone sensor data in Room database
    single<Map<String, BaseDao<*, *>>>(named("sensorDataStorages")) {
        val db = get<TrackerRoomDB>()
        mapOf(
            get<AmbientLightSensor>().id to db.ambientLightDao(),
            get<AppListChangeSensor>().id to db.appListChangeDao(),
            get<AppUsageLogSensor>().id to db.appUsageLogDao(),
            get<BatterySensor>().id to db.batteryDao(),
            get<BluetoothScanSensor>().id to db.bluetoothScanDao(),
            get<CallLogSensor>().id to db.callLogDao(),
            get<DataTrafficStatSensor>().id to db.dataTrafficDao(),
            get<DeviceModeSensor>().id to db.deviceModeDao(),
            get<ScreenSensor>().id to db.screenDao(),
            get<WifiScanSensor>().id to db.wifiDao(),
        )
    }

    // PhoneSensorRepository - bind interface to implementation
    single<PhoneSensorRepository> {
        PhoneSensorRepositoryImpl(
            sensorDataStorages = get<Map<String, BaseDao<*, *>>>(named("sensorDataStorages"))
        )
    }

    // AmbientLightSensorService for uploading to Supabase
    single {
        AmbientLightSensorService(supabaseHelper = get())
    }

    // BatterySensorService for uploading to Supabase
    single {
        BatterySensorService(supabaseHelper = get())
    }

    // BluetoothScanSensorService for uploading to Supabase
    single {
        BluetoothScanSensorService(supabaseHelper = get())
    }

    // DataTrafficSensorService for uploading to Supabase
    single {
        DataTrafficSensorService(supabaseHelper = get())
    }

    // DeviceModeSensorService for uploading to Supabase
    single {
        DeviceModeSensorService(supabaseHelper = get())
    }

    // ScreenSensorService for uploading to Supabase
    single {
        ScreenSensorService(supabaseHelper = get())
    }

    // WifiSensorService for uploading to Supabase
    single {
        WifiSensorService(supabaseHelper = get())
    }

    // Phone sensor service registry
    single<SensorServiceRegistry>(named("phoneSensorServiceRegistry")) {
        val ambientLightService = get<AmbientLightSensorService>()
        val batteryService = get<BatterySensorService>()
        val bluetoothScanService = get<BluetoothScanSensorService>()
        val dataTrafficService = get<DataTrafficSensorService>()
        val deviceModeService = get<DeviceModeSensorService>()
        val screenService = get<ScreenSensorService>()
        val wifiService = get<WifiSensorService>()
        
        SensorServiceRegistryImpl(
            mapOf(
                get<AmbientLightSensor>().id to ambientLightService,
                get<BatterySensor>().id to batteryService,
                get<BluetoothScanSensor>().id to bluetoothScanService,
                get<DataTrafficStatSensor>().id to dataTrafficService,
                get<DeviceModeSensor>().id to deviceModeService,
                get<ScreenSensor>().id to screenService,
                get<WifiScanSensor>().id to wifiService,
            )
        )
    }

    // SyncTimestampService for tracking upload timestamps
    single {
        SyncTimestampService(context = androidContext())
    }

    // PhoneSensorUploadService for handling phone sensor data uploads
    single {
        PhoneSensorUploadService(
            db = get<TrackerRoomDB>(),
            serviceRegistry = get<SensorServiceRegistry>(named("phoneSensorServiceRegistry")),
            supabaseHelper = get(),
            syncTimestampService = get()
        )
    }
}

