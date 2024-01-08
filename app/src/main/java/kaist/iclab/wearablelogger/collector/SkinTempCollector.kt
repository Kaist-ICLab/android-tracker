package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import kaist.iclab.wearablelogger.HealthTrackerRepo
import kaist.iclab.wearablelogger.db.AccDao
import kaist.iclab.wearablelogger.db.TestDao

class SkinTempCollector(
    val androidContext: Context,
    val healthTrackerRepo: HealthTrackerRepo,
    val testDao: TestDao,
): AbstractCollector() {

    private var SkinTempTracker: HealthTracker? =  null
    private var healthTrackingService: HealthTrackingService? = null
    private val TAG = "SkinTempCollector"

    private val trackerEventListener: HealthTracker.TrackerEventListener = object :
        HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            Log.d(TAG, "onDataReceived = timestamp: ${timestamp} ,size: ${list.size}")
        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
        }
        override fun onError(trackerError: HealthTracker.TrackerError) {
            if (trackerError == HealthTracker.TrackerError.PERMISSION_ERROR) {
                Log.d(TAG, "onError = Permission Failed")
            } else if (trackerError == HealthTracker.TrackerError.SDK_POLICY_ERROR) {
                Log.d(TAG, "onError = SDK policy denied")
            } else {
                Log.d(TAG, "onError = Unknown Error ${trackerError}")
            }
        }
    }
    private val connectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            SkinTempTracker = healthTrackingService?.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
            Log.d(TAG, "connectionListener onConnectionSuccess")
        }
        override fun onConnectionEnded() {
            Log.d(TAG, "connectionListener onConnectionEnded")
        }
        override fun onConnectionFailed(e: HealthTrackerException) {
            Log.d(TAG, "connectionListener onConnectionFailed: ${e}")
        }
    }
    override fun setup() {
        Log.d(TAG, "setup()")
        healthTrackingService = HealthTrackingService(connectionListener, androidContext)
        healthTrackingService?.connectService()
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")

        try {
            SkinTempTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "SkinTempCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        SkinTempTracker?.unsetEventListener()
        healthTrackingService?.disconnectService()
    }
}