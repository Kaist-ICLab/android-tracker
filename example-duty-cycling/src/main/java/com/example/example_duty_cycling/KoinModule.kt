package com.example.example_duty_cycling

import com.example.example_duty_cycling.storage.CouchbaseSensorStateStorage
import com.example.example_duty_cycling.storage.SimpleStateStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB

val koinModule = module {
    // Core dependencies
    single {
        AndroidPermissionManager(context = androidContext())
    }
    
    // CouchbaseDB for storage
    single {
        CouchbaseDB(context = androidContext())
    }
    
    // ScreenSensor
    single {
        ScreenSensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(ScreenSensor.Config()),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get<CouchbaseDB>(),
                collectionName = ScreenSensor::class.simpleName ?: ""
            )
        )
    }
    
    // AppManager with ScreenSensor dependency
    single {
        AppManager(
            context = androidContext(),
            screenSensor = get<ScreenSensor>()
        )
    }
}
