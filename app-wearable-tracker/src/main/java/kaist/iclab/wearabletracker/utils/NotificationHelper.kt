package kaist.iclab.wearabletracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kaist.iclab.wearabletracker.Constants
import kaist.iclab.wearabletracker.R

/**
 * Helper class for creating and showing notifications in the wearable tracker app.
 * Provides reusable methods for common notification patterns.
 */
object NotificationHelper {
    /**
     * Notification channel configurations
     */
    enum class NotificationChannelConfig(
        val channelId: String,
        val channelName: String,
        val description: String
    ) {
        PHONE_COMMUNICATION(
            Constants.NotificationChannel.PHONE_COMMUNICATION_ID,
            Constants.NotificationChannel.PHONE_COMMUNICATION_NAME,
            Constants.NotificationChannel.PHONE_COMMUNICATION_DESCRIPTION
        ),
        FLUSH_OPERATION(
            Constants.NotificationChannel.FLUSH_OPERATION_ID,
            Constants.NotificationChannel.FLUSH_OPERATION_NAME,
            Constants.NotificationChannel.FLUSH_OPERATION_DESCRIPTION
        )
    }

    /**
     * Ensure a notification channel exists. Safe to call multiple times.
     */
    fun ensureNotificationChannel(
        context: Context,
        config: NotificationChannelConfig
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            config.channelId,
            config.channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = config.description
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Show a success notification
     */
    fun showSuccessNotification(
        context: Context,
        channelConfig: NotificationChannelConfig,
        title: String,
        message: String,
        notificationId: Int
    ) {
        ensureNotificationChannel(context, channelConfig)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelConfig.channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * Show a failure notification
     */
    fun showFailureNotification(
        context: Context,
        channelConfig: NotificationChannelConfig,
        title: String,
        message: String,
        notificationId: Int
    ) {
        ensureNotificationChannel(context, channelConfig)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelConfig.channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    /**
     * Show phone communication success notification
     */
    fun showPhoneCommunicationSuccess(context: Context) {
        showSuccessNotification(
            context = context,
            channelConfig = NotificationChannelConfig.PHONE_COMMUNICATION,
            title = Constants.NotificationMessage.PhoneCommunication.SUCCESS_TITLE,
            message = Constants.NotificationMessage.PhoneCommunication.SUCCESS_MESSAGE,
            notificationId = Constants.NotificationId.PHONE_COMMUNICATION_SUCCESS
        )
    }

    /**
     * Show phone communication failure notification
     */
    fun showPhoneCommunicationFailure(context: Context, message: String) {
        showFailureNotification(
            context = context,
            channelConfig = NotificationChannelConfig.PHONE_COMMUNICATION,
            title = Constants.NotificationMessage.PhoneCommunication.FAILURE_TITLE,
            message = message,
            notificationId = Constants.NotificationId.PHONE_COMMUNICATION_FAILURE
        )
    }

    /**
     * Show flush operation success notification
     */
    fun showFlushSuccess(context: Context) {
        showSuccessNotification(
            context = context,
            channelConfig = NotificationChannelConfig.FLUSH_OPERATION,
            title = Constants.NotificationMessage.FlushOperation.SUCCESS_TITLE,
            message = Constants.NotificationMessage.FlushOperation.SUCCESS_MESSAGE,
            notificationId = Constants.NotificationId.FLUSH_OPERATION_SUCCESS
        )
    }

    /**
     * Show flush operation failure notification
     */
    fun showFlushFailure(context: Context, message: String) {
        showFailureNotification(
            context = context,
            channelConfig = NotificationChannelConfig.FLUSH_OPERATION,
            title = Constants.NotificationMessage.FlushOperation.FAILURE_TITLE,
            message = message,
            notificationId = Constants.NotificationId.FLUSH_OPERATION_FAILURE
        )
    }
}

