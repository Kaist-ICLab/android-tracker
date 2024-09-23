package kaist.iclab.wearablelogger.data.source

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class HealthTrackerSource(
    context: Context
) {
    private val TAG = javaClass.simpleName
    private val connectionListener: ConnectionListener = object: ConnectionListener {
        override fun onConnectionSuccess() {
            Log.d(TAG, "Connection Success")
        }

        override fun onConnectionEnded() {
            Log.d(TAG, "Connection Ended")
        }

        override fun onConnectionFailed(e: HealthTrackerException?) {
            Log.e(TAG, "Connection Failed: $e")
        }
    }
    private val healthTrackingService =
        HealthTrackingService(connectionListener, context)

    init {
        healthTrackingService.connectService()
    }

    fun getTracker(trackerType: HealthTrackerType): HealthTracker {
        return healthTrackingService.getHealthTracker(trackerType)
    }
}