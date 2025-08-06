package com.example.sensor_test_app.storage

import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB
import kaist.iclab.tracker.storage.couchbase.CouchbaseStateStorage

class CouchbaseSensorStateStorage(
    couchbase: CouchbaseDB,
    collectionName: String,
) : CouchbaseStateStorage<SensorState>(
    couchbase = couchbase,
    defaultVal = SensorState(SensorState.FLAG.UNAVAILABLE),
    clazz = SensorState::class.java,
    collectionName = collectionName
)