package kaist.iclab.tracker.listener

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AccessibilityListener: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d(javaClass.simpleName, "onAccessibilityEvent: $event")
    }

    override fun onInterrupt() {}
}