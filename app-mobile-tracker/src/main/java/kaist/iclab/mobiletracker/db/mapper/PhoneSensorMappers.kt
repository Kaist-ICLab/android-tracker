package kaist.iclab.mobiletracker.db.mapper

import kaist.iclab.mobiletracker.data.DeviceType
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BluetoothScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.CallLogSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.MessageLogSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.UserInteractionSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DataTrafficSensorData
import kaist.iclab.mobiletracker.data.sensors.common.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DeviceModeSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ScreenSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.AppListChangeSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.AppUsageLogSensorData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kaist.iclab.mobiletracker.data.sensors.phone.WifiScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ConnectivitySensorData
import kaist.iclab.mobiletracker.db.entity.phone.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.phone.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.phone.AppUsageLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.phone.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.phone.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.MessageLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.UserInteractionEntity
import kaist.iclab.mobiletracker.db.entity.phone.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.phone.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.common.LocationEntity
import kaist.iclab.mobiletracker.db.entity.phone.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.phone.WifiScanEntity
import kaist.iclab.mobiletracker.db.entity.phone.ConnectivityEntity

object AmbientLightMapper : EntityToSupabaseMapper<AmbientLightEntity, AmbientLightSensorData> {
    override fun map(entity: AmbientLightEntity, userUuid: String?): AmbientLightSensorData {
        return AmbientLightSensorData(
            uuid = userUuid,
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            value = entity.value,
            received = entity.received,
            accuracy = entity.accuracy
        )
    }
}

object BatteryMapper : EntityToSupabaseMapper<BatteryEntity, BatterySensorData> {
    override fun map(entity: BatteryEntity, userUuid: String?): BatterySensorData {
        return BatterySensorData(
            uuid = userUuid,
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            level = entity.level,
            connectedType = entity.connectedType,
            status = entity.status,
            temperature = entity.temperature,
            received = entity.received
        )
    }
}

object CallLogMapper : EntityToSupabaseMapper<CallLogEntity, CallLogSensorData> {
    override fun map(entity: CallLogEntity, userUuid: String?): CallLogSensorData {
        return CallLogSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            duration = entity.duration,
            number = entity.number,
            type = entity.type
        )
    }
}

object MessageLogMapper : EntityToSupabaseMapper<MessageLogEntity, MessageLogSensorData> {
    override fun map(entity: MessageLogEntity, userUuid: String?): MessageLogSensorData {
        return MessageLogSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            number = entity.number,
            messageType = entity.messageType,
            contactType = entity.contactType
        )
    }
}

object UserInteractionMapper : EntityToSupabaseMapper<UserInteractionEntity, UserInteractionSensorData> {
    override fun map(entity: UserInteractionEntity, userUuid: String?): UserInteractionSensorData {
        return UserInteractionSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            packageName = entity.packageName,
            className = entity.className,
            eventType = entity.eventType,
            text = entity.text
        )
    }
}

object BluetoothScanMapper : EntityToSupabaseMapper<BluetoothScanEntity, BluetoothScanSensorData> {
    override fun map(entity: BluetoothScanEntity, userUuid: String?): BluetoothScanSensorData {
        return BluetoothScanSensorData(
            uuid = userUuid,
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            name = entity.name,
            alias = entity.alias,
            address = entity.address,
            bondState = entity.bondState,
            connectionType = entity.connectionType,
            classType = entity.classType,
            rssi = entity.rssi,
            isLE = entity.isLE,
            received = entity.received
        )
    }
}

object DataTrafficMapper : EntityToSupabaseMapper<DataTrafficEntity, DataTrafficSensorData> {
    override fun map(entity: DataTrafficEntity, userUuid: String?): DataTrafficSensorData {
        return DataTrafficSensorData(
            uuid = userUuid,
            received = entity.received,
            timestamp = entity.timestamp,
            deviceType = DeviceType.PHONE.value,
            totalRx = entity.totalRx,
            totalTx = entity.totalTx,
            mobileRx = entity.mobileRx,
            mobileTx = entity.mobileTx
        )
    }
}

object DeviceModeMapper : EntityToSupabaseMapper<DeviceModeEntity, DeviceModeSensorData> {
    override fun map(entity: DeviceModeEntity, userUuid: String?): DeviceModeSensorData {
        return DeviceModeSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            eventType = entity.eventType,
            value = entity.value
        )
    }
}

object PhoneLocationMapper : EntityToSupabaseMapper<LocationEntity, LocationSensorData> {
    override fun map(entity: LocationEntity, userUuid: String?): LocationSensorData {
        return LocationSensorData(
            uuid = userUuid,
            deviceType = entity.deviceType, // Should be 0 for phone
            timestamp = entity.timestamp,
            received = entity.received,
            accuracy = entity.accuracy,
            altitude = entity.altitude,
            latitude = entity.latitude,
            longitude = entity.longitude,
            speed = entity.speed
        )
    }
}

object ScreenMapper : EntityToSupabaseMapper<ScreenEntity, ScreenSensorData> {
    override fun map(entity: ScreenEntity, userUuid: String?): ScreenSensorData {
        return ScreenSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            type = entity.type
        )
    }
}

object WifiMapper : EntityToSupabaseMapper<WifiScanEntity, WifiScanSensorData> {
    override fun map(entity: WifiScanEntity, userUuid: String?): WifiScanSensorData {
        return WifiScanSensorData(
            uuid = userUuid,
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            bssid = entity.bssid,
            frequency = entity.frequency,
            level = entity.level,
            ssid = entity.ssid,
            received = entity.received
        )
    }
}

object ConnectivityMapper : EntityToSupabaseMapper<ConnectivityEntity, ConnectivitySensorData> {
    override fun map(entity: ConnectivityEntity, userUuid: String?): ConnectivitySensorData {
        val transportList = entity.transportTypes.split(",").filter { it.isNotBlank() }
        return ConnectivitySensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            isConnected = entity.isConnected,
            hasInternet = entity.hasInternet,
            networkType = entity.networkType,
            transportTypes = transportList
        )
    }
}

object AppListChangeMapper : EntityToSupabaseMapper<AppListChangeEntity, AppListChangeSensorData> {
    private val json = Json { ignoreUnknownKeys = true }
    
    override fun map(entity: AppListChangeEntity, userUuid: String?): AppListChangeSensorData {
        // Parse JSON strings to JsonElement so Supabase stores them as native JSON (not stringified)
        val changedApp: JsonElement? = entity.changedAppJson?.let { jsonString ->
            try {
                json.parseToJsonElement(jsonString)
            } catch (e: Exception) {
                null
            }
        }
        
        val appList: JsonElement? = entity.appListJson?.let { jsonString ->
            try {
                json.parseToJsonElement(jsonString)
            } catch (e: Exception) {
                null
            }
        }
        
        return AppListChangeSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            changedApp = changedApp,
            appList = appList
        )
    }
}

object AppUsageLogMapper : EntityToSupabaseMapper<AppUsageLogEntity, AppUsageLogSensorData> {
    override fun map(entity: AppUsageLogEntity, userUuid: String?): AppUsageLogSensorData {
        return AppUsageLogSensorData(
            uuid = userUuid,
            timestamp = entity.timestamp,
            received = entity.received,
            deviceType = DeviceType.PHONE.value,
            packageName = entity.packageName,
            installedBy = entity.installedBy,
            eventType = entity.eventType
        )
    }
}

