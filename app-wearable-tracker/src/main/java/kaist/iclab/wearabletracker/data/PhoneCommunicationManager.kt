package kaist.iclab.wearabletracker.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.wearabletracker.Constants
import kaist.iclab.wearabletracker.R
import kaist.iclab.wearabletracker.db.dao.BaseDao
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
     * Generate CSV formatted data from all sensor DAOs using generic serialization.
     * Each entity implements CsvSerializable, so we can generate CSV without
     * knowing the specific sensor type.
     */
    private suspend fun generateCSVData(): String {
        val csvBuilder = StringBuilder()

        daos.forEach { (sensorId, dao) ->
            val data = dao.getAllForExport()
            if (data.isNotEmpty()) {
                csvBuilder.append("$sensorId\n")
                csvBuilder.append(data.first().toCsvHeader() + "\n")
                data.forEach { csvBuilder.append(it.toCsvRow() + "\n") }
            }
            csvBuilder.append("\n")
        }

        return csvBuilder.toString()
    }
}

