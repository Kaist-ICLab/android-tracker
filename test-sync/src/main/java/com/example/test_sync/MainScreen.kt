package com.example.test_sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    sendStringOverBLE: (String, String) -> Unit,
    sendTestDataOverBLE: (String, TestData) -> Unit,
    sendUrgentBLE: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📱 Phone Communication Test",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)

        )

        Text(
            text = "BLE Communication Phone <-> Watch",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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