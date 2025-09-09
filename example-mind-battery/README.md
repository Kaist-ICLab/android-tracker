# Duty Cycling Commands Example

This module demonstrates a simple duty cycling command system that sends JSON messages to other devices based on the application and screen state. Built with **Jetpack Compose** for a modern, declarative UI.

## How It Works

### Duty Cycling States

- **APP_OPENED**: When the app is open and visible
- **APP_MINIMIZED**: When the app is minimized but screen is on
- **SCREEN_OFF**: When the screen is off (regardless of app state)

### JSON Commands Sent

1. **App Opened**: `"Continuous Monitoring Started - App Opened"`
2. **App Minimized**: `"Continuous Monitoring Started - App Minimized"`
3. **Screen Off**: `"Continuous Monitoring Paused"`

### Implementation Details

1. **Screen State Monitoring**: Uses Android broadcast receivers to detect:
   - `ACTION_SCREEN_ON`: Screen turned on
   - `ACTION_SCREEN_OFF`: Screen turned off  
   - `ACTION_USER_PRESENT`: User unlocked device

2. **App Lifecycle Monitoring**: Uses Activity lifecycle methods:
   - `onResume()`: App came to foreground
   - `onPause()`: App went to background

3. **JSON Command Generation**: 
   - Each state change generates a JSON command
   - Commands include timestamp, device ID, and state information
   - Commands are stored in memory for real-time access

4. **In-Memory Logging**: 
   - All state changes are logged in memory
   - Logs include timestamps and state transitions
   - Logs are displayed directly in the UI via the "View Logs" button
   - No external storage permissions required

## Features

- ✅ Simple duty cycling based on app/screen state
- ✅ JSON command generation for other devices
- ✅ Automatic state detection and command sending
- ✅ **In-memory logging with real-time display**
- ✅ **Modern Jetpack Compose UI** (no XML layouts)
- ✅ **No external storage permissions required**
- ✅ Research-ready implementation

## Usage

1. **Build and run** the app
2. **Open the app** - sends "Continuous Monitoring Started - App Opened"
3. **Minimize the app** - sends "Continuous Monitoring Started - App Minimized"
4. **Turn off screen** - sends "Continuous Monitoring Paused"
5. **View logs** by tapping the "View Logs" button - logs are displayed directly in the UI
6. **Real-time updates** - logs are updated as state changes occur

## Log Display

Logs are now displayed directly in the app's UI:
- **Access**: Tap the "View Logs" button
- **Content**: Shows both duty cycling logs and JSON commands
- **Format**: Clean, readable format with timestamps
- **Real-time**: Logs update as the app state changes
- **Memory efficient**: Keeps last 100 log entries and 50 JSON commands

## JSON Command Format

```json
{
  "timestamp": 1703123456789,
  "command": "Continuous Monitoring Started - App Opened",
  "state": "APP_OPENED",
  "state_change_time": 1703123456789,
  "device_id": "abc123def456"
}
```

## Integration with Other Devices

This app is designed to send commands to other devices that will handle the actual sensor monitoring:

1. **Command Receiver**: Other devices can monitor the JSON commands
2. **State Synchronization**: Commands provide real-time state updates
3. **Remote Control**: Other devices can start/stop monitoring based on commands
4. **Data Collection**: Actual sensor data collection happens on other devices

## Files

- `AppManager.kt` - Core command generation logic with in-memory logging
- `MainActivity.kt` - Main activity with Compose UI and lifecycle handling
- `KoinModule.kt` - Dependency injection setup

## Permissions

- `FOREGROUND_SERVICE` - For background operations
- `POST_NOTIFICATIONS` - For status notifications
- `WAKE_LOCK` - For keeping device awake during monitoring
- `READ_PHONE_STATE` - For device identification

### Permission Handling

- **No external storage permissions required** - all logging is done in memory
- **Simplified setup** - fewer permission requests for users
- **Better user experience** - logs are immediately accessible within the app
