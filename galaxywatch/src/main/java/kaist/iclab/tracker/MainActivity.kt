package kaist.iclab.tracker

import android.os.Bundle
import androidx.activity.compose.setContent
import kaist.iclab.tracker.permission.PermissionActivity
import kaist.iclab.tracker.ui.MainScreen

class MainActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}