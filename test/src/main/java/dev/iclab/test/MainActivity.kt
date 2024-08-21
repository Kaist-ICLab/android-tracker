package dev.iclab.test

import PermissionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.iclab.test.ui.theme.TrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val permissionManager = PermissionManager(this@MainActivity)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackerTheme {
                MainScreen(permissionManager)
            }
        }

    }
}

@Composable
fun MainScreen(permissionManager: PermissionManager) {
    Button(onClick = {permissionManager}) {
        Text("Request Permission")
    }
}