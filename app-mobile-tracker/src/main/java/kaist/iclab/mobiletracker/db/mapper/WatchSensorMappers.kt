package kaist.iclab.mobiletracker.db.mapper

import kaist.iclab.mobiletracker.data.DeviceType
import kaist.iclab.mobiletracker.data.sensors.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.EDASensorData
import kaist.iclab.mobiletracker.data.sensors.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.sensors.common.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.LocationEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity

object HeartRateMapper : EntityToSupabaseMapper<WatchHeartRateEntity, HeartRateSensorData> {
    override fun map(entity: WatchHeartRateEntity, userUuid: String?): HeartRateSensorData {
        return HeartRateSensorData(
            uuid = userUuid,
            deviceType = DeviceType.WATCH.value,
            timestamp = entity.timestamp,
            hr = entity.hr,
            hrStatus = entity.hrStatus,
            ibi = entity.ibi,
            ibiStatus = entity.ibiStatus,
            received = entity.received
        )
    }
}

object AccelerometerMapper : EntityToSupabaseMapper<WatchAccelerometerEntity, AccelerometerSensorData> {
    override fun map(entity: WatchAccelerometerEntity, userUuid: String?): AccelerometerSensorData {
        return AccelerometerSensorData(
            uuid = userUuid,
            deviceType = DeviceType.WATCH.value,
            timestamp = entity.timestamp,
            x = entity.x,
            y = entity.y,
            z = entity.z,
            received = entity.received
        )
    }
}

object EDAMapper : EntityToSupabaseMapper<WatchEDAEntity, EDASensorData> {
    override fun map(entity: WatchEDAEntity, userUuid: String?): EDASensorData {
        return EDASensorData(
            uuid = userUuid,
            deviceType = DeviceType.WATCH.value,
            timestamp = entity.timestamp,
            skinConductance = entity.skinConductance,
            status = entity.status,
            received = entity.received
        )
    }
}

object PPGMapper : EntityToSupabaseMapper<WatchPPGEntity, PPGSensorData> {
    override fun map(entity: WatchPPGEntity, userUuid: String?): PPGSensorData {
        return PPGSensorData(
            uuid = userUuid,
            deviceType = DeviceType.WATCH.value,
            timestamp = entity.timestamp,
            green = entity.green,
            greenStatus = entity.greenStatus,
            red = entity.red,
            redStatus = entity.redStatus,
            ir = entity.ir,
            irStatus = entity.irStatus,
            received = entity.received
        )
    }
}

object SkinTemperatureMapper : EntityToSupabaseMapper<WatchSkinTemperatureEntity, SkinTemperatureSensorData> {
    override fun map(entity: WatchSkinTemperatureEntity, userUuid: String?): SkinTemperatureSensorData {
        return SkinTemperatureSensorData(
            uuid = userUuid,
            deviceType = DeviceType.WATCH.value,
            timestamp = entity.timestamp,
            ambientTemp = entity.ambientTemp,
            objectTemp = entity.objectTemp,
            status = entity.status,
            received = entity.received
        )
    }
}

object LocationMapper : EntityToSupabaseMapper<LocationEntity, LocationSensorData> {
    override fun map(entity: LocationEntity, userUuid: String?): LocationSensorData {
        return LocationSensorData(
            uuid = userUuid,
            deviceType = entity.deviceType,
            timestamp = entity.timestamp,
            latitude = entity.latitude,
            longitude = entity.longitude,
            altitude = entity.altitude,
            speed = entity.speed,
            accuracy = entity.accuracy,
            received = entity.received
        )
    }
}

