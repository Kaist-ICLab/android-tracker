package com.example.test_sync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.test_sync.ui.theme.AndroidtrackerTheme
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
        super.onCreate(savedInstanceState)
        syncManager.addOnReceivedListener(listOf("test")) { it: JsonElement ->
            Log.v("test", it.toString())
        }

        syncManager.addOnReceivedListener(listOf("test2")) {
            val testData: TestData = Json.decodeFromJsonElement(it)
            Log.v("test2", testData.toString())
        }

        enableEdgeToEdge()
        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    syncManager.send(
                                        "test",
                                        "HELLO7"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Text")
                        }

                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    syncManager.send(
                                        "test2",
                                        TestData(
                                            test = "HELLO",
                                            test2 = 123
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Data")
                        }

                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    syncManager.send(
                                        "test2",
                                        TestData(
                                            test = "Bye",
                                            test2 = 456
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Data 2")
                        }
                    }
                }
            }
        }
    }
}