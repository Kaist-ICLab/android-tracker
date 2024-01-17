package kaist.iclab.wearablelogger.collector.Test

import android.util.Log
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TestCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val testDao: TestDao,
): AbstractCollector {

    override val TAG = javaClass.simpleName
    private var job: Job? = null

    override fun setup() {
        Log.d(TAG, "setup()")
    }
    override fun startLogging() {
        Log.d(TAG, "startLogging()")
        if(job== null){
            job = CoroutineScope(Dispatchers.IO).launch{
                while(true){
                    delay(TimeUnit.SECONDS.toMillis(5))
                    testDao.insertTestEvent(TestEntity(timestamp = System.currentTimeMillis()))
                }
            }
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging()")
        job?.cancel()
        job = null
    }
    override fun flush() {
    }
}