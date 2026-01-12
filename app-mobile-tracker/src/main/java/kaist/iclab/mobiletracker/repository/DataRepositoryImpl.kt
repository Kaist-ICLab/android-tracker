package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.dao.phone.AmbientLightDao
import kaist.iclab.mobiletracker.db.dao.phone.AppListChangeDao
import kaist.iclab.mobiletracker.db.dao.phone.AppUsageLogDao
import kaist.iclab.mobiletracker.db.dao.phone.BatteryDao
import kaist.iclab.mobiletracker.db.dao.phone.BluetoothScanDao
import kaist.iclab.mobiletracker.db.dao.phone.CallLogDao
import kaist.iclab.mobiletracker.db.dao.phone.ConnectivityDao
import kaist.iclab.mobiletracker.db.dao.phone.DataTrafficDao
import kaist.iclab.mobiletracker.db.dao.phone.DeviceModeDao
import kaist.iclab.mobiletracker.db.dao.phone.MediaDao
import kaist.iclab.mobiletracker.db.dao.phone.MessageLogDao
import kaist.iclab.mobiletracker.db.dao.phone.NotificationDao
import kaist.iclab.mobiletracker.db.dao.phone.ScreenDao
import kaist.iclab.mobiletracker.db.dao.phone.StepDao
import kaist.iclab.mobiletracker.db.dao.phone.UserInteractionDao
import kaist.iclab.mobiletracker.db.dao.phone.WifiScanDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchAccelerometerDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchEDADao
import kaist.iclab.mobiletracker.db.dao.watch.WatchHeartRateDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchPPGDao
import kaist.iclab.mobiletracker.db.dao.watch.WatchSkinTemperatureDao
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.upload.PhoneSensorUploadService
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService
import kaist.iclab.mobiletracker.utils.SensorTypeHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.mobiletracker.repository.Result // Explicit import to avoid ambiguity with kotlin.Result
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of DataRepository that aggregates sensor info from all DAOs.
 */
