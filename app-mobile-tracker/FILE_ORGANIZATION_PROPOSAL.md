# TODO: Remove This Later

# Settings Screen File Organization Proposal

## Current Issues

1. **Inconsistent naming**: `LanguageScreen.kt` vs `AccountSettingsScreen.kt` vs `PhoneSensorScreen.kt`
2. **Mixed component locations**: Some in root `components/`, some in sub-settings, some in root `UI.kt`
3. **Unclear structure for nested settings**: No pattern for sub-settings within a setting
4. **Inconsistent file organization**: Some have `UI.kt`/`Utils.kt`, some don't

## Proposed Structure

```
SettingsScreen/
├── SettingsScreen.kt          # Main screen entry point
├── Styles.kt                  # Main screen styles
│
├── components/                 # Shared components for SettingsScreen
│   ├── EnableTrackerCard.kt
│   ├── SettingsMenuItem.kt     # Move from UI.kt
│   ├── SettingsMenuItemWithDivider.kt  # Move from UI.kt
│   └── SettingsHeader.kt      # Move from SettingsScreen.kt
│
├── AccountSettings/
│   ├── AccountSettingsScreen.kt
│   ├── Styles.kt
│   ├── components/            # Account-specific components (if any)
│   └── utils/                 # Account-specific utilities (if any)
│
├── LanguageSettings/
│   ├── LanguageSettingsScreen.kt  # Rename from LanguageScreen.kt
│   ├── Styles.kt
│   ├── UI.kt                  # Language-specific UI components
│   └── components/            # Language-specific components (if any)
│
├── PermissionSettings/
│   ├── PermissionSettingsScreen.kt
│   ├── Styles.kt
│   ├── UI.kt
│   ├── Utils.kt
│   └── components/            # Permission-specific components (if any)
│
├── PhoneSensorSettings/
│   ├── PhoneSensorSettingsScreen.kt  # Rename from PhoneSensorScreen.kt
│   ├── Styles.kt
│   ├── UI.kt
│   ├── Utils.kt
│   ├── components/            # Components for sensor list view
│   │   └── SensorCard.kt      # Move from UI.kt
│   └── IndividualSensorSettings/  # Nested: Individual sensor configuration
│       ├── IndividualSensorSettingsScreen.kt
│       ├── Styles.kt
│       └── components/         # Components for individual sensor view
│           └── SensorConfigCard.kt
│
├── DevicesSettings/
│   ├── DevicesSettingsScreen.kt
│   ├── Styles.kt
│   └── components/            # For nested settings like "Add Device", "Device Details"
│
├── ServerSyncSettings/
│   ├── ServerSyncSettingsScreen.kt
│   ├── Styles.kt
│   └── components/            # For nested settings like "Sync Schedule", "Data Retention"
│
└── AboutSettings/
    ├── AboutSettingsScreen.kt
    └── Styles.kt
```

## Key Principles

### 1. **Naming Convention**
- All main screens: `{Name}SettingsScreen.kt` (consistent)
- All styles: `Styles.kt`
- All utilities: `Utils.kt` (if needed)
- All UI components: `UI.kt` (if needed)

### 2. **Component Organization**
- **Shared components** (used by main SettingsScreen): `SettingsScreen/components/`
- **Sub-setting specific components**: `{SubSetting}/components/`
- **Nested settings components**: `{SubSetting}/components/{NestedSetting}/`

### 3. **Multi-Level Nested Settings Pattern**

Settings can have multiple levels of nesting. Each nested level gets its own folder:

**Example 1: Phone Sensor Settings → Individual Sensor Settings**
```
PhoneSensorSettings/
├── PhoneSensorSettingsScreen.kt    # Shows list of sensors
├── Styles.kt
├── UI.kt                           # SensorCard, etc.
├── Utils.kt
├── components/                     # Components for list view
│   └── SensorCard.kt
└── IndividualSensorSettings/       # Nested level 1
    ├── IndividualSensorSettingsScreen.kt
    ├── Styles.kt
    ├── components/                 # Components for individual sensor
    │   └── SensorConfigCard.kt
    └── [FurtherNestedSettings]/    # Nested level 2 (if needed)
        └── ...
```

**Example 2: Devices Settings → Add Device → Device Configuration**
```
DevicesSettings/
├── DevicesSettingsScreen.kt       # Shows list of devices
├── Styles.kt
├── components/
│   └── DeviceCard.kt
└── AddDeviceSettings/              # Nested level 1
    ├── AddDeviceSettingsScreen.kt
    ├── Styles.kt
    ├── components/
    │   └── DeviceForm.kt
    └── DeviceConfigurationSettings/ # Nested level 2
        ├── DeviceConfigurationSettingsScreen.kt
        └── Styles.kt
```

**Key Pattern**: Each nested level is a folder with the same structure as a top-level setting:
- `{Name}SettingsScreen.kt`
- `Styles.kt`
- `components/` (optional)
- `Utils.kt` (optional)
- Further nested folders (optional)

