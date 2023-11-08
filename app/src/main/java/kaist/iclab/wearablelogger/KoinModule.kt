package kaist.iclab.wearablelogger

import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.collector.PPGGreenCollector
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module{

    single {
        PPGGreenCollector(androidContext())
    }

    single {
        CollectorRepository(
            listOf<AbstractCollector>(
                get<PPGGreenCollector>()
            ),
            androidContext()
        )
    }
}