class DataRepositoryImpl(
    // Phone sensor DAOs
    private val locationDao: LocationDao,
    private val appUsageLogDao: AppUsageLogDao,
    private val stepDao: StepDao,
    private val batteryDao: BatteryDao,
    private val notificationDao: NotificationDao,
    private val screenDao: ScreenDao,
    private val connectivityDao: ConnectivityDao,
    private val bluetoothScanDao: BluetoothScanDao,
    private val ambientLightDao: AmbientLightDao,
    private val appListChangeDao: AppListChangeDao,
    private val callLogDao: CallLogDao,
    private val dataTrafficDao: DataTrafficDao,
    private val deviceModeDao: DeviceModeDao,
    private val mediaDao: MediaDao,
    private val messageLogDao: MessageLogDao,
    private val userInteractionDao: UserInteractionDao,
    private val wifiScanDao: WifiScanDao,
    // Watch sensor DAOs
    private val watchHeartRateDao: WatchHeartRateDao,
    private val watchAccelerometerDao: WatchAccelerometerDao,
    private val watchEDADao: WatchEDADao,
    private val watchPPGDao: WatchPPGDao,
    private val watchSkinTemperatureDao: WatchSkinTemperatureDao,
    // Services for upload and sync
    private val syncTimestampService: SyncTimestampService,
    private val phoneSensorUploadService: PhoneSensorUploadService,
    private val watchSensorUploadService: WatchSensorUploadService,
    private val sensors: List<Sensor<*, *>>
) : DataRepository {
    private val syncingSensors = ConcurrentHashMap<String, Boolean>()

    override suspend fun getAllSensorInfo(): List<SensorInfo> {
        return listOf(
            // Phone sensors (alphabetically ordered)
            SensorInfo(
                sensorId = "AmbientLight",
                displayName = "Ambient Light",
                recordCount = ambientLightDao.getRecordCount(),
                lastRecordedTime = ambientLightDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "AppListChange",
                displayName = "App List Change",
                recordCount = appListChangeDao.getRecordCount(),
                lastRecordedTime = appListChangeDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "AppUsage",
                displayName = "App Usage",
                recordCount = appUsageLogDao.getRecordCount(),
                lastRecordedTime = appUsageLogDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Battery",
                displayName = "Battery",
                recordCount = batteryDao.getRecordCount(),
                lastRecordedTime = batteryDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "BluetoothScan",
                displayName = "Bluetooth Scan",
                recordCount = bluetoothScanDao.getRecordCount(),
                lastRecordedTime = bluetoothScanDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "CallLog",
                displayName = "Call Log",
                recordCount = callLogDao.getRecordCount(),
                lastRecordedTime = callLogDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Connectivity",
                displayName = "Connectivity",
                recordCount = connectivityDao.getRecordCount(),
                lastRecordedTime = connectivityDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "DataTraffic",
                displayName = "Data Traffic",
                recordCount = dataTrafficDao.getRecordCount(),
                lastRecordedTime = dataTrafficDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "DeviceMode",
                displayName = "Device Mode",
                recordCount = deviceModeDao.getRecordCount(),
                lastRecordedTime = deviceModeDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Location",
                displayName = "Location",
                recordCount = locationDao.getRecordCount(),
                lastRecordedTime = locationDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Media",
                displayName = "Media",
                recordCount = mediaDao.getRecordCount(),
                lastRecordedTime = mediaDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "MessageLog",
                displayName = "Message Log",
                recordCount = messageLogDao.getRecordCount(),
                lastRecordedTime = messageLogDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Notification",
                displayName = "Notification",
                recordCount = notificationDao.getRecordCount(),
                lastRecordedTime = notificationDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Screen",
                displayName = "Screen",
                recordCount = screenDao.getRecordCount(),
                lastRecordedTime = screenDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "Step",
                displayName = "Step",
                recordCount = stepDao.getRecordCount(),
                lastRecordedTime = stepDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "UserInteraction",
                displayName = "User Interaction",
                recordCount = userInteractionDao.getRecordCount(),
                lastRecordedTime = userInteractionDao.getLatestTimestamp()
            ),
            SensorInfo(
                sensorId = "WifiScan",
                displayName = "WiFi Scan",
                recordCount = wifiScanDao.getRecordCount(),
                lastRecordedTime = wifiScanDao.getLatestTimestamp()
            ),
            // Watch sensors
            SensorInfo(
                sensorId = "WatchAccelerometer",
                displayName = "Accelerometer",
                recordCount = watchAccelerometerDao.getRecordCount(),
                lastRecordedTime = watchAccelerometerDao.getLatestTimestamp(),
                isWatchSensor = true
            ),
            SensorInfo(
                sensorId = "WatchEDA",
                displayName = "EDA",
                recordCount = watchEDADao.getRecordCount(),
                lastRecordedTime = watchEDADao.getLatestTimestamp(),
                isWatchSensor = true
            ),
            SensorInfo(
                sensorId = "WatchHeartRate",
                displayName = "Heart Rate",
                recordCount = watchHeartRateDao.getRecordCount(),
                lastRecordedTime = watchHeartRateDao.getLatestTimestamp(),
                isWatchSensor = true
            ),
            SensorInfo(
                sensorId = "WatchPPG",
                displayName = "PPG",
                recordCount = watchPPGDao.getRecordCount(),
                lastRecordedTime = watchPPGDao.getLatestTimestamp(),
                isWatchSensor = true
            ),
            SensorInfo(
                sensorId = "WatchSkinTemperature",
                displayName = "Skin Temperature",
                recordCount = watchSkinTemperatureDao.getRecordCount(),
                lastRecordedTime = watchSkinTemperatureDao.getLatestTimestamp(),
                isWatchSensor = true
            )
        ).sortedBy { it.displayName }
    }

    override suspend fun getSensorInfo(sensorId: String): SensorInfo? {
        return getAllSensorInfo().find { it.sensorId == sensorId }
    }
    
    override suspend fun getSensorDetailInfo(sensorId: String): SensorDetailInfo? {
        val info = getSensorInfo(sensorId) ?: return null
        val startOfToday = getStartOfToday()
        
        val todayCount = when (sensorId) {
            "Location" -> locationDao.getRecordCountAfterTimestamp(startOfToday)
            "AppUsage" -> appUsageLogDao.getRecordCountAfterTimestamp(startOfToday)
            "Step" -> stepDao.getRecordCountAfterTimestamp(startOfToday)
            "Battery" -> batteryDao.getRecordCountAfterTimestamp(startOfToday)
            "Notification" -> notificationDao.getRecordCountAfterTimestamp(startOfToday)
            "Screen" -> screenDao.getRecordCountAfterTimestamp(startOfToday)
            "Connectivity" -> connectivityDao.getRecordCountAfterTimestamp(startOfToday)
            "BluetoothScan" -> bluetoothScanDao.getRecordCountAfterTimestamp(startOfToday)
            "AmbientLight" -> ambientLightDao.getRecordCountAfterTimestamp(startOfToday)
            "AppListChange" -> appListChangeDao.getRecordCountAfterTimestamp(startOfToday)
            "CallLog" -> callLogDao.getRecordCountAfterTimestamp(startOfToday)
            "DataTraffic" -> dataTrafficDao.getRecordCountAfterTimestamp(startOfToday)
            "DeviceMode" -> deviceModeDao.getRecordCountAfterTimestamp(startOfToday)
            "Media" -> mediaDao.getRecordCountAfterTimestamp(startOfToday)
            "MessageLog" -> messageLogDao.getRecordCountAfterTimestamp(startOfToday)
            "UserInteraction" -> userInteractionDao.getRecordCountAfterTimestamp(startOfToday)
            "WifiScan" -> wifiScanDao.getRecordCountAfterTimestamp(startOfToday)
            "WatchHeartRate" -> watchHeartRateDao.getRecordCountAfterTimestamp(startOfToday)
            "WatchAccelerometer" -> watchAccelerometerDao.getRecordCountAfterTimestamp(startOfToday)
            "WatchEDA" -> watchEDADao.getRecordCountAfterTimestamp(startOfToday)
            "WatchPPG" -> watchPPGDao.getRecordCountAfterTimestamp(startOfToday)
            "WatchSkinTemperature" -> watchSkinTemperatureDao.getRecordCountAfterTimestamp(startOfToday)
            else -> 0
        }
        
        return SensorDetailInfo(
            sensorId = info.sensorId,
            displayName = info.displayName,
            totalRecords = info.recordCount,
            todayRecords = todayCount,
            lastRecordedTime = info.lastRecordedTime,
            lastSyncTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId),
            isWatchSensor = info.isWatchSensor
        )
    }
    
    override suspend fun getSensorRecords(
        sensorId: String,
        dateFilter: DateFilter,
        sortOrder: SortOrder,
        limit: Int,
        offset: Int
    ): List<SensorRecord> {
        val afterTimestamp = getTimestampForFilter(dateFilter)
        val isAscending = sortOrder == SortOrder.OLDEST_FIRST
        
        return when (sensorId) {
            "Location" -> locationDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Latitude" to String.format("%.6f°", entity.latitude),
                            "Longitude" to String.format("%.6f°", entity.longitude),
                            "Accuracy" to String.format("%.1f m", entity.accuracy)
                        )
                    )
                }
            "AppUsage" -> appUsageLogDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Package" to entity.packageName,
                            "Event" to entity.eventType.toString()
                        )
                    )
                }
            "Step" -> stepDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Steps" to entity.steps.toString()
                        )
                    )
                }
            "Battery" -> batteryDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Level" to "${entity.level}%",
                            "Status" to entity.status.toString()
                        )
                    )
                }
            "Notification" -> notificationDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Package" to entity.packageName,
                            "Title" to entity.title
                        )
                    )
                }
            "Screen" -> screenDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Type" to entity.type
                        )
                    )
                }
            "Connectivity" -> connectivityDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Network" to entity.networkType,
                            "Connected" to entity.isConnected.toString()
                        )
                    )
                }
            "BluetoothScan" -> bluetoothScanDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Name" to (entity.name ?: "Unknown"),
                            "Address" to entity.address
                        )
                    )
                }
            "AmbientLight" -> ambientLightDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Value" to String.format("%.1f lux", entity.value)
                        )
                    )
                }
            "AppListChange" -> appListChangeDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Changed" to (entity.changedAppJson?.take(50) ?: "N/A")
                        )
                    )
                }
            "CallLog" -> callLogDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Type" to entity.type.toString(),
                            "Duration" to "${entity.duration}s"
                        )
                    )
                }
            "DataTraffic" -> dataTrafficDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Total Rx" to "${entity.totalRx / 1024} KB",
                            "Total Tx" to "${entity.totalTx / 1024} KB"
                        )
                    )
                }
            "DeviceMode" -> deviceModeDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Event" to entity.eventType,
                            "Value" to entity.value.toString()
                        )
                    )
                }
            "Media" -> mediaDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Type" to entity.mediaType,
                            "File" to (entity.fileName ?: "Unknown")
                        )
                    )
                }
            "MessageLog" -> messageLogDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Type" to entity.messageType.toString(),
                            "Contact" to entity.contactType.toString()
                        )
                    )
                }
            "UserInteraction" -> userInteractionDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Package" to entity.packageName,
                            "Event" to entity.eventType.toString()
                        )
                    )
                }
            "WifiScan" -> wifiScanDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id.toLong(),
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "SSID" to (entity.ssid ?: "Hidden"),
                            "Level" to "${entity.level} dBm"
                        )
                    )
                }
            "WatchHeartRate" -> watchHeartRateDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Heart Rate" to "${entity.hr} BPM",
                            "Status" to entity.hrStatus.toString()
                        )
                    )
                }
            "WatchAccelerometer" -> watchAccelerometerDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "X" to String.format("%.3f", entity.x),
                            "Y" to String.format("%.3f", entity.y),
                            "Z" to String.format("%.3f", entity.z)
                        )
                    )
                }
            "WatchEDA" -> watchEDADao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "EDA" to String.format("%.3f μS", entity.skinConductance)
                        )
                    )
                }
            "WatchPPG" -> watchPPGDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Green" to entity.green.toString(),
                            "IR" to entity.ir.toString()
                        )
                    )
                }
            "WatchSkinTemperature" -> watchSkinTemperatureDao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
                .map { entity ->
                    SensorRecord(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        fields = mapOf(
                            "Skin Temp" to String.format("%.1f°C", entity.objectTemp)
                        )
                    )
                }
            else -> emptyList()
        }
    }
    
    override suspend fun getSensorRecordCount(sensorId: String, dateFilter: DateFilter): Int {
        val afterTimestamp = getTimestampForFilter(dateFilter)
        
        return when (sensorId) {
            "Location" -> locationDao.getRecordCountAfterTimestamp(afterTimestamp)
            "AppUsage" -> appUsageLogDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Step" -> stepDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Battery" -> batteryDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Notification" -> notificationDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Screen" -> screenDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Connectivity" -> connectivityDao.getRecordCountAfterTimestamp(afterTimestamp)
            "BluetoothScan" -> bluetoothScanDao.getRecordCountAfterTimestamp(afterTimestamp)
            "AmbientLight" -> ambientLightDao.getRecordCountAfterTimestamp(afterTimestamp)
            "AppListChange" -> appListChangeDao.getRecordCountAfterTimestamp(afterTimestamp)
            "CallLog" -> callLogDao.getRecordCountAfterTimestamp(afterTimestamp)
            "DataTraffic" -> dataTrafficDao.getRecordCountAfterTimestamp(afterTimestamp)
            "DeviceMode" -> deviceModeDao.getRecordCountAfterTimestamp(afterTimestamp)
            "Media" -> mediaDao.getRecordCountAfterTimestamp(afterTimestamp)
            "MessageLog" -> messageLogDao.getRecordCountAfterTimestamp(afterTimestamp)
            "UserInteraction" -> userInteractionDao.getRecordCountAfterTimestamp(afterTimestamp)
            "WifiScan" -> wifiScanDao.getRecordCountAfterTimestamp(afterTimestamp)
            "WatchHeartRate" -> watchHeartRateDao.getRecordCountAfterTimestamp(afterTimestamp)
            "WatchAccelerometer" -> watchAccelerometerDao.getRecordCountAfterTimestamp(afterTimestamp)
            "WatchEDA" -> watchEDADao.getRecordCountAfterTimestamp(afterTimestamp)
            "WatchPPG" -> watchPPGDao.getRecordCountAfterTimestamp(afterTimestamp)
            "WatchSkinTemperature" -> watchSkinTemperatureDao.getRecordCountAfterTimestamp(afterTimestamp)
            else -> 0
        }
    }
    
    override suspend fun deleteAllSensorData(sensorId: String) {
        when (sensorId) {
            "Location" -> locationDao.deleteAll()
            "AppUsage" -> appUsageLogDao.deleteAll()
            "Step" -> stepDao.deleteAll()
            "Battery" -> batteryDao.deleteAll()
            "Notification" -> notificationDao.deleteAll()
            "Screen" -> screenDao.deleteAll()
            "Connectivity" -> connectivityDao.deleteAll()
            "BluetoothScan" -> bluetoothScanDao.deleteAll()
            "AmbientLight" -> ambientLightDao.deleteAll()
            "AppListChange" -> appListChangeDao.deleteAll()
            "CallLog" -> callLogDao.deleteAll()
            "DataTraffic" -> dataTrafficDao.deleteAll()
            "DeviceMode" -> deviceModeDao.deleteAll()
            "Media" -> mediaDao.deleteAll()
            "MessageLog" -> messageLogDao.deleteAll()
            "UserInteraction" -> userInteractionDao.deleteAll()
            "WifiScan" -> wifiScanDao.deleteAll()
            "WatchHeartRate" -> watchHeartRateDao.deleteAll()
            "WatchAccelerometer" -> watchAccelerometerDao.deleteAll()
            "WatchEDA" -> watchEDADao.deleteAll()
            "WatchPPG" -> watchPPGDao.deleteAll()
            "WatchSkinTemperature" -> watchSkinTemperatureDao.deleteAll()
        }
        // Clear sync timestamp when data is deleted
        syncTimestampService.clearLastSuccessfulUpload(sensorId)
    }
    
    override suspend fun uploadSensorData(sensorId: String): Int {
        if (syncingSensors.putIfAbsent(sensorId, true) != null) {
            // Already syncing
            return -2 // Using -2 to indicate already in progress
        }
        
        try {
            // Watch sensor check
            if (SensorTypeHelper.isWatchSensor(sensorId)) {
                if (!watchSensorUploadService.hasDataToUpload(sensorId)) {
                    return 0
                }
                return when (watchSensorUploadService.uploadSensorData(sensorId)) {
                    is Result.Success -> 1
                    is Result.Error -> -1
                }
            }
            
            // Phone sensor
            val sensor = sensors.firstOrNull { it.id == sensorId } ?: return -1
            if (!phoneSensorUploadService.hasDataToUpload(sensorId, sensor)) {
                return 0
            }
            return when (phoneSensorUploadService.uploadSensorData(sensorId, sensor)) {
                is Result.Success -> 1
                is Result.Error -> -1
            }
        } finally {
            syncingSensors.remove(sensorId)
        }
    }
    
    override suspend fun getLastSyncTimestamp(sensorId: String): Long? {
        return syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId)
    }

    override suspend fun uploadAllData(): Int {
        val allSensors = getAllSensorInfo()
        var successCount = 0
        for (sensor in allSensors) {
            try {
                val result = uploadSensorData(sensor.sensorId)
                if (result > 0) {
                    successCount++
                }
            } catch (e: Exception) {
                // Continue to next sensor
            }
        }
        return successCount
    }

    override suspend fun deleteAllAllData() {
        val allSensors = getAllSensorInfo()
        for (sensor in allSensors) {
            try {
                deleteAllSensorData(sensor.sensorId)
            } catch (e: Exception) {
                // Continue to next sensor
            }
        }
    }

    override suspend fun deleteRecord(sensorId: String, recordId: Long) {
        when (sensorId) {
            "Location" -> locationDao.deleteById(recordId)
            "AppUsage" -> appUsageLogDao.deleteById(recordId)
            "Step" -> stepDao.deleteById(recordId)
            "Battery" -> batteryDao.deleteById(recordId)
            "Notification" -> notificationDao.deleteById(recordId)
            "Screen" -> screenDao.deleteById(recordId)
            "Connectivity" -> connectivityDao.deleteById(recordId)
            "BluetoothScan" -> bluetoothScanDao.deleteById(recordId)
            "AmbientLight" -> ambientLightDao.deleteById(recordId)
            "AppListChange" -> appListChangeDao.deleteById(recordId)
            "CallLog" -> callLogDao.deleteById(recordId)
            "DataTraffic" -> dataTrafficDao.deleteById(recordId)
            "DeviceMode" -> deviceModeDao.deleteById(recordId)
            "Media" -> mediaDao.deleteById(recordId)
            "MessageLog" -> messageLogDao.deleteById(recordId)
            "UserInteraction" -> userInteractionDao.deleteById(recordId)
            "WifiScan" -> wifiScanDao.deleteById(recordId)
            "WatchHeartRate" -> watchHeartRateDao.deleteById(recordId)
            "WatchAccelerometer" -> watchAccelerometerDao.deleteById(recordId)
            "WatchEDA" -> watchEDADao.deleteById(recordId)
            "WatchPPG" -> watchPPGDao.deleteById(recordId)
            "WatchSkinTemperature" -> watchSkinTemperatureDao.deleteById(recordId)
        }
    }
    
    private fun getStartOfToday(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getTimestampForFilter(dateFilter: DateFilter): Long {
        val calendar = java.util.Calendar.getInstance()
        return when (dateFilter) {
            DateFilter.TODAY -> {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            DateFilter.LAST_7_DAYS -> {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            DateFilter.LAST_30_DAYS -> {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }
            DateFilter.ALL_TIME -> 0L
        }
    }
}
