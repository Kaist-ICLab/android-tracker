/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.test_sync.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.test_sync.presentation.theme.AndroidtrackerTheme
import kaist.iclab.tracker.sync.BLESyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class TestData(
    val test: String,
    val test2: Int,
)
class MainActivity : ComponentActivity() {
    private val syncManager = BLESyncManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        syncManager.addOnReceivedListener(listOf("test")) { it: JsonElement ->
            Log.v("test", it.toString())
        }

        syncManager.addOnReceivedListener(listOf("json")) {
            val testData: TestData = Json.decodeFromJsonElement(it)
            Log.v("test2", testData.toString())
        }

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(
                sendText = {
                    CoroutineScope(Dispatchers.IO).launch {
                        syncManager.send(
                            "test",
                            "HELLO"
                        )
                    }
                },
                sendData = {
                    CoroutineScope(Dispatchers.IO).launch {
                        syncManager.send(
                            "test2",
                            TestData(
                                test = "HELLO",
                                test2 = 123
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun WearApp(
    sendText: () -> Unit,
    sendData: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidtrackerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Button(
                    onClick = sendText
                ) {
                    Text("Send Text")
                }

                Button(
                    onClick = sendData
                ) {
                    Text("Send Data")
                }
            }
        }
    }
}