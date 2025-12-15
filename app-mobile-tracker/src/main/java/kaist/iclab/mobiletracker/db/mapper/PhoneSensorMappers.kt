package kaist.iclab.mobiletracker.db.mapper

import kaist.iclab.mobiletracker.data.DeviceType
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BluetoothScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.CallLogSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DataTrafficSensorData
import kaist.iclab.mobiletracker.data.sensors.common.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DeviceModeSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ScreenSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.AppListChangeSensorData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kaist.iclab.mobiletracker.data.sensors.phone.WifiScanSensorData
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.LocationEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiScanEntity

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
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            duration = entity.duration,
            number = entity.number,
            type = entity.type,
            received = entity.received
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
            deviceType = DeviceType.PHONE.value,
            received = entity.received,
            timestamp = entity.timestamp,
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
            deviceType = DeviceType.PHONE.value,
            timestamp = entity.timestamp,
            type = entity.type,
            received = entity.received
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

