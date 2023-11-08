package kaist.iclab.wearablelogger.collector


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType


class PPGGreenCollector(
    val androidContext: Context
): AbstractCollector() {

    private var ppgGreenTracker: HealthTracker? =  null
    private var healthTrackingService: HealthTrackingService? = null
    private val TAG = "PPGGreenCollector"

    private val trackerEventListener: TrackerEventListener = object : TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            Log.d(TAG, "onDataReceived = timestamp: ${timestamp} ,size: ${list.size}")
        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
        }
        override fun onError(trackerError: TrackerError) {
            if (trackerError == TrackerError.PERMISSION_ERROR) {
                Log.d(TAG, "onError = Permission Failed")
            } else if (trackerError == TrackerError.SDK_POLICY_ERROR) {
                Log.d(TAG, "onError = SDK policy denied")
            } else {
                Log.d(TAG, "onError = Unknown Error ${trackerError}")
            }
        }
    }
    private val connectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            ppgGreenTracker = healthTrackingService?.getHealthTracker(HealthTrackerType.PPG_GREEN)
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
        healthTrackingService = HealthTrackingService(connectionListener, androidContext)
        healthTrackingService?.connectService()
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")

        try {
            ppgGreenTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "PPGGreenCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        ppgGreenTracker?.unsetEventListener()
        healthTrackingService?.disconnectService()
    }
}