package dev.iclab.tracker.collectors

import android.Manifest
import android.content.Context
import android.util.Log
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.triggers.AlarmTrigger

class TestCollector(
    override val context: Context,
    override val database: DatabaseInterface
): AbstractCollector(
    context, database
) {
    companion object {
        const val TAG = "TestCollector"
        const val ACTION_TEST_REQUEST =
            "dev.iclab.tracker.ACTION_TEST_REQUEST"
        const val ACTION_CODE_TEST_REQUEST = 0x1
    }

    override val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG
    )

    lateinit var alarmTrigger: AlarmTrigger

    override fun isAvailable():Boolean = true

    fun listener() {
        Log.d(TAG, "listener")
        val queriedTime = System.currentTimeMillis()
        database.insert("test", mapOf("time" to queriedTime))
    }
    override suspend fun enable() {
        Log.d(TAG, "enable")
    }
    override fun start() {
        Log.d(TAG, "start")
        alarmTrigger = AlarmTrigger(
            context,
            ACTION_TEST_REQUEST,
            ACTION_CODE_TEST_REQUEST,
            60000L
        ) {
            listener()
        }
        alarmTrigger.register()
    }
    override fun stop() {
        Log.d(TAG, "stop")
        alarmTrigger.unregister()
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}