### 4. **File Types per Setting Level**
Each level (top-level or nested) follows the same pattern:
- **Always present**: `{Name}SettingsScreen.kt`, `Styles.kt`
- **Optional**: `UI.kt` (if multiple UI components), `Utils.kt` (if helper functions)
- **Optional**: `components/` (if this level has reusable components)
- **Optional**: `{NestedName}Settings/` (if this level has nested settings)

### 5. **Naming Convention for Nested Settings**
- **Level 1**: `PhoneSensorSettings/`, `DevicesSettings/`
- **Level 2**: `PhoneSensorSettings/IndividualSensorSettings/`
- **Level 3**: `PhoneSensorSettings/IndividualSensorSettings/AdvancedSettings/`
- Each nested folder follows the same naming: `{Name}Settings/`

## Pros and Cons

### Pros ✅

1. **Scalable**: Easy to add new settings or nested settings at any level
   - Adding a new top-level setting? Create a new folder following the pattern
   - Adding nested settings? Create a nested folder with the same structure
   - No need to restructure existing code

2. **Discoverable**: Clear where to find/add files
   - Folder structure mirrors navigation hierarchy
   - Easy to locate files: just follow the navigation path
   - New developers can quickly understand the structure

3. **Consistent**: Same pattern at every level (top-level and nested)
   - Predictable structure reduces cognitive load
   - Same conventions apply everywhere
   - Easier to maintain and refactor

4. **Maintainable**: Components are co-located with their usage level
   - Related files are grouped together
   - Changes to a feature are localized to its folder
   - Easier to understand dependencies

5. **Flexible**: Supports unlimited nesting levels with the same structure
   - No artificial limits on nesting depth
   - Pattern works for simple and complex settings
   - Future-proof for evolving requirements

6. **Self-documenting**: Folder structure shows the navigation hierarchy
   - Code structure reflects UI structure
   - Easy to visualize the app's settings flow
   - Reduces need for additional documentation

7. **Separation of Concerns**: Clear boundaries between different settings
   - Each setting is self-contained
   - Shared components are clearly separated
   - Reduces coupling between features

### Cons ❌

1. **Deep Folder Nesting**: Can lead to long file paths
   - Example: `SettingsScreen/PhoneSensorSettings/IndividualSensorSettings/AdvancedSensorSettings/Styles.kt`
   - May be harder to navigate in some IDEs
   - **Mitigation**: Most IDEs have good file navigation/search features

2. **Potential Duplication**: Similar components might be duplicated across levels
   - Each level might have its own `Styles.kt` with similar values
   - Some utility functions might be duplicated
   - **Mitigation**: Extract truly shared code to common locations, use inheritance/composition

3. **Initial Setup Overhead**: More folders to create initially
   - Requires discipline to follow the pattern
   - Might feel like over-engineering for simple settings
   - **Mitigation**: Start simple, add structure as complexity grows

4. **Import Paths**: Longer import statements for deeply nested files
   - Example: `import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensorSettings.IndividualSensorSettings.IndividualSensorSettingsScreen`
   - Can make imports verbose
   - **Mitigation**: Use IDE import organization, consider package aliases if needed

5. **Component Location Decisions**: Sometimes unclear where a component belongs
   - Is it shared? Sub-setting specific? Nested?
   - Requires judgment calls
   - **Mitigation**: Clear guidelines in this document, start with most specific location and move up if needed

6. **File Count**: More files overall (each level has its own `Styles.kt`, etc.)
   - More files to manage
   - More files to search through
   - **Mitigation**: Good IDE search and navigation tools help

7. **Migration Effort**: Requires refactoring existing code
   - Need to move and rename files
   - Update imports across the codebase
   - **Mitigation**: Can be done incrementally, one setting at a time

### Trade-offs Summary

| Aspect | Current Structure | Proposed Structure |
|--------|------------------|-------------------|
| **Simplicity** | ✅ Simpler, fewer folders | ❌ More folders, more structure |
| **Scalability** | ❌ Hard to scale, unclear patterns | ✅ Easy to scale, clear patterns |
| **Discoverability** | ❌ Hard to find related files | ✅ Easy to find related files |
| **Consistency** | ❌ Inconsistent naming/structure | ✅ Consistent everywhere |
| **Maintainability** | ⚠️ Mixed, depends on feature | ✅ High, clear organization |
| **Onboarding** | ⚠️ Need to learn each feature's structure | ✅ One pattern to learn |
| **File Path Length** | ✅ Shorter paths | ❌ Longer paths for nested settings |

### Recommendation

**Use this structure if:**
- You expect settings to grow in complexity
- You plan to have nested settings (like Individual Sensor Settings)
- You want a maintainable, scalable codebase
- You have multiple developers working on the codebase

**Consider simpler structure if:**
- Settings will remain simple with no nesting
- You have a very small team (1-2 developers)
- You prioritize quick iteration over long-term maintainability

## Migration Steps

1. Rename inconsistent files (`LanguageScreen.kt` → `LanguageSettingsScreen.kt`)
2. Move shared components from `UI.kt` to `components/`
3. Move `SettingsHeader` from `SettingsScreen.kt` to `components/`
4. Create `components/` folders in sub-settings that need them
5. Move sub-setting specific components to their respective `components/` folders

