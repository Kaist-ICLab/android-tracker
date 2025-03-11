package dev.iclab.test_listener

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kaist.iclab.tracker.listener.NotificationListener
import kaist.iclab.tracker.listener.core.NotificationEventInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

//@RunWith(AndroidJUnit4::class)
//class NotificationListenerInstrumentedTest {
//    private var context: Context = ApplicationProvider.getApplicationContext()
//    private var listener: NotificationListener = NotificationListener()
//    private val timeoutSeconds = 10L
//    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    private val testNotificationId = 1001
//    private val testChannelId = "test_channel"
//
//    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(testChannelId, "Test Channel", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    @Test
//    fun testNotificationListenerReceivesEvent() {
//        val latch = CountDownLatch(1)
//        val testCallback = { event: NotificationEventInfo ->
//            if (event is NotificationEventInfo.Posted) {
//                latch.countDown()
//            }
//        }
//
//        listener.addListener(testCallback)
//
//        // Send a real notification
//        val notification = NotificationCompat.Builder(context, testChannelId)
//            .setContentTitle("Test Notification")
//            .setContentText("This is a test notification.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//        notificationManager.notify(testNotificationId, notification)
//
//        assertTrue(latch.await(timeoutSeconds, TimeUnit.SECONDS))
//
//        listener.removeListener(testCallback)
//    }
//
//    @Test
//    fun testNotificationListenerReceivesRemoveEvent() {
//        val latch = CountDownLatch(1)
//        val testCallback = { event: NotificationEventInfo ->
//            if (event is NotificationEventInfo.Removed) {
//                latch.countDown()
//            }
//        }
//
//        listener.addListener(testCallback)
//
//        // Send and remove a real notification
//        val notification = NotificationCompat.Builder(context, testChannelId)
//            .setContentTitle("Test Notification")
//            .setContentText("This is a test notification.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//        notificationManager.notify(testNotificationId, notification)
//        notificationManager.cancel(testNotificationId)
//
//        assertTrue(latch.await(timeoutSeconds, TimeUnit.SECONDS))
//
//        listener.removeListener(testCallback)
//    }
//
//    @Test
//    fun testNotificationListenerDoesNotReceiveAfterRemoval() {
//        val latch = CountDownLatch(1)
//        val testCallback: (NotificationEventInfo) -> Unit = { event ->
//            if (event is NotificationEventInfo.Posted) {
//                latch.countDown()
//            }
//        }
//
//        listener.addListener(testCallback)
//        listener.removeListener(testCallback)
//
//        val notification = NotificationCompat.Builder(context, testChannelId)
//            .setContentTitle("Test Notification")
//            .setContentText("This is a test notification.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//        notificationManager.notify(testNotificationId, notification)
//
//        assertFalse("Listener should not receive the event after removal", latch.await(2, TimeUnit.SECONDS))
//    }
//
//    @Test
//    fun testMultipleListenersReceiveEvent() {
//        val testCallbackCount = 10
//        val latch = CountDownLatch(testCallbackCount)
//        val receivers = mutableListOf<(NotificationEventInfo) -> Unit>()
//
//        repeat(testCallbackCount) {
//            val testCallback: (NotificationEventInfo) -> Unit = { event ->
//                if (event is NotificationEventInfo.Posted) {
//                    latch.countDown()
//                }
//            }
//            receivers.add(testCallback)
//            listener.addListener(testCallback)
//        }
//
//        val notification = NotificationCompat.Builder(context, testChannelId)
//            .setContentTitle("Test Notification")
//            .setContentText("This is a test notification.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//        notificationManager.notify(testNotificationId, notification)
//
//        assertTrue("NotificationListener did not receive the event", latch.await(timeoutSeconds, TimeUnit.SECONDS))
//
//        for (callback in receivers) {
//            listener.removeListener(callback)
//        }
//    }
//
//    @Test
//    fun testAddingListenerTwiceThrowsException() {
//        val testCallback: (NotificationEventInfo) -> Unit = { }
//        listener.addListener(testCallback)
//
//        assertThrows(AssertionError::class.java) {
//            listener.addListener(testCallback)
//        }
//    }
//
//    @Test
//    fun testRemoveNonExistentListenerThrowsException() {
//        val testCallback: (NotificationEventInfo) -> Unit = { }
//        val anotherTestCallback: (NotificationEventInfo) -> Unit = { }
//
//        listener.addListener(testCallback)
//
//        assertThrows(AssertionError::class.java) {
//            listener.removeListener(anotherTestCallback)
//        }
//    }
//}
