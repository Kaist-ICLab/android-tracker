# Phone BLE Test App

This module demonstrates the new separated DataChannel architecture with BLE communication between phone and watch devices.

## 🏗️ Architecture

The new DataChannel architecture separates sending and receiving functionality:

- **Complete BLE Channel**: `BLEDataChannel` - Bidirectional communication
- **BLE Sender Only**: `BLESender` - One-way communication (send only)
- **BLE Receiver Only**: `BLEReceiver` - One-way communication (receive only)

## 📱 Features

### 1. **Complete BLE Channel**
- Bidirectional BLE communication using Wearable DataLayer API
- Both sending and receiving use the same BLE mechanism
- Supports urgent messages with priority flag
- Automatic callback synchronization between activity and service

### 2. **BLE Sender Only**
- Use individual `BLESender` for one-way communication
- Useful when you only need to send data
- Lightweight for simple data transmission

### 3. **BLE Receiver Only**
- Use individual `BLEReceiver` for listening only
- Useful when you only need to receive data
- Shared callback list for inter-process communication

## 🚀 Usage Examples

### Complete BLE Channel
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

// Send different types of data
bleChannel.send("message", "Hello from phone")
bleChannel.send("structured_data", Json.encodeToString(TestData("Phone Data", 123)))
bleChannel.send("urgent_message", "URGENT_MESSAGE", isUrgent = true)
```

### BLE Sender Only
```kotlin
val bleSender: DataSender<Unit> = BLESender(context)

// Send data (one-way)
bleSender.send("sensor_data", "sensor reading")
bleSender.send("device_status", "status update")
```

### BLE Receiver Only
```kotlin
val bleReceiver: DataReceiver = BLEReceiver()

// Listen for specific data types
bleReceiver.addOnReceivedListener(setOf("sensor_data")) { key, json ->
    Log.d("PHONE_BLE_RECEIVER", "📱 Received sensor data from watch - Key: '$key', Data: $json")
}
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
2. **Use the UI buttons** to test different BLE communication patterns:
   - Send String: Simple text messages
   - Send TestData: Structured JSON data
   - Send Urgent: Priority messages
   - Send Sensor Data: Sensor readings
   - Send Status: Device status updates
3. **Check logs** for communication results

## 📊 Logging

The app uses different log tags for easy debugging:

- `PHONE_BLE_CHANNEL`: Messages from complete channel
- `PHONE_BLE_RECEIVER`: Messages from individual receiver
- `PHONE_BLE_SEND`: Outgoing messages

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

1. **Separation of Concerns**: Sending and receiving logic are separate
2. **Flexibility**: Can use senders and receivers independently
3. **Inter-Process Communication**: Shared callback list for service communication
4. **Testability**: Components can be tested in isolation
5. **Simple Usage**: Easy to understand and implement
6. **Robust Communication**: Handles urgent messages and structured data
