package kaist.iclab.wearabletracker.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.wearabletracker.data.DeviceInfo
import kaist.iclab.wearabletracker.data.PhoneCommunicationManager
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.storage.SensorDataReceiver
import kaist.iclab.wearabletracker.helpers.NotificationHelper
import kaist.iclab.wearabletracker.helpers.SyncPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val syncPreferencesHelper by inject<SyncPreferencesHelper>(clazz = SyncPreferencesHelper::class.java)

    // StateFlow for last sync timestamp
    private val _lastSyncTimestamp = MutableStateFlow<Long?>(null)
    val lastSyncTimestamp: StateFlow<Long?> = _lastSyncTimestamp.asStateFlow()

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
                    "Error getting device information from getDeviceInfo(): ${exception.message}",
                    exception
                )
                // Show notification for this error
                NotificationHelper.showException(
                    context,
                    exception,
                    "Failed to get device information"
                )
            }
    }

    /**
     * Start logging. 
     * Note: Permission check should be done by the caller before invoking this method.
     */
    fun startLogging() {
        sensorController.start()
    }

    fun stopLogging() {
        sensorController.stop()
    }

    fun upload() {
        Log.d(TAG, "UPLOAD")
        phoneCommunicationManager.sendDataToPhone()
        // Refresh last sync timestamp after a delay to allow async sync to complete
        // Note: SharedPreferences operations are synchronous, but we still delay to allow
        // the sync operation to complete first
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000) // Wait 2 seconds for sync to complete
            refreshLastSyncTimestamp()
        }
    }

    /**
     * Load the last sync timestamp from SharedPreferences.
     */
    fun refreshLastSyncTimestamp() {
        try {
            val timestamp = syncPreferencesHelper.getLastSyncTimestamp()
            _lastSyncTimestamp.value = timestamp
        } catch (e: Exception) {
            Log.e(TAG, "Error loading last sync timestamp: ${e.message}", e)
        }
    }

    fun flush(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sensorDataStorages.values.forEach { it.deleteAll() }
                Log.v(TAG, "FLUSH - All sensor data deleted successfully")
                withContext(Dispatchers.Main) {
                    NotificationHelper.showFlushSuccess(context)
                }
            } catch (e: Exception) {
                Log.e(TAG, "FLUSH - Error deleting sensor data: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    NotificationHelper.showFlushFailure(context, e, "Failed to delete sensor data")
                }
            }
        }
    }
}