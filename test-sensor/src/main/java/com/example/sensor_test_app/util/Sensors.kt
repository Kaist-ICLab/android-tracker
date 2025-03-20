package com.example.sensor_test_app.util

import android.content.Context
import android.util.Log
import com.example.sensor_test_app.ui.MainViewModel
import com.google.android.gms.location.Priority
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.AppUsageLogSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor
import kaist.iclab.tracker.sensor.phone.CallLogSensor
import kaist.iclab.tracker.sensor.phone.DataTrafficStatSensor
import kaist.iclab.tracker.sensor.phone.LocationSensor
import kaist.iclab.tracker.sensor.phone.MessageLogSensor
import kaist.iclab.tracker.sensor.phone.NotificationSensor
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kaist.iclab.tracker.sensor.phone.UserInteractionSensor
import kaist.iclab.tracker.sensor.phone.WifiScanSensor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Maybe move the sensor definitions to the other file?
fun addBunchOfSensors(
    context: Context,
    permissionManager: PermissionManager,
    mainViewModel: MainViewModel
) {
    // AmbientLight
    val ambientLight = AmbientLightSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            AmbientLightSensor.Config(
                interval = 100L
            )
        ),
    )
    ambientLight.addListener {
        mainViewModel.setSensorValue(0, millisToDateString(it.timestamp))
        Log.v("test_ambient", "${it.value.toDouble()}")
    }
    mainViewModel.registerSensor(ambientLight as BaseSensor<SensorConfig, SensorEntity>)

    // AppUsageLog
    val appUsageLog = AppUsageLogSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            AppUsageLogSensor.Config(
                interval = 100L,
            )
        ),
    )
    appUsageLog.addListener {
        mainViewModel.setSensorValue(1, millisToDateString(it.timestamp))
        Log.v("test_appUsageLog", "${it.timestamp} ${it.packageName}")
    }
    mainViewModel.registerSensor(appUsageLog as BaseSensor<SensorConfig, SensorEntity>)

    // Battery
    val battery = BatterySensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            BatterySensor.Config()
        ),
    )
    battery.addListener {
        mainViewModel.setSensorValue(2, millisToDateString(it.timestamp))
        Log.v("test_battery", "${it.timestamp} ${it.level}%")
    }
    mainViewModel.registerSensor(battery as BaseSensor<SensorConfig, SensorEntity>)

    // bluetooth
    val bluetooth = BluetoothScanSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            BluetoothScanSensor.Config(
                true,
                TimeUnit.SECONDS.toMillis(10),
                TimeUnit.SECONDS.toMillis(1)
            )
        ),
    )
    bluetooth.addListener {
        mainViewModel.setSensorValue(3, millisToDateString(it.timestamp))
        Log.v("test_bluetooth", "${it.timestamp} ${it.name} ${it.bondState} ${it.connectionType}")
    }
    mainViewModel.registerSensor(bluetooth as BaseSensor<SensorConfig, SensorEntity>)

    //callLog
    val callLog = CallLogSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            CallLogSensor.Config(
                TimeUnit.MINUTES.toMillis(1)
            )
        ),
    )
    callLog.addListener {
        mainViewModel.setSensorValue(4, millisToDateString(it.timestamp))
        Log.v("test_callLog", "${it.timestamp} ${it.number} ${it.duration} ${it.type}")
    }
    mainViewModel.registerSensor(callLog as BaseSensor<SensorConfig, SensorEntity>)

    // dataTrafficStat
    val dataTraffic = DataTrafficStatSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            DataTrafficStatSensor.Config(
                TimeUnit.MINUTES.toMillis(1)
            )
        ),
    )
    dataTraffic.addListener {
        mainViewModel.setSensorValue(5, millisToDateString(it.timestamp))
        Log.v(
            "test_dataTrafficStat",
            "${it.timestamp} ${it.totalRx} ${it.totalTx} ${it.mobileRx} ${it.mobileTx}"
        )
    }
    mainViewModel.registerSensor(dataTraffic as BaseSensor<SensorConfig, SensorEntity>)

    // location
    val location = LocationSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            LocationSensor.Config(
                TimeUnit.SECONDS.toMillis(1),
                0,
                0,
                0.0f,
                0,
                Priority.PRIORITY_HIGH_ACCURACY
            )
        ),
    )
    location.addListener {
        mainViewModel.setSensorValue(6, millisToDateString(it.timestamp))
        Log.v(
            "test_location",
            "${it.timestamp} ${it.speed} ${it.altitude} ${it.latitude} ${it.longitude}"
        )
    }
    mainViewModel.registerSensor(location as BaseSensor<SensorConfig, SensorEntity>)

    // MessageLog
    val message = MessageLogSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            MessageLogSensor.Config(
                TimeUnit.SECONDS.toMillis(10)
            )
        ),
    )
    message.addListener {
        mainViewModel.setSensorValue(7, millisToDateString(it.timestamp))
        Log.v(
            "test_message",
            "${millisToDateString(it.timestamp)} ${it.number} ${it.messageType}"
        )
    }
    mainViewModel.registerSensor(message as BaseSensor<SensorConfig, SensorEntity>)

    // Notification
    val notification = NotificationSensor(
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            NotificationSensor.Config()
        ),
    )
    notification.addListener {
        mainViewModel.setSensorValue(8, millisToDateString(it.timestamp))
        Log.v(
            "test_notification",
            "${it.timestamp} ${it.eventType} ${it.title} ${it.text}"
        )
    }
    mainViewModel.registerSensor(notification as BaseSensor<SensorConfig, SensorEntity>)

    // Screen
    val screen = ScreenSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            ScreenSensor.Config()
        ),
    )
    screen.addListener {
        mainViewModel.setSensorValue(9, millisToDateString(it.timestamp))
        Log.v(
            "test_screen",
            "${it.timestamp} ${it.type}"
        )
    }
    mainViewModel.registerSensor(screen as BaseSensor<SensorConfig, SensorEntity>)

    // User Interaction
    val interaction = UserInteractionSensor(
        permissionManager = permissionManager,
        configStorage = SimpleStateStorage(
            UserInteractionSensor.Config()
        ),
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE))
    )
    interaction.addListener {
        mainViewModel.setSensorValue(10, millisToDateString(it.timestamp))
        Log.v(
            "test_interaction",
            "${it.timestamp} ${it.eventType} ${it.packageName} ${it.text} ${it.className}"
        )
    }
    mainViewModel.registerSensor(interaction as BaseSensor<SensorConfig, SensorEntity>)

    // Wifi
    val wifi = WifiScanSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            WifiScanSensor.Config()
        )
    )
    wifi.addListener {
        mainViewModel.setSensorValue(11, millisToDateString(it.timestamp))
        Log.v(
            "test_interaction",
            "${it.timestamp} ${it.level} ${it.ssid} ${it.bssid} ${it.frequency}"
        )
    }
    mainViewModel.registerSensor(wifi as BaseSensor<SensorConfig, SensorEntity>)
}

private fun millisToDateString(time: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(time))
}