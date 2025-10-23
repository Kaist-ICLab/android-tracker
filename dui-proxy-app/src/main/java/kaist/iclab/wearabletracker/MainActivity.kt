package kaist.iclab.wearabletracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.wearabletracker.ui.theme.AndroidtrackerTheme

class MainActivity : ComponentActivity() {
    private val permissionManager = AndroidPermissionManager(this)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startService() {
        val serviceIntent = Intent(this, DataProxyService::class.java)
        val res = this.startForegroundService(serviceIntent)
        Log.d("MainActivity", "startService: $res")
    }

    private fun stopService() {
        val serviceIntent = Intent(this, DataProxyService::class.java)
        this.stopService(serviceIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager.bind(this)

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        startService = {
                            Log.d("MainActivity", "Click")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ActivityCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    permissionManager.request(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                                }
                            }
                            startService()
                        },
                        stopService = { stopService() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    startService: () -> Unit,
    stopService: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Button(
            onClick = startService,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start data transfer")
        }

        Button(
            onClick = stopService,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop data transfer")
        }
    }

}