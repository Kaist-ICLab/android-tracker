//package kaist.iclab.wearablelogger.healthtracker
//
//import android.content.Context
//import android.util.Log
//import com.samsung.android.service.health.tracking.ConnectionListener
//import com.samsung.android.service.health.tracking.HealthTrackerException
//import com.samsung.android.service.health.tracking.HealthTrackingService
//
//class HealthTrackerRepository(
//    androidContext: Context
//) {
//    private val TAG = javaClass.simpleName
//    private val connectionListener: ConnectionListener = object: ConnectionListener {
//        override fun onConnectionSuccess() {
//            Log.d(TAG, "Connection Success")
//        }
//
//        override fun onConnectionEnded() {
//            Log.d(TAG, "Connection Ended")
//        }
//
//        override fun onConnectionFailed(e: HealthTrackerException?) {
//            Log.e(TAG, "Connection Failed: $e")
//        }
//    }
//    val healthTrackingService: HealthTrackingService = HealthTrackingService(connectionListener, androidContext)
//    fun start(){
//        healthTrackingService.connectService()
//    }
//
//    fun finish(){
//        healthTrackingService.disconnectService()
//    }
//}