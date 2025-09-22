package com.example.test_sync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    sendStringOverBLE: (String, String) -> Unit,
    sendTestDataOverBLE: (String, TestData) -> Unit,
    sendStringOverInternet: (String, String) -> Unit,
    sendTestDataOverInternet: (String, TestData) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        Text("BLE")
        ActionButton(
            onClick = { sendStringOverBLE("test", "HELLO_FROM_PHONE") },
            description = "Send Text"
        )
        ActionButton(
            onClick = { sendTestDataOverBLE("test2", TestData(test = "HELLO_FROM_PHONE", test2 = 123)) },
            description = "Send Data"
        )
        ActionButton(
            onClick = { sendTestDataOverBLE("test2", TestData(test = "HELLO_FROM_PHONE", test2 = 123)) },
            description = "Send Data with time"
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text("Internet")
        ActionButton(
            onClick = { sendStringOverInternet("http://143.248.57.106:3030/recommend", "HELLO_FROM_PHONE") },
            description = "Send Text"
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(description)
    }
}