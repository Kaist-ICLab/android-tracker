package com.example.sensor_test_app.util

import android.content.Context
import kaist.iclab.tracker.permission.PermissionManagerImpl
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.sensor.phone.AmbientLightCollector

val ambientLightCollector = { context: Context ->
    AmbientLightCollector(
        context = context,
        permissionManager = PermissionManagerImpl(context),
        stateStorage = SimpleStateStorage(SensorState(SensorState.FLAG.ENABLED)),
        configStorage = SimpleStateStorage(
            AmbientLightCollector.Config(interval = 1000L)
        ),
    )
}