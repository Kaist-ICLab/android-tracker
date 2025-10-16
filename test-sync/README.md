# Phone Test App (test-sync)

This module demonstrates a comprehensive communication system with BLE, Internet, and Supabase capabilities for phone-watch data synchronization.

## 🏗️ Architecture

The app uses a modular helper-based architecture for clean separation of concerns:

- **BLE Communication**: `BLEDataChannel` - Bidirectional communication between phone and watch
- **Internet Communication**: `InternetDataChannel` - HTTP requests (GET, POST) to external servers
- **Supabase Integration**: Direct `supabase-kt` usage - Database operations and real-time subscriptions
- **Helper Classes**: `BLEHelper`, `InternetHelper`, `SupabaseHelper` - Clean separation of functionality

## 📱 Features

### **BLE Communication**
- **Bidirectional Communication**: Send and receive data between phone and watch
- **Multiple Message Types**: String messages, structured data (JSON), and urgent messages
- **Automatic Callback Management**: Shared callback list for inter-process communication
- **Wearable DataLayer API**: Uses Google's Wearable DataLayer for reliable communication
- **Urgent Message Support**: Priority messages with urgency flag

### **Internet Communication**
- **HTTP Requests**: GET and POST requests to external servers
- **JSON Serialization**: Automatic serialization of structured data
- **Error Handling**: Comprehensive error handling for network operations
- **Configurable URLs**: Easy configuration of server endpoints

### **Supabase Integration**
- **Database Operations**: INSERT, SELECT, UPDATE, DELETE operations
- **Real-time Subscriptions**: Live data updates via WebSocket
- **Direct Integration**: Uses `supabase-kt` library directly (no wrapper layers)
- **Optimized Configuration**: Only PostgREST and Realtime modules (no Auth overhead)

## 🚀 Usage Examples

### BLE Communication
```kotlin
val bleHelper = BLEHelper(context)
bleHelper.initialize()

// Send different types of data
bleHelper.sendString("message", "Hello from phone")
bleHelper.sendTestData("structured_data", TestData("Phone Data", 123))
bleHelper.sendUrgent("urgent_message", "URGENT_MESSAGE")
```

### Internet Communication
```kotlin
val internetHelper = InternetHelper()

// Send GET request
internetHelper.sendGetRequest("https://httpbin.org/get")

// Send POST request with data
val testData = TestData("Hello Server", 456)
internetHelper.sendPostRequest("https://httpbin.org/post", testData)
```

### Supabase Integration
```kotlin
val supabaseHelper = SupabaseHelper()

// Send data to Supabase
supabaseHelper.sendData("Hello Supabase", 789)

// Fetch data from Supabase
supabaseHelper.getData()
```

## 🔧 Setup & Running

### Prerequisites
- Android phone with Bluetooth support
- Wear OS device (Samsung Galaxy Watch, etc.) with `test-sync-watch` app
- Internet connection for HTTP requests and Supabase
- Supabase project with configured database table
- Both devices must have the same application ID
- Bluetooth permissions granted on both devices

### Configuration
1. **Update AppConfig.kt** with your configuration:
   ```kotlin
   object AppConfig {
       // Supabase Configuration
       const val SUPABASE_URL = "your-supabase-url"
       const val SUPABASE_ANON_KEY = "your-supabase-anon-key"
       const val SUPABASE_TABLE_NAME = "your-table-name"
       
       // HTTP Configuration
       const val HTTPBIN_URL = "https://httpbin.org"
   }
   ```

### Installation
1. Build and install the `test-sync` module on your phone
2. Install the companion `test-sync-watch` app on your watch
3. Ensure both devices are paired via Bluetooth

### Testing
1. **Launch both apps** on phone and watch
2. **Test BLE Communication**:
   - **Send String**: Simple text messages
   - **Send TestData**: Structured JSON data  
   - **Send Urgent**: Priority messages
3. **Test Internet Communication**:
   - **Send GET Request**: Test HTTP GET requests
   - **Send POST Request**: Test HTTP POST with JSON data
4. **Test Supabase Integration**:
   - **Send Data to Supabase**: Insert data into database
   - **Send TestData to Supabase**: Insert structured data
   - **Get Data from Supabase**: Fetch data from database
5. **Check logs** for communication results

## 📊 Logging

The app uses centralized log tags for easy debugging:

- `PHONE_BLE`: BLE communication operations
- `PHONE_INTERNET`: Internet/HTTP operations  
- `PHONE_SUPABASE`: Supabase database operations
- All log tags are centralized in `AppConfig.LogTags`

### Example Log Output
```
D/PHONE_SUPABASE: 🗄️ Sending data to Supabase - Message: 'Hello', Value: 123
D/PHONE_BLE: 📱 Sending message to watch - Key: 'message', Data: Hello
D/PHONE_INTERNET: 🌐 Sending GET request to server - URL: 'https://httpbin.org/get'
```

### Log Filtering
```bash
# Filter all phone operations
adb logcat | grep "PHONE_"

# Filter specific operations
adb logcat | grep "PHONE_BLE"      # BLE operations
adb logcat | grep "PHONE_INTERNET" # Internet operations  
adb logcat | grep "PHONE_SUPABASE" # Supabase operations
```

## 🐛 Troubleshooting

### Common Issues
1. **No BLE communication**: Ensure both devices have the same application ID
2. **Permission denied**: Grant Bluetooth permissions on both devices
3. **Connection lost**: Restart both apps and check Bluetooth connectivity
4. **Supabase errors**: Check URL, API key, and table name in AppConfig
5. **Internet errors**: Verify network connectivity and server URLs
6. **PostgREST errors**: Ensure PostgREST module is installed in Supabase client

### Debug Steps
1. Check logs using `adb logcat | grep "PHONE_"` for all phone operations
2. Verify Bluetooth is enabled and devices are paired
3. Test Supabase connection with correct credentials in `AppConfig`
4. Verify internet connectivity for HTTP requests
5. Check Supabase table permissions and RLS policies

## 📚 Key Benefits

1. **Comprehensive Communication**: BLE, Internet, and Supabase integration
2. **Clean Architecture**: Helper-based separation of concerns
3. **Centralized Configuration**: All settings in `AppConfig.kt`
4. **Consistent Logging**: Standardized log tags across all components
5. **Direct Integration**: No wrapper layers for better performance
6. **Optimized Configuration**: Only necessary modules (no Auth overhead)
7. **Real-world Ready**: Production-ready communication system
