package com.example.sensor_test_app.util

import android.content.Context
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.AppUsageLogSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor
import java.util.concurrent.TimeUnit

val ambientLight = { context: Context, permissionManager: PermissionManager ->
    AmbientLightSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            AmbientLightSensor.Config(
                interval = 100L
            )
        ),
    )
}

val appUsageLog = { context: Context, permissionManager: PermissionManager ->
    AppUsageLogSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            AppUsageLogSensor.Config(
                interval = 100L,
            )
        ),
    )
}

val battery = { context: Context, permissionManager: PermissionManager ->
    BatterySensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            BatterySensor.Config()
        ),
    )
}

val bluetooth = { context: Context, permissionManager: PermissionManager ->
    BluetoothScanSensor(
        context = context,
        permissionManager = permissionManager,
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.UNAVAILABLE)),
        configStorage = SimpleStateStorage(
            BluetoothScanSensor.Config(
                true,
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1)
            )
        ),
    )
}