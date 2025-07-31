package kaist.iclab.wearabletracker

import kaist.iclab.tracker.listener.SamsungHealthSensorInitializer
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.tracker.sensor.galaxywatch.HRSensor
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.tracker.sensor.galaxywatch.SkinTempSensor
import kaist.iclab.wearabletracker.state.ControllerStateStorage
import kaist.iclab.wearabletracker.state.SensorConfigStorage
import kaist.iclab.wearabletracker.state.SensorStateStorage
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kaist.iclab.wearabletracker.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val koinModule = module {
    single {
        SamsungHealthSensorInitializer(context = androidContext())
    }

    single {
        AndroidPermissionManager(context = androidContext())
    }

    single {
        ControllerStateStorage()
    }

    // Sensors
    single {
        AccelerometerSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SensorConfigStorage(AccelerometerSensor.Config()),
            stateStorage = SensorStateStorage(),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        PPGSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SensorConfigStorage(PPGSensor.Config()),
            stateStorage = SensorStateStorage(),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        HRSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SensorConfigStorage(HRSensor.Config()),
            stateStorage = SensorStateStorage(),
            samsungHealthSensorInitializer = get()
        )
    }

    single {
        SkinTempSensor(
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SensorConfigStorage(SkinTempSensor.Config()),
            stateStorage = SensorStateStorage(),
            samsungHealthSensorInitializer = get()
        )
    }

    single(named("sensors")) {
        listOf(
            get<AccelerometerSensor>(),
            get<PPGSensor>(),
            get<HRSensor>(),
            get<SkinTempSensor>(),
        )
    }

    // Global Controller
    single {
        BackgroundController(
            context = androidContext(),
            controllerStateStorage = get<ControllerStateStorage>(),
            sensors = get(qualifier("sensors")),
            serviceNotification = BackgroundController.ServiceNotification(
                channelId = "BackgroundControllerService",
                channelName = "WearableTracker",
                notificationId = 1,
                title = "WearableTracker",
                description = "Background sensor controller is running",
                icon = R.drawable.ic_launcher_foreground
            )
        )
    }

    single {
        SensorDataReceiver(
            sensors = get(qualifier("sensors"))
        )
    }

    // ViewModel
    viewModel {
        SettingsViewModel(
            sensorController = get()
        )
    }
}