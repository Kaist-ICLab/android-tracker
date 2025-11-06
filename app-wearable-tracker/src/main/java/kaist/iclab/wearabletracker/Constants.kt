package kaist.iclab.wearabletracker

/**
 * Centralized constants for the wearable tracker app.
 * All constants used across the app should be defined here.
 */
object Constants {
    /**
     * BLE Communication Constants
     */
    object BLE {
        const val KEY_SENSOR_DATA = "sensor_data_csv"
    }

    /**
     * Sensor Type Constants
     */
    object SensorType {
        const val ACCELEROMETER = "Accelerometer"
        const val PPG = "PPG"
        const val HEART_RATE = "HeartRate"
        const val SKIN_TEMPERATURE = "SkinTemperature"
        const val EDA = "EDA"
        const val LOCATION = "Location"
    }

    /**
     * Notification Channel Constants
     */
    object NotificationChannel {
        const val PHONE_COMMUNICATION_ID = "phone_communication_channel"
        const val PHONE_COMMUNICATION_NAME = "Phone Communication"
        const val PHONE_COMMUNICATION_DESCRIPTION = "Notifications for phone communication status"

        const val FLUSH_OPERATION_ID = "flush_operation_channel"
        const val FLUSH_OPERATION_NAME = "Flush Operation"
        const val FLUSH_OPERATION_DESCRIPTION = "Notifications for flush operation status"
    }

    /**
     * Notification ID Constants
     */
    object NotificationId {
        const val PHONE_COMMUNICATION_SUCCESS = 1001
        const val PHONE_COMMUNICATION_FAILURE = 1002
        const val FLUSH_OPERATION_SUCCESS = 1003
        const val FLUSH_OPERATION_FAILURE = 1004
    }

    /**
     * Notification Message Constants
     */
    object NotificationMessage {
        object PhoneCommunication {
            const val SUCCESS_TITLE = "Data Sent Successfully"
            const val SUCCESS_MESSAGE = "Sensor data has been sent to phone"
            const val FAILURE_TITLE = "Data Send Failed"
        }

        object FlushOperation {
            const val SUCCESS_TITLE = "Data Flushed Successfully"
            const val SUCCESS_MESSAGE = "All sensor data has been deleted"
            const val FAILURE_TITLE = "Flush Failed"
        }
    }

    /**
     * Device Info Constants
     */
    object DeviceInfo {
        const val UNKNOWN_NAME = "Unknown"
        const val UNKNOWN_ID = "Unknown"
    }
}

