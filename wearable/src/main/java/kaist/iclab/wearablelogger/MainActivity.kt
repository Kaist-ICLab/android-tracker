package kaist.iclab.wearablelogger

import android.os.Bundle
import androidx.activity.compose.setContent
import dev.iclab.tracker.collectors.controller.CollectorController
import dev.iclab.tracker.permission.PermissionActivity
import kaist.iclab.wearablelogger.ui.MainScreen
import org.koin.android.ext.android.get

class MainActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        setupCollector()
    }

    fun setupCollector() {
        val collectorController = get<CollectorController>()
//        collectorController.addCollector(get<PPGGreenCollector>())
//        collectorController.addCollector(get<ACCCollector>())
//        collectorController.addCollector(get<HRCollector>())
//        collectorController.addCollector(get<SkinTempCollector>())

    }
}