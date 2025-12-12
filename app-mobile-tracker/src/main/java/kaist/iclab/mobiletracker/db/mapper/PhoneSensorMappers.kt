package kaist.iclab.mobiletracker.db.mapper

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

object AmbientLightMapper : EntityToSupabaseMapper<AmbientLightEntity, AmbientLightSensorData> {
    override fun map(entity: AmbientLightEntity, userUuid: String?): AmbientLightSensorData {
        return AmbientLightSensorData(
            uuid = userUuid,
            deviceType = "PHONE",
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
            deviceType = "PHONE",
            timestamp = entity.timestamp,
            level = entity.level,
            connectedType = entity.connectedType,
            status = entity.status,
            temperature = entity.temperature,
            received = entity.received
        )
    }
}

object BluetoothScanMapper : EntityToSupabaseMapper<BluetoothScanEntity, BluetoothScanSensorData> {
    override fun map(entity: BluetoothScanEntity, userUuid: String?): BluetoothScanSensorData {
        return BluetoothScanSensorData(
            uuid = userUuid,
            deviceType = "PHONE",
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
            deviceType = "PHONE",
            timestamp = entity.timestamp,
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
            deviceType = "PHONE",
            received = entity.received,
            timestamp = entity.timestamp,
            eventType = entity.eventType,
            value = entity.value
        )
    }
}

object ScreenMapper : EntityToSupabaseMapper<ScreenEntity, ScreenSensorData> {
    override fun map(entity: ScreenEntity, userUuid: String?): ScreenSensorData {
        return ScreenSensorData(
            uuid = userUuid,
            deviceType = "PHONE",
            timestamp = entity.timestamp,
            type = entity.type,
            received = entity.received
        )
    }
}

object WifiMapper : EntityToSupabaseMapper<WifiEntity, WifiSensorData> {
    override fun map(entity: WifiEntity, userUuid: String?): WifiSensorData {
        return WifiSensorData(
            uuid = userUuid,
            deviceType = "PHONE",
            timestamp = entity.timestamp,
            bssid = entity.bssid,
            frequency = entity.frequency,
            level = entity.level,
            ssid = entity.ssid,
            received = entity.received
        )
    }
}

