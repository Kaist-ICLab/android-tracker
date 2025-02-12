package kaist.iclab.tracker.collector.phone

//class UserInteractionCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<UserInteractionCollector.Config, UserInteractionCollector.Entity>(permissionManager) {
//    companion object {
//        var instance: WeakReference<UserInteractionCollector>? = null
//    }
//    init {
//        instance = WeakReference(this)
//    }
//
//
//    override val permissions = listOfNotNull<String>().toTypedArray()
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    /*No attribute required... can not be data class*/
//    class Config: CollectorConfig()
//    override val _defaultConfig = Config()
//
//    override fun isAvailable(): Availability {
//        return Availability(true)
//    }
//
//
////    class MyAccessibilityService : AccessibilityService() {
////        override fun onAccessibilityEvent(event: AccessibilityEvent?) {
////            val isRunning = instance?.get()?.stateFlow?.value?.flag == CollectorState.FLAG.RUNNING
////            if(!isRunning) return
////            event?.let { it->
////                val timestamp = System.currentTimeMillis()
////                instance?.get()?.listener?.invoke(
////                    Entity(
////                        timestamp,
////                        timestamp,
////                        it.packageName?.toString()?: "UNKNOWN",
////                        it.className?.toString()?: "UNKNOWN",
////                        it.eventType,
////                        it.text.toString()
////                    )
////                )
////            }
////        }
////        override fun onInterrupt() {}
////    }
//
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val packageName: String,
//        val className: String,
//        val eventType: Int,
//        val text: String
//    ) : DataEntity(received)
//}