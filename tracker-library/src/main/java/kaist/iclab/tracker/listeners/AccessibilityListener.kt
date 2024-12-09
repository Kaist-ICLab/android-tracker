package kaist.iclab.tracker.listeners

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AccessibilityListener: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d(javaClass.simpleName, "onAccessibilityEvent: $event")
    }

    override fun onInterrupt() {}
}