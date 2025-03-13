package com.example.sensor_test_app.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState

class MainViewModel: ViewModel() {
    var sensors = mutableStateListOf<BaseSensor<SensorConfig, SensorEntity>>()
        private set
    var sensorState = mutableStateListOf<SensorState.FLAG>()
        private set

    var sensorValues = mutableStateListOf<Double>()
        private set

    fun registerSensor(
        newSensor: BaseSensor<SensorConfig, SensorEntity>
    ) {
        newSensor.init()
        sensors.add(newSensor)
        sensorState.add(newSensor.sensorStateFlow.value.flag)
        sensorValues.add(0.0)
    }

    fun setSensorValue(index: Int, value: Double) {
        sensorValues[index] = value
    }

    fun enableSensor(index: Int) {
        sensors[index].enable()
        sensorState[index] = sensors[index].sensorStateFlow.value.flag
    }

    fun startSensor(index: Int) {
        sensors[index].start()
        sensorState[index] = sensors[index].sensorStateFlow.value.flag
    }

    fun stopSensor(index: Int) {
        sensors[index].stop()
        sensorState[index] = sensors[index].sensorStateFlow.value.flag
    }
}