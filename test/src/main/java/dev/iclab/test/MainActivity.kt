package dev.iclab.test

import dev.iclab.tracker.PermissionManager
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.iclab.test.ui.theme.TrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val permissionManager = dev.iclab.tracker.PermissionManager(this@MainActivity)

        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            TrackerTheme {
                MainScreen(permissionManager)
            }
        }

    }
}

@Composable
fun MainScreen(permissionManager: dev.iclab.tracker.PermissionManager) {
    Box(
        modifier = Modifier.padding(16.dp).fillMaxSize()
    ) {
        Button(onClick = {permissionManager.request(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ){ grantedMap ->
            grantedMap.forEach { t, u ->
                Log.d("Permission", "$t, $u")
            }
        }}) {
            Text("Request Permission")
        }
    }
}