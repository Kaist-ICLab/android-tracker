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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    sendStringOverBLE: (String, String) -> Unit,
    sendTestDataOverBLE: (String, TestData) -> Unit,
    sendUrgentBLE: (String, String) -> Unit,
    senderOnlyBLE: (String, String) -> Unit,
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
            text = "📱 Phone BLE Test",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        Text(
            text = "📱 BLE Channel (Complete)",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ActionButton(
            onClick = {
                sendStringOverBLE(
                    "message",
                    "BLE_Channel_Complete_HELLO_STRING_FROM_PHONE"
                )
            },
            description = "Send String"
        )
        ActionButton(
            onClick = {
                sendTestDataOverBLE(
                    "structured_data",
                    TestData(message = "BLE_Channel_Complete_HELLO_DATA_FROM_PHONE", value = 123)
                )
            },
            description = "Send TestData"
        )
        ActionButton(
            onClick = {
                sendUrgentBLE(
                    "urgent_message",
                    "BLE_Channel_Complete_HELLO_URGENT_DATA_FROM_PHONE"
                )
            },
            description = "Send Urgent Message"
        )

        Text(
            text = "📤 BLE Sender Only",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        ActionButton(
            onClick = { senderOnlyBLE("sensor_data", "BLE_Sender_Only_Send_Sensor_Data") },
            description = "Send Sensor Data"
        )
        ActionButton(
            onClick = { senderOnlyBLE("device_status", "BLE_Sender_Only_Send_Status") },
            description = "Send Status"
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