# Phone BLE Test App

This module demonstrates the new separated DataChannel architecture with BLE communication between phone and watch devices.

## 🏗️ Architecture

The simplified BLE communication architecture focuses on bidirectional communication:

- **BLE Channel**: `BLEDataChannel` - Bidirectional communication between phone and watch
- **Automatic Callback Management**: Shared callback list for inter-process communication
- **Message Types**: String, structured data, and urgent messages

## 📱 Features

### **BLE Communication**
- **Bidirectional Communication**: Send and receive data between phone and watch
- **Multiple Message Types**: String messages, structured data (JSON), and urgent messages
- **Automatic Callback Management**: Shared callback list for inter-process communication
- **Wearable DataLayer API**: Uses Google's Wearable DataLayer for reliable communication
- **Urgent Message Support**: Priority messages with urgency flag

## 🚀 Usage Examples

### BLE Communication
```kotlin
val bleChannel = BLEDataChannel(context)

// Set up listeners for different message types
bleChannel.addOnReceivedListener(setOf("message")) { key, json ->
    Log.d("PHONE_BLE_CHANNEL", "📱 Received message from watch - Key: '$key', Data: $json")
}

bleChannel.addOnReceivedListener(setOf("structured_data")) { key, json ->
    val testData: TestData = Json.decodeFromJsonElement(json)
    Log.d("PHONE_BLE_CHANNEL", "📱 Received structured data from watch - Key: '$key', Data: $testData")
}

bleChannel.addOnReceivedListener(setOf("urgent_message")) { key, json ->
    Log.d("PHONE_BLE_CHANNEL", "🚨 URGENT message from watch - Key: '$key', Data: $json")
}

// Send different types of data
bleChannel.send("message", "Hello from phone")
bleChannel.send("structured_data", Json.encodeToString(TestData("Phone Data", 123)))
bleChannel.send("urgent_message", "URGENT_MESSAGE", isUrgent = true)
```

## 🔧 Setup & Running

### Prerequisites
- Android phone with Bluetooth support
- Wear OS device (Samsung Galaxy Watch, etc.) with `test-sync-watch` app
- Both devices must have the same application ID
- Bluetooth permissions granted on both devices

### Installation
1. Build and install the `test-sync` module on your phone
2. Install the companion `test-sync-watch` app on your watch
3. Ensure both devices are paired via Bluetooth

### Testing
1. **Launch both apps** on phone and watch
2. **Use the UI buttons** to test BLE communication:
   - **Send String**: Simple text messages
   - **Send TestData**: Structured JSON data  
   - **Send Urgent**: Priority messages
3. **Check logs** for communication results
4. **Test bidirectional communication** by sending messages from both devices

## 📊 Logging

The app uses different log tags for easy debugging:

- `PHONE_BLE_CHANNEL`: Received messages from watch
- `PHONE_BLE_SEND`: Outgoing messages to watch

### Example Log Output
```
D/PHONE_BLE_SEND: 📱 Sending message to watch - Key: 'message', Data: Hello from phone
D/PHONE_BLE_CHANNEL: 📱 Received message from watch - Key: 'message', Data: "Hello from watch"
D/PHONE_BLE_CHANNEL: 📱 Received structured data from watch - Key: 'structured_data', Data: TestData(message="Watch Data", value=456)
```

## 🐛 Troubleshooting

### Common Issues
1. **No communication**: Ensure both devices have the same application ID
2. **Permission denied**: Grant Bluetooth permissions on both devices
3. **Connection lost**: Restart both apps and check Bluetooth connectivity
4. **Callbacks not triggered**: Check if `BLEReceiverService` is properly registered

### Debug Steps
1. Check logs on both devices using `adb logcat`
2. Verify Bluetooth is enabled and devices are paired
3. Ensure both apps are running and have proper permissions
4. Look for "No callbacks registered" errors in watch logs

## 📚 Key Benefits

1. **Simplified Architecture**: Single BLE channel for bidirectional communication
2. **Automatic Callback Management**: Shared callback list for inter-process communication
3. **Multiple Message Types**: Support for strings, structured data, and urgent messages
4. **Easy to Use**: Simple API for phone-watch communication
5. **Robust Communication**: Handles urgent messages and structured data
6. **Real-world Ready**: Designed for actual phone-watch communication scenarios
