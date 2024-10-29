package kaist.iclab.tracker.collectors

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.net.TrafficStats
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kaist.iclab.tracker.collectors.LocationCollector.Config
import kaist.iclab.tracker.triggers.AlarmTrigger
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.TimeUnit

class UserInteractionCollector(
    override val context: Context
) : AbstractCollector(context) {


    data class DataEntity(
        val timestamp: Long,
        val packageName: String,
        val className: String,
        val eventType: Int,
        val text: String
    ) : AbstractCollector.DataEntity()


    override val permissions = listOfNotNull<String>().toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()


    override fun isAvailable(): Boolean = true

    object MyAccessibilityService : AccessibilityService() {
        var listener: ((DataEntity) -> Unit)? = null
        var isListening = false
        override fun onAccessibilityEvent(event: AccessibilityEvent?) {
            if (isListening) {
                event?.let {
                    listener?.invoke(
                        DataEntity(
                            System.currentTimeMillis(),
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

    override fun start() {
        MyAccessibilityService.listener = listener
        MyAccessibilityService.startListening()
    }

    override fun stop() {
        MyAccessibilityService.stopListening()
    }
}