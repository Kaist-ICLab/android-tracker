package com.example.test_sync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.test_sync.helpers.BLEHelper
import com.example.test_sync.helpers.InternetHelper
import com.example.test_sync.helpers.SupabaseHelper
import com.example.test_sync.ui.theme.AndroidtrackerTheme
import kotlinx.serialization.Serializable

@Serializable
data class TestData(
    val message: String,
    val value: Int,
)

class MainActivity : ComponentActivity() {
    // Helper classes for cleaner separation
    private lateinit var bleHelper: BLEHelper
    private lateinit var internetHelper: InternetHelper
    private lateinit var supabaseHelper: SupabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize helpers
        bleHelper = BLEHelper(this)
        bleHelper.initialize()

        internetHelper = InternetHelper()
        supabaseHelper = SupabaseHelper()

        setContent {
            AndroidtrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        // BLE Communication
                        sendStringOverBLE = bleHelper::sendString,
                        sendTestDataOverBLE = bleHelper::sendTestData,
                        sendUrgentBLE = bleHelper::sendUrgent,

                        // Internet Communication
                        sendGetRequest = internetHelper::sendGetRequest,
                        sendPostRequest = internetHelper::sendPostRequest,

                        // Supabase Communication
                        sendToSupabase = supabaseHelper::sendData,
                        sendTestDataToSupabase = supabaseHelper::sendTestData,
                        getFromSupabase = supabaseHelper::getData,

                        // Style Modifier
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}