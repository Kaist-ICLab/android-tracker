package kaist.iclab.wearablelogger

import android.app.Application
import android.util.Log
import kaist.iclab.wearablelogger.db.TestDao
import kaist.iclab.wearablelogger.db.TestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WearableLoggerApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WearableLoggerApplication)
            androidLogger(level = Level.NONE)
            modules(koinModule)
        }


        val testDao = get<TestDao>()
        CoroutineScope(Dispatchers.IO).launch {
            testDao.insertTestEvent(TestEntity(timestamp = System.currentTimeMillis()))

            testDao.queryTestEvent(0L).collect{
                Log.d("TEST", it.toString())
            }
            Log.d("TEST", "Here?")

        }

    }
}