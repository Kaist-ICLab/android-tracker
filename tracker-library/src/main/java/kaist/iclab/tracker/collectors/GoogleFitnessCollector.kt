//package kaist.iclab.tracker.collectors
//
//import android.Manifest
//import android.content.Context
//import android.net.TrafficStats
//import android.os.Build
//import android.util.Log
//import com.google.android.gms.fitness.Fitness
//import com.google.android.gms.fitness.FitnessOptions
//import com.google.android.gms.fitness.data.DataType
//import kaist.iclab.tracker.database.DatabaseInterface
//import kaist.iclab.tracker.filters.Filter
//import kaist.iclab.tracker.triggers.AlarmTrigger
//
//class GoogleFitnessCollector(
//    override val context: Context,
//    override val database: DatabaseInterface
//) : AbstractCollector(
//    context, database
//) {
//    companion object {
//        const val NAME = "GOOGLE_FITNESS"
//        const val action = "kaist.iclab.tracker.GOOGLE_FITNESS_REQUEST"
//        const val code = 0x3
//    }
//
//    override val NAME: String
//        get() = Companion.NAME
//    override val permissions: Array<String> =listOfNotNull(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null,
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
//    ).toTypedArray()
//
//
//    override val filters: MutableList<Filter> = mutableListOf()
//    override val TAG: String
//        get() = super.TAG
//
//    data class Entity(
//        val timestamp: Long,
//        val start
//        val totalRx: Long,
//        val totalTx: Long,
//        val mobileRx: Long,
//        val mobileTx: Long,
//    )
//
//
//    val fitnessOptions by lazy {
//            FitnessOptions.builder()
//            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
//            .build();
//    }
//
//    val googleSignInAccount by lazy {
//        GoogleSignIn.getAccountForExtension(this, fitnessOptions)
//    }
//
//
////    fun retrieve(): Entity {
////        return Entity(
////            System.currentTimeMillis(),
////
////        )
////    }
//
//
//
//    override fun isAvailable(): Boolean = {
//
//    }
//
//    override fun start() {
//        Log.d(TAG, "start")
//        Fitness.getRecordingClient(
//            context,
//
//        )
//    }
//    override fun stop() {
//        Log.d(TAG, "stop")
//        alarmTrigger.unregister()
//    }
//
//
//}