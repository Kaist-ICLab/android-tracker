package kaist.iclab.tracker.collectors

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.filters.Filter
import kaist.iclab.tracker.triggers.AlarmTrigger

class TestCollector(
    override val context: Context,
    override val database: DatabaseInterface
): AbstractCollector(
    context, database
) {
    companion object {
        const val NAME = "TEST"
        const val ACTION_TEST_REQUEST =
            "dev.iclab.tracker.ACTION_TEST_REQUEST"
        const val ACTION_CODE_TEST_REQUEST = 0x1
    }

    override val permissions: Array<String> = arrayOf()
    override val NAME: String
        get() = Companion.NAME
    override val filters: MutableList<Filter> = mutableListOf()
    override val TAG: String
        get() = super.TAG

    lateinit var alarmTrigger: AlarmTrigger

    override fun isAvailable():Boolean = true

    fun listener() {
        Log.d(TAG, "listener")
        val queriedTime = System.currentTimeMillis()
        database.insert("test", mapOf("time" to queriedTime))
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

}