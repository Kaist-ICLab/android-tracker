package kaist.iclab.wearablelogger.healthtracker

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener

abstract class AbstractTrackerEventListener: TrackerEventListener {
    val TAG = javaClass.simpleName

    override fun onError(trackerError: HealthTracker.TrackerError) {
        Log.d(TAG, "onError")
        when (trackerError) {
            HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
                TAG,
                "ERROR: Permission Failed"
            )

            HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
                TAG,
                "ERROR: SDK Policy Error"
            )

            else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
        }
    }

    override fun onFlushCompleted() {
        Log.d(TAG, "onFlushCompleted")
    }
}