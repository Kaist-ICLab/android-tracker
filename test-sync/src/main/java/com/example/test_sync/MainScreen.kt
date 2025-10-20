package com.example.test_sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    sendStringOverBLE: (String, String) -> Unit,
    sendTestDataOverBLE: (String, TestData) -> Unit,
    sendUrgentBLE: (String, String) -> Unit,
    sendGetRequest: (String) -> Unit,
    sendPostRequest: (String, TestData) -> Unit,
    sendToSupabase: (String, Int) -> Unit,
    getFromSupabase: () -> Unit,
    togglePolling: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📱 BLE Communication Test",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        ActionButton(
            onClick = {
                sendStringOverBLE(
                    "message",
                    "HellO STRING FROM PHONE"
                )
            },
            description = "Send String to Watch"
        )
        ActionButton(
            onClick = {
                sendTestDataOverBLE(
                    "structured_data",
                    TestData(message = "HELLO STRUCTURED DATA FROM PHONE", value = 123)
                )
            },
            description = "Send Structured Data to Watch"
        )
        ActionButton(
            onClick = {
                sendUrgentBLE(
                    "urgent_message",
                    "HELLO URGENT MESSAGE FROM PHONE"
                )
            },
            description = "Send Urgent Message to Watch"
        )
        Text(
            text = "Check ADB Logcat for received BLE data from watch",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = "🌐 Internet Communication Test",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
        ActionButton(
            onClick = {
                sendGetRequest("https://httpbin.org/get")
            },
            description = "Send GET Request"
        )
        ActionButton(
            onClick = {
                sendPostRequest(
                    "https://httpbin.org/post",
                    TestData(message = "Internet Data from Phone", value = 789)
                )
            },
            description = "Send POST Request"
        )
        Text(
            text = "Check ADB Logcat for received HTTP Response",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = "🗄️ Supabase Interaction Test",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
        ActionButton(
            onClick = {
                val timestamp = System.currentTimeMillis()
                sendToSupabase("Hello from Supabase", timestamp.toInt())
            },
            description = "Send Data to Supabase"
        )
        ActionButton(
            onClick = {
                getFromSupabase()
            },
            description = "Retrieve Data from Supabase"
        )
        ActionButton(
            onClick = {
                togglePolling()
            },
            description = "Start / Stop Data Polling"
        )
        Text(
            text = "Check ADB Logcat for Supabase operations. \n Supabase is NOT implemented in the Tracker Library. ",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
fun ActionButton(
    onClick: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        Text(description)
    }
}