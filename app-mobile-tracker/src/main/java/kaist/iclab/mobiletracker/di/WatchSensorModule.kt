package kaist.iclab.mobiletracker.di

import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.WatchSensorRepository
import kaist.iclab.mobiletracker.repository.WatchSensorRepositoryImpl
import kaist.iclab.mobiletracker.services.supabase.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.supabase.EDASensorService
import kaist.iclab.mobiletracker.services.supabase.HeartRateSensorService
import kaist.iclab.mobiletracker.services.supabase.LocationSensorService
import kaist.iclab.mobiletracker.services.supabase.PPGSensorService
import kaist.iclab.mobiletracker.services.SensorServiceRegistry
import kaist.iclab.mobiletracker.services.SensorServiceRegistryImpl
import kaist.iclab.mobiletracker.services.supabase.SkinTemperatureSensorService
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val watchSensorModule = module {
    // Watch Sensor Services - inject SupabaseHelper
    single {
        LocationSensorService(supabaseHelper = get())
    }

    single {
        AccelerometerSensorService(supabaseHelper = get())
    }

    single {
        EDASensorService(supabaseHelper = get())
    }

    single {
        HeartRateSensorService(supabaseHelper = get())
    }

    single {
        PPGSensorService(supabaseHelper = get())
    }

    single {
        SkinTemperatureSensorService(supabaseHelper = get())
    }
    
    // Map of sensor IDs to DAOs for storing watch sensor data in Room database
    single<Map<String, BaseDao<*, *>>>(named("watchSensorDaos")) {
        val db = get<TrackerRoomDB>()
        mapOf(
            WatchSensorUploadService.HEART_RATE_SENSOR_ID to db.watchHeartRateDao(),
            WatchSensorUploadService.ACCELEROMETER_SENSOR_ID to db.watchAccelerometerDao(),
            WatchSensorUploadService.EDA_SENSOR_ID to db.watchEDADao(),
            WatchSensorUploadService.PPG_SENSOR_ID to db.watchPPGDao(),
            WatchSensorUploadService.SKIN_TEMPERATURE_SENSOR_ID to db.watchSkinTemperatureDao(),
            WatchSensorUploadService.LOCATION_SENSOR_ID to db.watchLocationDao(),
        )
    }
    
    // WatchSensorRepository - bind interface to implementation
    single<WatchSensorRepository> {
        WatchSensorRepositoryImpl(
            db = get(),
            watchSensorDaos = get<Map<String, BaseDao<*, *>>>(named("watchSensorDaos")),
            supabaseHelper = get()
        )
    }
    
    // Watch sensor service registry
    single<SensorServiceRegistry>(named("watchSensorServiceRegistry")) {
        val accelerometerService = get<AccelerometerSensorService>()
        val edaService = get<EDASensorService>()
        val heartRateService = get<HeartRateSensorService>()
        val locationService = get<LocationSensorService>()
        val ppgService = get<PPGSensorService>()
        val skinTemperatureService = get<SkinTemperatureSensorService>()
        
        SensorServiceRegistryImpl(
            mapOf(
                WatchSensorUploadService.HEART_RATE_SENSOR_ID to heartRateService,
                WatchSensorUploadService.ACCELEROMETER_SENSOR_ID to accelerometerService,
                WatchSensorUploadService.EDA_SENSOR_ID to edaService,
                WatchSensorUploadService.PPG_SENSOR_ID to ppgService,
                WatchSensorUploadService.SKIN_TEMPERATURE_SENSOR_ID to skinTemperatureService,
                WatchSensorUploadService.LOCATION_SENSOR_ID to locationService,
            )
        )
    }
    
    // WatchSensorUploadService - injects all watch sensor services and dependencies
    single {
        WatchSensorUploadService(
            watchSensorDaos = get<Map<String, BaseDao<*, *>>>(named("watchSensorDaos")),
            serviceRegistry = get<SensorServiceRegistry>(named("watchSensorServiceRegistry")),
            supabaseHelper = get(),
            syncTimestampService = SyncTimestampService(androidContext())
        )
    }
}

