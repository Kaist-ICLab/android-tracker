package kaist.iclab.tracker.listener.refactor_required//package kaist.iclab.tracker.listener
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.util.Log
//
//class BroadcastListener(
//    private val context: Context,
//    private val ACTIONS: Array<String>,
//    private val action: (intent: Intent) -> Unit
//) {
//    private val TAG = javaClass.simpleName
//
//    private val receiver = object: BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d(TAG, "receiver:onReceive")
//            intent?.apply { action(this) }
//        }
//    }
//
//    fun register() {
//        Log.d(TAG, "Registering broadcast receiver")
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            /*
//            * From Tiramisu, we need to specify the receiver exported or not
//            * One of RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED should be specified when a receiver isn't being registered exclusively for system broadcasts
//            * */
//            context.registerReceiver(receiver, IntentFilter().apply{
//                ACTIONS.forEach { addAction(it) }
//            }, Context.RECEIVER_EXPORTED)
//
//        }else{
//            context.registerReceiver(receiver, IntentFilter().apply {
//                ACTIONS.forEach { addAction(it) }
//            })
//        }
//
//    }
//
//    fun unregister() {
//        Log.d(TAG, "Unregistering broadcast receiver")
//        context.unregisterReceiver(receiver)
//    }
//}