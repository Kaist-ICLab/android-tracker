package com.example.mindbattery

import com.example.mindbattery.storage.CouchbaseSensorStateStorage
import com.example.mindbattery.storage.SimpleStateStorage
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
    single {
        AndroidPermissionManager(context = androidContext())
    }

    single {
        CouchbaseDB(context = androidContext())
    }

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

    single {
        AppManager(
            context = androidContext(),
            screenSensor = get<ScreenSensor>()
        )
    }
}
