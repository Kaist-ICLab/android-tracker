package kaist.iclab.tracker.sensor.deprecated

//
//class ActivityTransitionCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<ActivityTransitionCollector.Config, ActivityTransitionCollector.Entity>(permissionManager) {
//    override val permissions = listOfNotNull(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACTIVITY_RECOGNITION else null,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>(
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else null,
//    ).toTypedArray()
//
//    /*No attribute required... can not be data class*/
//    class Config: CollectorConfig()
//
//    override val _defaultConfig = Config()
//
//    override fun isAvailable(): Availability {
//        val status =  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            Settings.Secure.getInt(
//                context.contentResolver,
//                Settings.Secure.LOCATION_MODE
//            ) != Settings.Secure.LOCATION_MODE_OFF
//        } else {
//            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isLocationEnabled
//        }
//        if(!status) return Availability(false, "Location service is disabled")
//        else return Availability(true)
//    }
//
//
//    override fun start() {
//        super.start()
//        val request = listOf(
//            DetectedActivity.IN_VEHICLE,
//            DetectedActivity.ON_BICYCLE,
//            DetectedActivity.RUNNING,
//            DetectedActivity.STILL,
//            DetectedActivity.WALKING
//        ).map { activity ->
//            listOf(
//                ActivityTransition.Builder()
//                    .setActivityType(activity)
//                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
//                    .build(),
//                ActivityTransition.Builder()
//                    .setActivityType(activity)
//                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
//                    .build()
//            )
//        }.flatten().let { ActivityTransitionRequest(it) }
//        client.requestActivityTransitionUpdates(request, activityTransitionIntent)
//        broadcastTrigger.register()
//    }
//
//    override fun stop() {
//        client.removeActivityTransitionUpdates(activityTransitionIntent)
//        broadcastTrigger.unregister()
//        super.stop()
//        DetectedActivity.STILL
//    }
//
//    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"
//    val CODE = 0xF1
//
//    private val client: ActivityRecognitionClient by lazy {
//        ActivityRecognition.getClient(context)
//    }
//
//    private val activityTransitionIntent by lazy {
//        PendingIntent.getBroadcast(
//            context, CODE,
//            Intent(ACTION),
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//            else PendingIntent.FLAG_UPDATE_CURRENT
//        )
//    }
//
//    private val broadcastTrigger = BroadcastListener(
//        context,
//        arrayOf(
//            ACTION
//        )
//    ) {
//        val result = ActivityTransitionResult.extractResult(it)
//        result?.transitionEvents?.forEach { event ->
//            val timestamp = System.currentTimeMillis()
//            listener?.invoke(
//                Entity(
//                    timestamp,
//                    timestamp,
//                    event.activityType,
//                    event.transitionType
//                )
//            )
//        }
//    }
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val activityType: Int,
//        val transitionType: Int
//    ) : DataEntity(received)
//}