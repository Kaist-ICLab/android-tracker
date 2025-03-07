package dev.iclab.test_listener

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kaist.iclab.tracker.listener.AlarmListener
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AlarmListenerInstrumentedTest {
    private var context: Context = ApplicationProvider.getApplicationContext()
    private var listener: AlarmListener
    private val testAction = "ACTION_TEST_ALARM"
    private val testCode = 1001
    private val testIntervalMs = 1000L  // 1 second

    private val timeoutSeconds = 10L

    init {
        listener = AlarmListener(context, testAction, testCode, testIntervalMs)
    }

    @Test
    fun testAlarmListenerReceivesIntent() {
        val latch = CountDownLatch(1)

        val testCallback: (Intent?) -> Unit = { intent ->
            if (intent?.action == testAction) {
                latch.countDown() // Signal that the broadcast was received
            }
        }

        // Register listener
        listener.addListener(testCallback)

        // Manually trigger an alarm (for testing purposes)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerTime = SystemClock.elapsedRealtime() + 500  // 0.5 seconds delay
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, listener.getPendingIntent())

        // Wait up to 2 seconds for the alarm to trigger
        assertTrue("AlarmListener did not receive the broadcast", latch.await(timeoutSeconds, TimeUnit.SECONDS))

        // Cleanup
        listener.removeListener(testCallback)
    }

    @Test
    fun testAlarmListenerDoesNotReceiveAfterRemoval() {
        val latch = CountDownLatch(1)

        val testCallback: (Intent?) -> Unit = { intent ->
            if (intent?.action == testAction) {
                latch.countDown() // Should not be triggered after removal
            }
        }

        // Add and then immediately remove the listener
        listener.addListener(testCallback)
        listener.removeListener(testCallback)

        // Manually trigger an alarm (for testing purposes)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(testAction)
        val triggerTime = SystemClock.elapsedRealtime() + 500  // 0.5 seconds delay
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, listener.getPendingIntent())

        // Ensure the listener does NOT receive the alarm broadcast within 2 seconds
        assertFalse("Listener should not receive the alarm after removal", latch.await(timeoutSeconds, TimeUnit.SECONDS))
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

        // Manually trigger an alarm (for testing purposes)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerTime = SystemClock.elapsedRealtime() + 500  // 0.5 seconds delay
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, listener.getPendingIntent())

        // Wait up to 2 seconds for the alarm to trigger
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

        assertThrows(AssertionError::class.java) {
            listener.removeListener(testCallback)
        }
    }
}