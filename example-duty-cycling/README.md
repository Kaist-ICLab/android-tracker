# Duty Cycling Commands Example

This module demonstrates a simple duty cycling command system that sends JSON messages to other devices based on the application and screen state.

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
   - Commands are saved to `duty_cycling_commands.json`

4. **Logging**: 
   - All state changes are logged to `duty_cycling.log`
   - Logs include timestamps and state transitions

## Features

- ✅ Simple duty cycling based on app/screen state
- ✅ JSON command generation for other devices
- ✅ Automatic state detection and command sending
- ✅ State change logging and JSON export
- ✅ Basic Android UI (no fancy Compose)
- ✅ Research-ready implementation

## Usage

1. **Build and run** the app
2. **Open the app** - sends "Continuous Monitoring Started - App Opened"
3. **Minimize the app** - sends "Continuous Monitoring Started - App Minimized"
4. **Turn off screen** - sends "Continuous Monitoring Paused"
5. **View logs** by tapping the "View Logs" button
6. **Check JSON commands** in the app's internal storage

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

- `SimpleDutyCyclingManager.kt` - Core command generation logic
- `DutyCyclingViewModel.kt` - ViewModel for UI state management
- `DutyCyclingScreen.kt` - Simple Android UI
- `MainActivity.kt` - Main activity with lifecycle handling
- `KoinModule.kt` - Dependency injection setup

## Permissions

- `FOREGROUND_SERVICE` - For background operations
- `POST_NOTIFICATIONS` - For status notifications
- `WAKE_LOCK` - For keeping device awake during monitoring
- `READ_PHONE_STATE` - For device identification

## Research Use Cases

- **Multi-Device Studies**: Coordinate monitoring across devices
- **Command & Control**: Centralized monitoring management
- **State Synchronization**: Keep multiple devices in sync
- **Remote Monitoring**: Control monitoring from a central device
