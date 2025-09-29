package kaist.iclab.wearabletracker

import androidx.room.Room
import com.google.android.gms.location.Priority
import kaist.iclab.tracker.listener.SamsungHealthSensorInitializer
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.common.LocationSensor
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.tracker.sensor.galaxywatch.EDASensor
import kaist.iclab.tracker.sensor.galaxywatch.HeartRateSensor
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.tracker.sensor.galaxywatch.SkinTemperatureSensor
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB
import kaist.iclab.tracker.storage.couchbase.CouchbaseStateStorage
import kaist.iclab.wearabletracker.db.TrackerRoomDB
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kaist.iclab.wearabletracker.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val koinModule = module {
    single {
        SamsungHealthSensorInitializer(context = androidContext())
    }

    single {
        CouchbaseDB(context = androidContext())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            TrackerRoomDB::class.java,
            "tracker_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single {
        AndroidPermissionManager(context = androidContext())
    }

    // Sensors
    single {
        AccelerometerSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = AccelerometerSensor.Config(),
                clazz = AccelerometerSensor.Config::class.java,
                collectionName = (AccelerometerSensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = AccelerometerSensor::class.simpleName ?: ""
            ),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        PPGSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = PPGSensor.Config(),
                clazz = PPGSensor.Config::class.java,
                collectionName = (PPGSensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = PPGSensor::class.simpleName ?: ""
            ),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        HeartRateSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = HeartRateSensor.Config(),
                clazz = HeartRateSensor.Config::class.java,
                collectionName = (HeartRateSensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = HeartRateSensor::class.simpleName ?: ""
            ),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        SkinTemperatureSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SkinTemperatureSensor.Config(),
                clazz = SkinTemperatureSensor.Config::class.java,
                collectionName = (SkinTemperatureSensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = SkinTemperatureSensor::class.simpleName ?: ""
            ),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        EDASensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = EDASensor.Config(),
                clazz = EDASensor.Config::class.java,
                collectionName = (EDASensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = EDASensor::class.simpleName ?: ""
            ),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        LocationSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = LocationSensor.Config(
                    interval = TimeUnit.SECONDS.toMillis(1),
                    maxUpdateAge = 0,
                    maxUpdateDelay = 0,
                    minUpdateDistance = 0.0f,
                    minUpdateInterval = 0,
                    priority = Priority.PRIORITY_HIGH_ACCURACY,
                    waitForAccurateLocation = true,
                ),
                clazz = LocationSensor.Config::class.java,
                collectionName = (LocationSensor::class.simpleName ?: "") + "config"
            ),
            stateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
                clazz = SensorState::class.java,
                collectionName = LocationSensor::class.simpleName ?: ""
            )
        )
    }

    single(named("sensors")) {
        listOf(
            get<AccelerometerSensor>(),
            get<PPGSensor>(),
            get<HeartRateSensor>(),
            get<SkinTemperatureSensor>(),
            get<EDASensor>(),
            get<LocationSensor>()
        )
    }

//    single<Map<String, CouchbaseSensorDataStorage>>(named("sensorDataStorages")) {
//        get<List<Sensor<*, *>>>(named("sensors")).associate { sensor -> sensor.id to
//            CouchbaseSensorDataStorage(
//                couchbase = get(),
//                collectionName = sensor.id + "storage"
//            )
//        }
//    }

    single<Map<String, BaseDao<*>>>(named("sensorDataStorages")) {
        mapOf(
            get<AccelerometerSensor>().id to get<TrackerRoomDB>().accelerometerDao(),
            get<PPGSensor>().id to get<TrackerRoomDB>().ppgDao(),
            get<HeartRateSensor>().id to get<TrackerRoomDB>().heatRateDao(),
            get<SkinTemperatureSensor>().id to get<TrackerRoomDB>().skinTemperatureDao(),
            get<EDASensor>().id to get<TrackerRoomDB>().edaDao(),
            get<LocationSensor>().id to get<TrackerRoomDB>().locationDao()
        )
    }

    // Global Controller
    single {
        BackgroundController.ServiceNotification(
            channelId = "BackgroundControllerService",
            channelName = "WearableTracker",
            notificationId = 1,
            title = "WearableTracker",
            description = "Background sensor controller is running",
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
            allowPartialSensing = true
        )
    }

    single {
        SensorDataReceiver(
            context = androidContext(),
        )
    }

    // ViewModel
    viewModel {
        SettingsViewModel(
            sensorController = get()
        )
    }
}