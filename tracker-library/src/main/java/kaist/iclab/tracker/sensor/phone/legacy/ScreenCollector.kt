package kaist.iclab.tracker.sensor.phone.legacy

//class ScreenCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<ScreenCollector.Config, ScreenCollector.Entity>(permissionManager) {
//    override val permissions = listOfNotNull<String>().toTypedArray()
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    /*No attribute required... can not be data class*/
//    class Config: CollectorConfig()
//
//    override val _defaultConfig = Config()
//
//    // Access to Battery Status might be supported for all android systems
//    override fun isAvailable() = Availability(true)
//
//
//    override fun start() {
//        super.start()
//        broadcastTrigger.register()
//    }
//
//    override fun stop() {
//        broadcastTrigger.unregister()
//        super.stop()
//    }
//
//    private val broadcastTrigger = BroadcastListener(
//        context,
//        arrayOf(
//            Intent.ACTION_SCREEN_ON,
//            Intent.ACTION_SCREEN_OFF,
//            Intent.ACTION_USER_PRESENT
//        )
//    ) { intent ->
//        val timestamp = System.currentTimeMillis()
//        listener?.invoke(
//            Entity(
//                timestamp,
//                timestamp,
//                intent.action ?: "UNKNOWN"
//            )
//        )
//    }
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val type: String,
//    ) : DataEntity(received)
//}