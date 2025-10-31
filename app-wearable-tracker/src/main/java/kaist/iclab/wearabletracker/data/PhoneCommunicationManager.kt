package kaist.iclab.wearabletracker.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.wearabletracker.db.dao.AccelerometerDao
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.db.dao.EDADao
import kaist.iclab.wearabletracker.db.dao.HeartRateDao
import kaist.iclab.wearabletracker.db.dao.LocationDao
import kaist.iclab.wearabletracker.db.dao.PPGDao
import kaist.iclab.wearabletracker.db.dao.SkinTemperatureDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PhoneCommunicationManager(
    private val androidContext: Context,
    private val daos: Map<String, BaseDao<*>>,
) {
    private val TAG = javaClass.simpleName
    private val bleChannel: BLEDataChannel = BLEDataChannel(androidContext)
    private val nodeClient: NodeClient by lazy { Wearable.getNodeClient(androidContext) }

    companion object {
        private const val BLE_KEY_SENSOR_DATA = "sensor_data_csv"
    }

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
                   Log.e(TAG, "Error sending data to phone: Phone is not available or not connected")
                   return@launch
               }

                val csvData = generateCSVData()
                if (csvData.isNotEmpty()) {
                   bleChannel.send(BLE_KEY_SENSOR_DATA, csvData)
                    Log.d(TAG, "Sensor data sent to phone via BLE")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending data to phone: ${e.message}", e)
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
                "Accelerometer" -> appendAccelerometerData(csvBuilder, dao as? AccelerometerDao)
                "PPG" -> appendPPGData(csvBuilder, dao as? PPGDao)
                "HeartRate" -> appendHeartRateData(csvBuilder, dao as? HeartRateDao)
                "SkinTemperature" -> appendSkinTemperatureData(
                    csvBuilder,
                    dao as? SkinTemperatureDao
                )

                "EDA" -> appendEDAData(csvBuilder, dao as? EDADao)
                "Location" -> appendLocationData(csvBuilder, dao as? LocationDao)
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
