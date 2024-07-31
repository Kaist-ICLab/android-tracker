package dev.iclab.tracker.database

import android.util.Log
import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.collectors.TestCollector

class DatabaseFakeImpl : DatabaseInterface {
    val TAG = javaClass.simpleName
    override fun insert(data: String) {
        Log.d(TAG, "insert: $data")
    }

    override fun getCollectorConfig(): List<Class<out AbstractCollector>> {
        return listOf(
            TestCollector::class.java
        )
    }
}