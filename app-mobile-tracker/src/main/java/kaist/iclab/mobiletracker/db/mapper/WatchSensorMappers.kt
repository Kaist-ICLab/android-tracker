package kaist.iclab.mobiletracker.db.mapper

import kaist.iclab.mobiletracker.data.sensors.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.EDASensorData
import kaist.iclab.mobiletracker.data.sensors.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.WatchLocationEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity
import kaist.iclab.mobiletracker.utils.DateTimeFormatter

object HeartRateMapper : EntityToSupabaseMapper<WatchHeartRateEntity, HeartRateSensorData> {
    override fun map(entity: WatchHeartRateEntity, userUuid: String?): HeartRateSensorData {
        return HeartRateSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            hr = entity.hr,
            hrStatus = entity.hrStatus,
            ibi = entity.ibi,
            ibiStatus = entity.ibiStatus
        )
    }
}

object AccelerometerMapper : EntityToSupabaseMapper<WatchAccelerometerEntity, AccelerometerSensorData> {
    override fun map(entity: WatchAccelerometerEntity, userUuid: String?): AccelerometerSensorData {
        return AccelerometerSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            x = entity.x,
            y = entity.y,
            z = entity.z
        )
    }
}

object EDAMapper : EntityToSupabaseMapper<WatchEDAEntity, EDASensorData> {
    override fun map(entity: WatchEDAEntity, userUuid: String?): EDASensorData {
        return EDASensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            skinConductance = entity.skinConductance,
            status = entity.status
        )
    }
}

object PPGMapper : EntityToSupabaseMapper<WatchPPGEntity, PPGSensorData> {
    override fun map(entity: WatchPPGEntity, userUuid: String?): PPGSensorData {
        return PPGSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            green = entity.green,
            greenStatus = entity.greenStatus,
            red = entity.red,
            redStatus = entity.redStatus,
            ir = entity.ir,
            irStatus = entity.irStatus
        )
    }
}

object SkinTemperatureMapper : EntityToSupabaseMapper<WatchSkinTemperatureEntity, SkinTemperatureSensorData> {
    override fun map(entity: WatchSkinTemperatureEntity, userUuid: String?): SkinTemperatureSensorData {
        return SkinTemperatureSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            ambientTemp = entity.ambientTemp,
            objectTemp = entity.objectTemp,
            status = entity.status
        )
    }
}

object LocationMapper : EntityToSupabaseMapper<WatchLocationEntity, LocationSensorData> {
    override fun map(entity: WatchLocationEntity, userUuid: String?): LocationSensorData {
        return LocationSensorData(
            uuid = userUuid,
            timestamp = DateTimeFormatter.formatTimestamp(entity.timestamp),
            latitude = entity.latitude,
            longitude = entity.longitude,
            altitude = entity.altitude,
            speed = entity.speed,
            accuracy = entity.accuracy
        )
    }
}

