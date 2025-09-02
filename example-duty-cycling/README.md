# Duty Cycling Example

This module demonstrates a simple duty cycling implementation that monitors the application state and manages continuous monitoring based on whether the app is in the foreground or background.

## How It Works

### Duty Cycling States

- **ACTIVE**: When the app is open and visible (screen ON, user present)
- **PAUSED**: When the app is minimized or screen is off

### Implementation Details

1. **Screen State Monitoring**: Uses Android broadcast receivers to detect:
   - `ACTION_SCREEN_ON`: Screen turned on
   - `ACTION_SCREEN_OFF`: Screen turned off  
   - `ACTION_USER_PRESENT`: User unlocked device

2. **App Lifecycle Monitoring**: Uses `ProcessLifecycleOwner` to detect:
   - App moved to foreground
   - App moved to background

3. **Continuous Monitoring**: 
   - When ACTIVE: Starts a background coroutine that runs every 5 seconds
   - When PAUSED: Stops the monitoring coroutine

4. **Data Collection**: 
   - Logs all state changes to `duty_cycling.log`
   - Saves JSON data to `duty_cycling_data.json`
   - Each state change includes timestamp, device ID, and state information

## Features

- ✅ Simple duty cycling based on screen/app state
- ✅ Continuous monitoring when active
- ✅ Automatic pause when minimized
- ✅ State change logging and JSON data export
- ✅ Basic Android UI (no fancy Compose)
- ✅ Research-ready implementation

## Usage

1. **Build and run** the app
2. **Open the app** - monitoring becomes ACTIVE
3. **Minimize the app** - monitoring becomes PAUSED
4. **View logs** by tapping the "View Logs" button
5. **Check data files** in the app's internal storage

## Integration with Tracker Library

This example can be easily extended to integrate with the tracker library sensors:

```kotlin
// In startMonitoring() function, add:
private fun startMonitoring() {
    // ... existing code ...
    
    // Start actual sensors
    stepSensor.start()
    locationSensor.start()
    // etc.
}

private fun stopMonitoring() {
    // ... existing code ...
    
    // Stop actual sensors
    stepSensor.stop()
    locationSensor.stop()
    // etc.
}
```

## Files

- `SimpleDutyCyclingManager.kt` - Core duty cycling logic
- `DutyCyclingViewModel.kt` - ViewModel for UI state management
- `DutyCyclingScreen.kt` - Simple Android UI
- `MainActivity.kt` - Main activity setup
- `KoinModule.kt` - Dependency injection setup

## Permissions

- `FOREGROUND_SERVICE` - For background operations
- `POST_NOTIFICATIONS` - For status notifications
- `WAKE_LOCK` - For keeping device awake during monitoring
- `READ_PHONE_STATE` - For device identification

## Research Use Cases

- **Behavioral Studies**: Monitor app usage patterns
- **Battery Research**: Study impact of continuous monitoring
- **User Experience**: Understand app interaction patterns
- **Sensor Fusion**: Combine multiple data sources with duty cycling
