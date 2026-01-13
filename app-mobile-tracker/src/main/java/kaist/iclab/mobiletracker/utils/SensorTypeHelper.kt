package kaist.iclab.mobiletracker.utils

import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService

/**
 * Utility class for identifying and working with phone and watch sensors.
 * Provides helper methods to determine sensor types and get sensor IDs.
 */
object SensorTypeHelper {
    /**
     * List of all watch sensor IDs
     */
    val watchSensorIds: List<String> = listOf(
        WatchSensorUploadService.HEART_RATE_SENSOR_ID,
        WatchSensorUploadService.ACCELEROMETER_SENSOR_ID,
        WatchSensorUploadService.EDA_SENSOR_ID,
        WatchSensorUploadService.PPG_SENSOR_ID,
        WatchSensorUploadService.SKIN_TEMPERATURE_SENSOR_ID,
        WatchSensorUploadService.LOCATION_SENSOR_ID
    )

    /**
     * Check if a sensor ID belongs to a watch sensor
     * @param sensorId The sensor ID to check
     * @return true if it's a watch sensor, false otherwise
     */
    fun isWatchSensor(sensorId: String): Boolean {
        return watchSensorIds.contains(sensorId)
    }

    /**
     * Check if a sensor ID belongs to a phone sensor
     * Note: This assumes any sensor ID that's not a watch sensor is a phone sensor.
     * For more accurate detection, you may need to check against the actual phone sensor list.
     * @param sensorId The sensor ID to check
     * @return true if it's likely a phone sensor, false otherwise
     */
    fun isPhoneSensor(sensorId: String): Boolean {
        return !isWatchSensor(sensorId)
    }
}

