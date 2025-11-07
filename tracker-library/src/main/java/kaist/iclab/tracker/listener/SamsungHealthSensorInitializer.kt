package kaist.iclab.tracker.listener

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.PpgType
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SamsungHealthSensorInitializer(context: Context) {
    companion object {
        private val TAG = SamsungHealthSensorInitializer::class.simpleName
    }

    enum class ConnectionState {
        NOT_CONNECTED,
        CONNECTED,
        FAILED
    }

    val connectionStateFlow: MutableStateFlow<ConnectionState> = MutableStateFlow(ConnectionState.NOT_CONNECTED)

    private val connectionListener: ConnectionListener =
        object : ConnectionListener {
            override fun onConnectionSuccess() {
                Log.d(TAG, "Connection Success")
                connectionStateFlow.value = ConnectionState.CONNECTED
            }

            override fun onConnectionEnded() {
                connectionStateFlow.value = ConnectionState.NOT_CONNECTED
                Log.d(TAG, "Connection Ended")
            }

            override fun onConnectionFailed(e: HealthTrackerException?) {
                connectionStateFlow.value = ConnectionState.FAILED
                Log.e(TAG, "Connection Failed: $e")
            }
        }

    private val healthTrackingService = HealthTrackingService(connectionListener, context)

    init {
        try {
            healthTrackingService.connectService()
        } catch (e: Exception) {
            // If service package is not available, mark connection as failed
            Log.e(TAG, "Failed to connect to Samsung Health service: ${e.message}", e)
            connectionStateFlow.value = ConnectionState.FAILED
        }
    }

    fun getTracker(trackerType: HealthTrackerType): HealthTracker {
        return healthTrackingService.getHealthTracker(trackerType)
    }

    fun getTracker(trackerType: HealthTrackerType, ppgsets: Set<PpgType>): HealthTracker {
        return healthTrackingService.getHealthTracker(trackerType, ppgsets)
    }

    fun isTrackerAvailable(trackerType: HealthTrackerType): Boolean {
        val supportedTrackers = healthTrackingService.trackingCapability.supportHealthTrackerTypes
        return trackerType in supportedTrackers
    }

    /**
     * Check if a tracker is available and update the sensor state accordingly.
     * This is a reusable helper function to avoid duplication across sensors.
     * Since binding to the service takes a while, we subscribe to the connection stateflow
     * and check it when it is actually connected. If the connection fails, mark the sensor as unavailable.
     *
     * @param trackerType The HealthTrackerType to check
     * @param stateStorage The StateStorage to update if the tracker is unavailable
     * @param sensorName The name of the sensor (for error messages)
     */
    fun checkTrackerAvailability(
        trackerType: HealthTrackerType,
        stateStorage: StateStorage<SensorState>,
        sensorName: String
    ) {
        // Wait for the service to be connected before checking availability
        CoroutineScope(Dispatchers.IO).launch {
            connectionStateFlow.collect { state ->
                when (state) {
                    ConnectionState.FAILED -> {
                        // Connection failed permanently, mark sensor as unavailable
                        stateStorage.set(
                            SensorState(
                                SensorState.FLAG.UNAVAILABLE,
                                "$sensorName not available: Samsung Health service not available"
                            )
                        )
                        this.cancel()
                    }
                    ConnectionState.NOT_CONNECTED -> {
                        // Still waiting for connection, do nothing
                        return@collect
                    }
                    ConnectionState.CONNECTED -> {
                        // Connection established, check availability
                        try {
                            val isAvailable = isTrackerAvailable(trackerType)
                            if (!isAvailable) {
                                stateStorage.set(
                                    SensorState(
                                        SensorState.FLAG.UNAVAILABLE,
                                        "$sensorName not supported on this device."
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            // If service is not available, mark the sensor as unavailable
                            stateStorage.set(
                                SensorState(
                                    SensorState.FLAG.UNAVAILABLE,
                                    "$sensorName not available: ${e.message}"
                                )
                            )
                        }
                        this.cancel()
                    }
                }
            }
        }
    }

    class DataListener(private val callback: (MutableList<DataPoint>) -> Unit) :
        HealthTracker.TrackerEventListener {
        override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
            callback(dataPoints)
        }

        override fun onError(trackerError: HealthTracker.TrackerError) {
            Log.d(javaClass.simpleName, "onError")
            when (trackerError) {
                HealthTracker.TrackerError.PERMISSION_ERROR ->
                    Log.e(javaClass.simpleName, "ERROR: Permission Failed")

                HealthTracker.TrackerError.SDK_POLICY_ERROR ->
                    Log.e(javaClass.simpleName, "ERROR: SDK Policy Error")

                else -> Log.e(javaClass.simpleName, "ERROR: Unknown ${trackerError.name}")
            }
        }

        override fun onFlushCompleted() {
            Log.d(javaClass.simpleName, "onFlushCompleted")
        }
    }
}
