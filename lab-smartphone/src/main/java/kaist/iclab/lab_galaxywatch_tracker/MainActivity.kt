package kaist.iclab.lab_galaxywatch_tracker

import android.os.Bundle
import androidx.activity.compose.setContent
import kaist.iclab.tracker.collectors.controller.CollectorController
import kaist.iclab.tracker.permission.PermissionActivity
import kaist.iclab.lab_galaxywatch_tracker.data.WearableDataCollector
import kaist.iclab.lab_galaxywatch_tracker.ui.MainScreen
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
        collectorController.addCollector(get<WearableDataCollector>())
    }
}


