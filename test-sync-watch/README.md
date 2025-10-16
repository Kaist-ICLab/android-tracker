# Watch Test App (test-sync-watch)

This is the companion watch app for testing BLE communication with the phone app (`test-sync`). It demonstrates the simplified BLE communication architecture on Wear OS devices.

## 🏗️ Architecture

The watch app uses a simplified BLE communication architecture:

- **BLE Channel**: `BLEDataChannel` - Bidirectional communication with phone
- **Shared Callback List**: Synchronized callbacks between activity and service
- **Message Types**: String, structured data, and urgent messages
- **Clean UI**: Simple button-based interface for testing communication

## 📱 Features

### **BLE Communication**
- **Send String**: Sends simple string messages to the phone
- **Send TestData**: Sends structured data (TestData object) to the phone
- **Send Urgent**: Sends urgent messages with priority flag
- **Receive Messages**: Listens for incoming messages from phone
- **Structured Data Support**: Handles JSON serialization/deserialization
- **Bidirectional Communication**: Full two-way communication with phone

### **UI Features**
- **Compact Design**: Optimized for small watch screens
- **Button Layout**: Short buttons with proper spacing
- **Clean Interface**: Minimal, focused UI for testing
- **Responsive Design**: Works well on different watch sizes

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
2. **Test BLE Communication**:
   - Tap "Send String" to send a simple message
   - Tap "Send TestData" to send structured data
   - Tap "Send Urgent" to send urgent messages
3. **Check logs** for received messages from the phone
4. **Test bidirectional communication** by sending messages from both devices

## 📊 Logging

The watch app uses minimal logging for clean operation:

- **Error Logging**: Only essential error messages are logged
- **Clean Output**: No verbose debug logs for better performance
- **Essential Information**: Only critical errors and connection issues
- **Consistent with Phone**: Uses same logging patterns as phone app

### Example Log Output
```
E/BLE: Connection error: Device not found
E/BLE: Send error: Timeout
```

### Log Filtering
```bash
# Filter watch BLE operations
adb logcat | grep "WATCH_BLE"
```

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

## 📚 Key Benefits

1. **Simplified Architecture**: Clean BLE communication with phone
2. **Optimized UI**: Compact design for watch screens
3. **Minimal Logging**: Clean operation with essential error logging only
4. **Consistent Logging**: Uses same log tag patterns as phone app
5. **Easy Testing**: Simple button-based interface for communication testing
6. **Real-world Ready**: Production-ready watch communication

## 📚 Related Documentation

- [Phone App README](../test-sync/README.md) - Comprehensive communication system
- [Tracker Library Documentation](../tracker-library/README.md) - Core BLE functionality
- [Wearable DataLayer API](https://developers.google.com/android/wear/data-layer) - Google's Wearable API
