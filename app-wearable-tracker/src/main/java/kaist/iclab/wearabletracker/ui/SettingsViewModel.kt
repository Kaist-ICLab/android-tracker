package kaist.iclab.wearabletracker.ui

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.data.DeviceInfo
import kaist.iclab.wearabletracker.data.PhoneCommunicationManager
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SettingsViewModel(
    private val sensorController: BackgroundController
) : ViewModel() {
    companion object {
        private val TAG = SettingsViewModel::class.simpleName
    }

    val sensorDataReceiver by inject<SensorDataReceiver>(clazz = SensorDataReceiver::class.java)
    val sensorDataStorages by inject<Map<String, BaseDao<*>>>(
        clazz = Map::class.java,
        qualifier = named("sensorDataStorages")
    )
    val phoneCommunicationManager by inject<PhoneCommunicationManager>(clazz = PhoneCommunicationManager::class.java)

    init {
        Log.v(SensorDataReceiver::class.simpleName, "init()")

        CoroutineScope(Dispatchers.IO).launch {

            sensorController.controllerStateFlow.collect {
                // This will log the received data from the particular sensor
                Log.v(SensorDataReceiver::class.simpleName, it.toString())
                if (it.flag == ControllerState.FLAG.RUNNING) sensorDataReceiver.startBackgroundCollection()
                else sensorDataReceiver.stopBackgroundCollection()
            }
        }
    }

    val sensorMap = sensorController.sensors.associateBy { it.name }
    val sensorState = sensorController.sensors.associate { it.name to it.sensorStateFlow }
    val controllerState = sensorController.controllerStateFlow

    fun update(sensorName: String, status: Boolean) {
        Log.d(sensorName, status.toString())
        val sensor = sensorMap[sensorName]!!
        if (status) sensor.enable()
        else sensor.disable()
    }

    fun getDeviceInfo(context: Context, callback: (DeviceInfo) -> Unit) {
        Wearable.getNodeClient(context).localNode
            .addOnSuccessListener { localNode ->
                val deviceInfo = DeviceInfo(
                    name = localNode.displayName,
                    id = localNode.id
                )
                callback(deviceInfo)
            }
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Error getting device information from getDeviceInfo(): ${exception.message}"
                )
            }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun startLogging() {
        sensorController.start()
    }

    fun stopLogging() {
        sensorController.stop()
    }

    fun upload() {
        Log.d(TAG, "UPLOAD")
        phoneCommunicationManager.sendDataToPhone()
    }

    fun flush() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sensorDataStorages.values.forEach { it.deleteAll() }
                Log.v(TAG, "FLUSH - All sensor data deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "FLUSH - Error deleting sensor data: ${e.message}")
            }
        }
    }
}