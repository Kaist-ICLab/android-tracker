package kaist.iclab.tracker.collectors

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface

class UserInteractionCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<UserInteractionCollector.Config,UserInteractionCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>().toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    /*No attribute required... can not be data class*/
    class Config: CollectorConfig()
    override val defaultConfig = Config()

    override fun isAvailable(): Availability {
        return Availability(true)
    }

    override fun start() {
        MyAccessibilityService.instance?.listener = listener
        MyAccessibilityService.instance?.startListening()
    }

    override fun stop() {
        MyAccessibilityService.instance?.stopListening()
    }


    class MyAccessibilityService : AccessibilityService() {
        companion object {
            var instance: MyAccessibilityService? = null
        }
        var listener: ((DataEntity) -> Unit)? = null
        private var isListening = false

        override fun onServiceConnected() {
            super.onServiceConnected()
            Log.d("MyAccessibilityService", "onCreate")
            instance = this

        }

//        override fun onCreate() {
//            super.onCreate()
//         }

        override fun onDestroy() {
            super.onDestroy()
            instance = null
        }

        override fun onAccessibilityEvent(event: AccessibilityEvent?) {
            Log.d("MyAccessibilityService", "onAccessibilityEvent")
            if (isListening) {
                event?.let {
                    val timestamp = System.currentTimeMillis()
                    listener?.invoke(
                        Entity(
                            timestamp,
                            timestamp,
                            event.packageName.toString(),
                            event.className.toString(),
                            event.eventType,
                            event.text.toString()
                        )
                    )
                }
            }
        }

        override fun onInterrupt() {}

        fun startListening() {
            isListening = true
        }

        fun stopListening() {
            isListening = false
        }

    }


    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val packageName: String,
        val className: String,
        val eventType: Int,
        val text: String
    ) : DataEntity(received)
}