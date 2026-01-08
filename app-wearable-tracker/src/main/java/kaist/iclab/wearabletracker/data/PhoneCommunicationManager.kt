package kaist.iclab.wearabletracker.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.wearabletracker.Constants
import kaist.iclab.wearabletracker.R
import kaist.iclab.wearabletracker.db.dao.AccelerometerDao
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.db.dao.EDADao
import kaist.iclab.wearabletracker.db.dao.HeartRateDao
import kaist.iclab.wearabletracker.db.dao.LocationDao
import kaist.iclab.wearabletracker.db.dao.PPGDao
import kaist.iclab.wearabletracker.db.dao.SkinTemperatureDao
import kaist.iclab.wearabletracker.helpers.NotificationHelper
import kaist.iclab.wearabletracker.helpers.SyncPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PhoneCommunicationManager(
    private val androidContext: Context,
    private val daos: Map<String, BaseDao<*>>,
    private val syncPreferencesHelper: SyncPreferencesHelper,
) {
    private val TAG = javaClass.simpleName
    private val bleChannel: BLEDataChannel = BLEDataChannel(androidContext)
    private val nodeClient: NodeClient by lazy { Wearable.getNodeClient(androidContext) }

    /**
     * Check if phone node is available and reachable
     */
    private suspend fun isPhoneAvailable(): Boolean = try {
        val connectedNodes = suspendCancellableCoroutine<List<Node>> { continuation ->
            nodeClient.connectedNodes
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
        connectedNodes.isNotEmpty()
    } catch (e: Exception) {
        Log.e(TAG, "Error checking phone availability: ${e.message}", e)
        false
    }

    /**
     * Send all collected sensor data to the phone app via BLE
     */
    fun sendDataToPhone() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!isPhoneAvailable()) {
                    Log.e(
                        TAG,
                        "Error sending data to phone: Phone is not available or not connected"
                    )
                    withContext(Dispatchers.Main) {
                        NotificationHelper.showPhoneCommunicationFailure(
                            androidContext,
                            androidContext.getString(R.string.notification_phone_not_available)
                        )
                    }
                    return@launch
                }

                val csvData = generateCSVData()
                if (csvData.isEmpty()) {
                    Log.w(TAG, "No data to send")
                    withContext(Dispatchers.Main) {
                        NotificationHelper.showPhoneCommunicationFailure(androidContext, androidContext.getString(R.string.notification_no_data))
                    }
                    return@launch
                }

                try {
                    bleChannel.send(Constants.BLE.KEY_SENSOR_DATA, csvData)
                    Log.d(TAG, "Sensor data sent to phone via BLE")
                    
                    // Save successful sync timestamp
                    val currentTime = System.currentTimeMillis()
                    syncPreferencesHelper.saveLastSyncTimestamp(currentTime)
                    Log.d(TAG, "Last sync timestamp saved: $currentTime")
                    
                    withContext(Dispatchers.Main) {
                        NotificationHelper.showPhoneCommunicationSuccess(androidContext)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending data to phone: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        NotificationHelper.showPhoneCommunicationFailure(
                            androidContext,
                            e,
                            androidContext.getString(R.string.notification_send_failed)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sendDataToPhone: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    NotificationHelper.showPhoneCommunicationFailure(
                        androidContext,
                        e,
                        "Error in sendDataToPhone"
                    )
                }
            }
        }
    }

    /**
     * Generate CSV formatted data from all sensor DAOs
     */
    private suspend fun generateCSVData(): String {
        val csvBuilder = StringBuilder()

        daos.forEach { (sensorId, dao) ->
            when (sensorId) {
                Constants.SensorType.ACCELEROMETER -> appendAccelerometerData(csvBuilder, dao as? AccelerometerDao)
                Constants.SensorType.PPG -> appendPPGData(csvBuilder, dao as? PPGDao)
                Constants.SensorType.HEART_RATE -> appendHeartRateData(csvBuilder, dao as? HeartRateDao)
                Constants.SensorType.SKIN_TEMPERATURE -> appendSkinTemperatureData(
                    csvBuilder,
                    dao as? SkinTemperatureDao
                )
                Constants.SensorType.EDA -> appendEDAData(csvBuilder, dao as? EDADao)
                Constants.SensorType.LOCATION -> appendLocationData(csvBuilder, dao as? LocationDao)
            }
            csvBuilder.append("\n")
        }

        return csvBuilder.toString()
    }

    private suspend fun appendAccelerometerData(csvBuilder: StringBuilder, dao: AccelerometerDao?) {
        dao ?: return
        csvBuilder.append("accelerometer\n")
            .append("id,received,timestamp,x,y,z\n")
        dao.getAllAccelerometerData().forEach { entry ->
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},${entry.x},${entry.y},${entry.z}\n")
        }
    }

    private suspend fun appendPPGData(csvBuilder: StringBuilder, dao: PPGDao?) {
        dao ?: return
        csvBuilder.append("ppg\n")
            .append("id,received,timestamp,green,greenStatus,red,redStatus,ir,irStatus\n")
        dao.getAllPPGData().forEach { entry ->
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},")
                .append("${entry.green},${entry.greenStatus},${entry.red},${entry.redStatus},${entry.ir},${entry.irStatus}\n")
        }
    }

    private suspend fun appendHeartRateData(csvBuilder: StringBuilder, dao: HeartRateDao?) {
        dao ?: return
        csvBuilder.append("heartRate\n")
            .append("id,received,timestamp,hr,hrStatus,ibi,ibiStatus\n")
        dao.getAllHeartRateData().forEach { entry ->
            val ibiString = entry.ibi.joinToString(";")
            val ibiStatusString = entry.ibiStatus.joinToString(";")
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},")
                .append("${entry.hr},${entry.hrStatus},$ibiString,$ibiStatusString\n")
        }
    }

    private suspend fun appendSkinTemperatureData(
        csvBuilder: StringBuilder,
        dao: SkinTemperatureDao?
    ) {
        dao ?: return
        csvBuilder.append("skinTemperature\n")
            .append("id,received,timestamp,ambientTemp,objectTemp,status\n")
        dao.getAllSkinTemperatureData().forEach { entry ->
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},")
                .append("${entry.ambientTemperature},${entry.objectTemperature},${entry.status}\n")
        }
    }

    private suspend fun appendEDAData(csvBuilder: StringBuilder, dao: EDADao?) {
        dao ?: return
        csvBuilder.append("eda\n")
            .append("id,received,timestamp,skinConductance,status\n")
        dao.getAllEDAData().forEach { entry ->
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},")
                .append("${entry.skinConductance},${entry.status}\n")
        }
    }

    private suspend fun appendLocationData(csvBuilder: StringBuilder, dao: LocationDao?) {
        dao ?: return
        csvBuilder.append("location\n")
            .append("id,received,timestamp,latitude,longitude,altitude,speed,accuracy\n")
        dao.getAllLocationData().forEach { entry ->
            csvBuilder.append("${entry.id},${entry.received},${entry.timestamp},")
                .append("${entry.latitude},${entry.longitude},${entry.altitude},${entry.speed},${entry.accuracy}\n")
        }
    }
}
