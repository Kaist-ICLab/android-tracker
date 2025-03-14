package dev.iclab.test_listener
//
//import androidx.test.platform.app.InstrumentationRegistry
//import junit.framework.TestCase.assertEquals
//import kaist.iclab.tracker.listener.AccessibilityListener
//import kaist.iclab.tracker.listener.core.AccessibilityEventInfo
//import org.junit.Test
//
//class AccessibilityListenerInstrumentedTest {
//    private var listener: AccessibilityListener = AccessibilityListener()
//
//    init {
//        // Start the service before tests
//        serviceRule.startService(
//            InstrumentationRegistry.getInstrumentation().context,
//            AccessibilityListener::class.java)
//    }
//
//    @Test
//    fun testOnAccessibilityEvent() {
//        // Create a mock AccessibilityEvent
//        val mockEvent = Mockito.mock(AccessibilityEvent::class.java)
//
//        // Capture the callback passed to addListener
//        val callbackCaptor = argumentCaptor<(AccessibilityEventInfo) -> Unit>()
//        listener.addListener(callbackCaptor.capture())
//
//        // Call onAccessibilityEvent with the mock event
//        listener.onAccessibilityEvent(mockEvent)
//
//        // Verify that the callback was called with the expected event
//        val capturedEvent = callbackCaptor.firstValue
//        assert(capturedEvent is AccessibilityEventInfo.Event)
//        assertEquals(mockEvent, (capturedEvent as AccessibilityEventInfo.Event).event)
//    }
//
//    @Test
//    fun testAddAndRemoveListener() {
//        // Create a mock listener
//        val mockListener = Mockito.mock<(AccessibilityEventInfo) -> Unit>()
//
//        // Add the listener
//        listener.addListener(mockListener)
//
//        // Verify the listener is added
//        val field = listener.javaClass.getDeclaredField("receivers")
//        field.isAccessible = true
//        val receivers = field.get(listener) as MutableList<*>
//        assert(receivers.contains(mockListener))
//
//        // Remove the listener
//        listener.removeListener(mockListener)
//
//        // Verify the listener is removed
//        assert(!receivers.contains(mockListener))
//    }
//
//    @Test
//    fun testOnInterrupt() = runTest {
//        // Create a mock listener
//        val mockListener = Mockito.mock<(AccessibilityEventInfo) -> Unit>()
//
//        // Add the mock listener to the listener's receivers list
//        listener.addListener(mockListener)
//
//        // Call onInterrupt
//        listener.onInterrupt()
//
//        // Verify that the listener was called with Interrupt
//        Mockito.verify(mockListener).invoke(AccessibilityEventInfo.Interrupt)
//    }
//}