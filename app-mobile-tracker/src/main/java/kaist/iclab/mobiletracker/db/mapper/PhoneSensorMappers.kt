package kaist.iclab.mobiletracker.db.mapper

import android.os.BatteryManager
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BluetoothScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DataTrafficSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DeviceModeSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ScreenSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.WifiSensorData
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.mobiletracker.utils.DateTimeFormatter

object AmbientLightMapper : EntityToSupabaseMapper<AmbientLightEntity, AmbientLightSensorData> {
    override fun map(entity: AmbientLightEntity, userUuid: String?): AmbientLightSensorData {
        return AmbientLightSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            value = entity.value,
            received = entity.received,
            accuracy = entity.accuracy
        )
    }
}

object BatteryMapper : EntityToSupabaseMapper<BatteryEntity, BatterySensorData> {
    override fun map(entity: BatteryEntity, userUuid: String?): BatterySensorData {
        val plugged = when (entity.connectedType) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "UNPLUGGED"
        }
        
        val status = when (entity.status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
            else -> "unknown"
        }
        
        return BatterySensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            level = entity.level.toFloat(),
            plugged = plugged,
            status = status,
            temperature = entity.temperature
        )
    }
}

object BluetoothScanMapper : EntityToSupabaseMapper<BluetoothScanEntity, BluetoothScanSensorData> {
    override fun map(entity: BluetoothScanEntity, userUuid: String?): BluetoothScanSensorData {
        return BluetoothScanSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            name = entity.name,
            alias = entity.alias,
            address = entity.address,
            bondState = entity.bondState,
            connectionType = entity.connectionType,
            classType = entity.classType,
            rssi = entity.rssi,
            isLE = entity.isLE
        )
    }
}

object DataTrafficMapper : EntityToSupabaseMapper<DataTrafficEntity, DataTrafficSensorData> {
    override fun map(entity: DataTrafficEntity, userUuid: String?): DataTrafficSensorData {
        return DataTrafficSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            totalRx = entity.totalRx,
            totalTx = entity.totalTx,
            mobileRx = entity.mobileRx,
            mobileTx = entity.mobileTx,
            received = entity.received
        )
    }
}

object DeviceModeMapper : EntityToSupabaseMapper<DeviceModeEntity, DeviceModeSensorData> {
    override fun map(entity: DeviceModeEntity, userUuid: String?): DeviceModeSensorData {
        return DeviceModeSensorData(
            uuid = userUuid,
            received = entity.received,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            eventType = entity.eventType,
            value = entity.value
        )
    }
}

object ScreenMapper : EntityToSupabaseMapper<ScreenEntity, ScreenSensorData> {
    override fun map(entity: ScreenEntity, userUuid: String?): ScreenSensorData {
        return ScreenSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            type = entity.type,
            received = entity.received
        )
    }
}

object WifiMapper : EntityToSupabaseMapper<WifiEntity, WifiSensorData> {
    override fun map(entity: WifiEntity, userUuid: String?): WifiSensorData {
        return WifiSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            bssid = entity.bssid,
            frequency = entity.frequency,
            rssi = entity.level, // level in Entity is rssi in Supabase data
            ssid = entity.ssid,
            received = entity.received
        )
    }
}

