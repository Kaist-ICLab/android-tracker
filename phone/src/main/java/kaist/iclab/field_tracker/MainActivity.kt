package kaist.iclab.field_tracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kaist.iclab.field_tracker.ui.MainApp
import kaist.iclab.field_tracker.ui.theme.Gray50
import kaist.iclab.tracker.permission.PermissionActivity
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinAndroidContext {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Gray50
                ) {
                    MainApp(
                        navController = rememberNavController(),
                        viewModel = koinViewModel())
                }
            }
        }
    }
}
