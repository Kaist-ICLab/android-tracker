package dev.iclab.test_listener

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kaist.iclab.tracker.listener.BroadcastListener
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BroadcastListenerInstrumentedTest {
    // Use real application context
    private var context: Context = ApplicationProvider.getApplicationContext()
    private var listener: BroadcastListener
    private val testAction = "ACTION_TEST"
    private val timeoutSeconds = 10L

    init {
        listener = BroadcastListener(context, arrayOf(testAction))
    }

    @Test
    fun testBroadcastListenerReceivesIntent() {
        val latch = CountDownLatch(1)
        val testCallback = { intent: Intent? ->
            if (intent?.action == testAction) {
                latch.countDown() // Signal that the broadcast was received
            }
        }

        // Add listener to capture broadcast
        listener.addListener(testCallback)

        // Send test broadcast
        val intent = Intent(testAction)
        context.sendBroadcast(intent)

        // Wait few seconds for broadcast to be received
        assertTrue(latch.await(timeoutSeconds, TimeUnit.SECONDS))

        // Cleanup
        listener.removeListener(testCallback)
    }

    @Test
    fun testBroadcastListenerDoesNotReceiveAfterRemoval() {
        val latch = CountDownLatch(1)

        val testCallback: (Intent?) -> Unit = { intent ->
            if (intent?.action == testAction) {
                latch.countDown() // Should not be triggered after removal
            }
        }

        // Add and then immediately remove the listener
        listener.addListener(testCallback)
        listener.removeListener(testCallback)

        // Send a test broadcast
        val intent = Intent(testAction)
        context.sendBroadcast(intent)

        // Ensure the listener does NOT receive the broadcast within 2 seconds
        assertFalse("Listener should not receive the broadcast after removal", latch.await(2, TimeUnit.SECONDS))
    }

    @Test
    fun testMultipleListenersReceiveIntent() {
        val testCallbackCount = 10
        val latch = CountDownLatch(testCallbackCount)

        val receivers = mutableListOf<(Intent?) -> Unit>()

        repeat(testCallbackCount) {
            val testCallback: (Intent?) -> Unit = { intent ->
                if (intent?.action == testAction) {
                    latch.countDown() // Should not be triggered after removal
                }
            }

            receivers.add(testCallback)
            listener.addListener(testCallback)
        }

        // Send a test broadcast
        val intent = Intent(testAction)
        context.sendBroadcast(intent)

        // Wait for the broadcast to be propagated
        assertTrue("AlarmListener did not receive the broadcast", latch.await(timeoutSeconds, TimeUnit.SECONDS))

        // Cleanup
        for(callback in receivers) {
            listener.removeListener(callback)
        }
    }

    @Test
    fun testAddingListenerTwiceThrowsException() {
        val testCallback: (Intent?) -> Unit = { }

        listener.addListener(testCallback)

        assertThrows(AssertionError::class.java) {
            listener.addListener(testCallback)
        }
    }

    @Test
    fun testRemoveNonExistentListenerThrowsException() {
        val testCallback: (Intent?) -> Unit = { }
        val anotherTestCallback: (Intent?) -> Unit = { }

        listener.addListener(testCallback)

        assertThrows(AssertionError::class.java) {
            listener.removeListener(anotherTestCallback)
        }
    }
}