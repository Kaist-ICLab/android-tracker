# Watch BLE Test App

This is the companion watch app for testing BLE communication with the phone app (`test-sync`). It demonstrates the new separated DataChannel architecture on Wear OS devices with improved callback synchronization.

## 🏗️ Architecture

The watch app uses the same separated DataChannel architecture as the phone app:

- **Complete BLE Channel**: `BLEDataChannel` - Bidirectional communication
- **BLE Sender Only**: `BLESender` - One-way communication (send only)  
- **BLE Receiver Only**: `BLEReceiver` - One-way communication (receive only)
- **Shared Callback List**: Synchronized callbacks between activity and service

## 📱 Features

### Complete BLE Channel
- **Send String**: Sends a simple string message to the phone
- **Send TestData**: Sends structured data (TestData object) to the phone
- **Send Urgent**: Sends urgent messages with priority flag
- **Receive Messages**: Listens for incoming messages from phone
- **Structured Data Support**: Handles JSON serialization/deserialization

### BLE Sender Only
- **Send Sensor Data**: Sends sensor data using individual sender
- **Send Status**: Sends status updates using individual sender
- **Lightweight Communication**: Minimal overhead for simple data transmission

### BLE Receiver Only
- **Listen for Data**: Receives specific data types from phone
- **Shared Callback Management**: Synchronized callbacks between activity and service
- **Inter-Process Communication**: Handles callbacks across process boundaries

## 🔧 Setup

### Prerequisites
- Wear OS device (Samsung Galaxy Watch, etc.)
- Phone with the `test-sync` app installed
- Both devices must have the same application ID

### Installation
1. Build and install the `test-sync-watch` module on your watch
2. Ensure the phone app (`test-sync`) is installed and running
3. Grant Bluetooth permissions on both devices

## 🚀 Usage

1. **Launch the watch app** - You'll see a simple UI with test buttons
2. **Test Complete BLE Channel**:
   - Tap "Send String" to send a simple message
   - Tap "Send TestData" to send structured data
   - Tap "Send Urgent" to send urgent messages
3. **Test BLE Sender Only**:
   - Tap "Send Sensor Data" to send sensor data
   - Tap "Send Status" to send status updates
4. **Check logs** for received messages from the phone

## 📊 Logging

The app uses different log tags for easy debugging:

- `WATCH_BLE_CHANNEL`: Messages from complete channel
- `WATCH_BLE_RECEIVER`: Messages from individual receiver
- `WATCH_BLE_SEND`: Outgoing messages from watch
- `BLEReceiver`: Callback registration and management

### Example Log Output
```
D/WATCH_BLE_SEND: ⌚ Sending message to phone - Key: 'message', Data: WATCH_HELLO_FROM_WATCH
D/WATCH_BLE_CHANNEL: ⌚ Received message from phone - Key: 'message', Data: "Hello from phone"
D/WATCH_BLE_CHANNEL: ⌚ Received structured data from phone - Key: 'structured_data', Data: TestData(message="Phone Data", value=123)
D/WATCH_BLE_CHANNEL: 🚨 URGENT message from phone - Key: 'urgent_message', Data: "URGENT_MESSAGE"
D/BLEReceiver: Added listeners for keys: [message]
```

### Debug Information
- **Callback Registration**: Shows when listeners are added/removed
- **Data Reception**: Shows incoming messages with keys and data
- **Error Handling**: Shows callback registration issues
- **Service Communication**: Shows inter-process communication status

## 🔄 Communication Flow

### Watch → Phone
1. Watch sends data via BLE using Wearable DataLayer API
2. Phone receives data through `BLEReceiverService`
3. Phone processes data and logs it

### Phone → Watch  
1. Phone sends data via BLE using Wearable DataLayer API
2. Watch receives data through `BLEReceiverService`
3. Watch processes data and logs it

## 🛠️ Technical Details

### Dependencies
- `tracker-library`: Core BLE functionality
- `android.gms.wearable`: Google Wearable DataLayer API
- `wear.compose.material`: Wear OS UI components
- `kotlinx.serialization`: JSON serialization

### Permissions
- `BLUETOOTH_CONNECT`: Connect to Bluetooth devices
- `BLUETOOTH_SCAN`: Scan for Bluetooth devices
- `BLUETOOTH`: Basic Bluetooth functionality
- `BLUETOOTH_ADMIN`: Bluetooth administration

### Services
- `BLEReceiverService`: Handles incoming BLE data via Wearable DataLayer

## 🐛 Troubleshooting

### Common Issues
1. **No communication**: Ensure both devices have the same application ID
2. **Permission denied**: Grant Bluetooth permissions on both devices
3. **Connection lost**: Restart both apps and check Bluetooth connectivity

### Debug Steps
1. Check logs on both devices using `adb logcat`
2. Verify Bluetooth is enabled and devices are paired
3. Ensure both apps are running and have proper permissions

## 📚 Related Documentation

- [Phone App README](../test-sync/README.md)
- [Tracker Library Documentation](../tracker-library/README.md)
- [Wearable DataLayer API](https://developers.google.com/android/wear/data-layer)
