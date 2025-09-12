package com.example.survey_test_app.ui

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.example.survey_test_app.SurveyDataReceiver
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.PermissionState
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.couchbase.CouchbaseSurveyScheduleStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SurveyViewModel(
    private val backgroundController: BackgroundController,
    private val permissionManager: AndroidPermissionManager,
    private val surveyDataReceiver: SurveyDataReceiver,
    private val surveyScheduleStorage: CouchbaseSurveyScheduleStorage
): ViewModel() {
    private val sensors = backgroundController.sensors

    val sensorMap = sensors.associateBy { it.name }
    val sensorState = sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = backgroundController.controllerStateFlow

    init {
        Log.v(SurveyDataReceiver::class.simpleName, "init()")

        CoroutineScope(Dispatchers.IO).launch {
            backgroundController.controllerStateFlow.collect {
                Log.v(SurveyDataReceiver::class.simpleName, it.toString())
                if(it.flag == ControllerState.FLAG.RUNNING) surveyDataReceiver.startBackgroundCollection()
                else surveyDataReceiver.stopBackgroundCollection()
            }
        }
    }

    fun toggleSensor(sensorName: String) {
        val status = sensorState[sensorName]!!.value.flag
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!

        when(status) {
            SensorState.FLAG.DISABLED -> {
                permissionManager.request(sensor.permissions)
                CoroutineScope(Dispatchers.IO).launch {
                    permissionManager.getPermissionFlow(sensor.permissions).collect { permissionMap ->
                        Log.d("SensorViewModel", "$permissionMap")
                        if(permissionMap.values.all { it == PermissionState.GRANTED }) {
                            sensor.enable()
                            this.cancel()
                        }
                    }
                }
            }

            SensorState.FLAG.ENABLED -> sensor.disable()
            else -> Unit
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging(){
        Log.d(SurveyViewModel::class.simpleName, "StartLogging()")
        backgroundController.start()
    }

    fun stopLogging() {
        backgroundController.stop()
    }

    fun resetSchedule() {
        surveyScheduleStorage.resetSchedule()
    }
}