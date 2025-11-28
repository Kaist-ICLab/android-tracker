package kaist.iclab.mobiletracker

import android.app.Activity
import androidx.room.Room
import com.google.android.gms.location.Priority
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.dao.BaseDao
import kaist.iclab.mobiletracker.repository.AuthRepositoryImpl
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.repository.PhoneSensorRepositoryImpl
import kaist.iclab.mobiletracker.helpers.BLEHelper
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.AuthRepository
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import kaist.iclab.mobiletracker.repository.SensorDataRepositoryImpl
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService
import kaist.iclab.mobiletracker.storage.CouchbaseSensorStateStorage
import kaist.iclab.mobiletracker.services.PhoneSensorDataService
import kaist.iclab.mobiletracker.storage.SimpleStateStorage
import kaist.iclab.mobiletracker.viewmodels.auth.AuthViewModel
import kaist.iclab.mobiletracker.viewmodels.settings.SettingsViewModel
import kaist.iclab.tracker.MetaData
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.GoogleAuth
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
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {
    // AuthRepository - bind interface to implementation
    single<AuthRepository> {
        AuthRepositoryImpl(context = androidContext())
    }

    // SupabaseHelper - singleton instance shared across all services
    single {
        SupabaseHelper()
    }

    // Sensor Services - inject SupabaseHelper
    single {
        LocationSensorService(supabaseHelper = get())
    }

    single {
        AccelerometerSensorService(supabaseHelper = get())
    }

    single {
        EDASensorService(supabaseHelper = get())
    }

    single {
        HeartRateSensorService(supabaseHelper = get())
    }

    single {
        PPGSensorService(supabaseHelper = get())
    }

    single {
        SkinTemperatureSensorService(supabaseHelper = get())
    }

    // SensorDataRepository - bind interface to implementation
    single<SensorDataRepository> {
        SensorDataRepositoryImpl(
            locationSensorService = get(),
            accelerometerSensorService = get(),
            edaSensorService = get(),
            heartRateSensorService = get(),
            ppgSensorService = get(),
            skinTemperatureSensorService = get()
        )
    }

    // BLEHelper - injects SensorDataRepository
    single {
        BLEHelper(
            context = androidContext(),
            sensorDataRepository = get<SensorDataRepository>()
        )
    }

    // GoogleAuth - factory for creating with Activity and server client ID
    // Note: This is a factory because GoogleAuth needs Activity context
    factory { (activity: Activity, serverClientId: String) ->
        GoogleAuth(activity, serverClientId) as Authentication
    }

    // AuthViewModel - factory that creates GoogleAuth internally
    // This simplifies the injection by handling GoogleAuth creation inside the ViewModel factory
    viewModel { (activity: Activity, serverClientId: String) ->
        val authentication: Authentication =
            get(parameters = { parametersOf(activity, serverClientId) })
        AuthViewModel(
            authentication = authentication,
            authRepository = get<AuthRepository>()
        )
    }

    // PHONE - Sensor Management Dependencies
    single {
        SamsungHealthDataInitializer(context = androidContext())
    }

    single {
        CouchbaseDB(context = androidContext())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            TrackerRoomDB::class.java,
            "phone_tracker_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single {
        AndroidPermissionManager(context = androidContext())
    }

    // PHONE - Sensors
    single {
        AmbientLightSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(
                AmbientLightSensor.Config(
                    interval = 100L
                ),
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
                AppUsageLogSensor.Config(
                    interval = 100L
                )
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
                CallLogSensor.Config(
                    TimeUnit.MINUTES.toMillis(1)
                )
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
                DataTrafficStatSensor.Config(
                    interval = TimeUnit.MINUTES.toMillis(1)
                )
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
            configStorage = SimpleStateStorage(
                DeviceModeSensor.Config(
                )
            ),
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
                MessageLogSensor.Config(
                    interval = TimeUnit.SECONDS.toMillis(10)
                )
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

    // Global Controller
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

    // PhoneSensorDataService for receiving and storing phone sensor data locally
    single {
        PhoneSensorDataService(
            context = androidContext(),
        )
    }

    // Map of sensor IDs to DAOs for storing phone sensor data in Room database
    // This is used internally by PhoneSensorRepository
    single<Map<String, BaseDao<*>>>(named("sensorDataStorages")) {
        val db = get<TrackerRoomDB>()
        mapOf(
            get<AmbientLightSensor>().id to db.ambientLightDao(),
            get<AppListChangeSensor>().id to db.appListChangeDao(),
            get<AppUsageLogSensor>().id to db.appUsageLogDao(),
            get<BatterySensor>().id to db.batteryDao(),
            get<BluetoothScanSensor>().id to db.bluetoothScanDao(),
            get<CallLogSensor>().id to db.callLogDao(),
        )
    }

    // PhoneSensorRepository - bind interface to implementation
    single<PhoneSensorRepository> {
        PhoneSensorRepositoryImpl(
            sensorDataStorages = get<Map<String, BaseDao<*>>>(named("sensorDataStorages"))
        )
    }

    single {
        MetaData(androidContext())
    }

    // SettingsViewModel
    viewModel {
        SettingsViewModel(
            backgroundController = get(),
            permissionManager = get<AndroidPermissionManager>(),
            context = androidContext()
        )
    }
}

