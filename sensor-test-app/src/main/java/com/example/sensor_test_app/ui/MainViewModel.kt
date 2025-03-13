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
    var sensorRunning = mutableStateListOf<Boolean>()
        private set

    var sensorValues = mutableStateListOf<Double>()
        private set

    fun registerSensor(
        newSensor: BaseSensor<SensorConfig, SensorEntity>
    ) {
        sensors.add(newSensor)
        sensorRunning.add(false)
        sensorValues.add(0.0)
    }

    fun setSensorValue(index: Int, value: Double) {
        sensorValues[index] = value
    }

    fun startSensor(index: Int) {
        sensors[index].start()
        sensorRunning[index] = (sensors[index].sensorStateFlow.value.flag == SensorState.FLAG.RUNNING)
    }

    fun stopSensor(index: Int) {
        sensors[index].stop()
        sensorRunning[index] = (sensors[index].sensorStateFlow.value.flag == SensorState.FLAG.RUNNING)
    }
}