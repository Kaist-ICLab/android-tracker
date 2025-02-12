package kaist.iclab.tracker.collector.galaxywatch

//class AccelerometerCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface,
//    healthSensosrListener: SamsungHealthSensorListener
//) : AbstractCollector<AccelerometerCollector.Config, AccelerometerCollector.Entity>(
//    permissionManager
//) {
//    override val permissions = listOfNotNull(
//        Manifest.permission.BODY_SENSORS,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    /*No attribute required... can not be data class*/
//    class Config : CollectorConfig()
//
//    override val _defaultConfig = Config()
//
//    override fun isAvailable(): Availability = Availability(true)
//
//    private val tracker = healthSensosrListener.getTracker(HealthTrackerType.ACCELEROMETER_CONTINUOUS)
//
//    val trigger = object : HealthTracker.TrackerEventListener {
//        override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
//            val timestamp = System.currentTimeMillis()
//            dataPoints.forEach { dataPoint ->
//                listener?.invoke(
//                    Entity(
//                        timestamp,
//                        dataPoint.timestamp,
//                        dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X),
//                        dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y),
//                        dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z)
//                    )
//                )
//            }
//        }
//
//        override fun onError(trackerError: HealthTracker.TrackerError) {
//            Log.d(TAG, "onError")
//            when (trackerError) {
//                HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
//                    TAG,
//                    "ERROR: Permission Failed"
//                )
//
//                HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
//                    TAG,
//                    "ERROR: SDK Policy Error"
//                )
//
//                else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
//            }
//        }
//
//        override fun onFlushCompleted() {
//            Log.d(TAG, "onFlushCompleted")
//        }
//    }
//
//    override fun start() {
//        super.start()
//        tracker.setEventListener(trigger)
//    }
//
//    override fun stop() {
//        tracker.unsetEventListener()
//        super.stop()
//    }
//
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val x: Int,
//        val y: Int,
//        val z: Int
//    ) : DataEntity(received)
//}