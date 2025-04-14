# AgroSphere - Technical Documentation

## 1. Overview

AgroSphere is an IoT-based system designed for automated monitoring and internal climate control of hydroponic greenhouses. It provides real-time sensor data visualization, remote actuator control, and automated environmental management through an Android mobile application connected to an ESP32 microcontroller via Firebase Realtime Database. The system aims to optimize growing conditions, improve resource efficiency, and enhance crop yields, particularly targeting environments like Sampaloc II, Dasmariñas, Cavite.

## 2. Architecture

The system employs a multi-tier architecture:

1.  **Mobile Application Tier (Android)**:
    *   Built using Kotlin and Jetpack Compose with the MVVM (Model-View-ViewModel) pattern.
    *   Provides user interface for monitoring, control, and configuration.
    *   Handles user authentication and interacts with Firebase.
    *   Manages local data caching and notifications.

2.  **Cloud Service Tier (Firebase)**:
    *   **Realtime Database**: Central hub for data storage, synchronization, and communication between the mobile app and the ESP32. Stores current sensor data, historical logs, actuator states, and configuration settings.
    *   **Authentication**: Manages user sign-in (Email/Password, Google Sign-In) and secures database access.
    *   **Server Timestamps**: Ensures consistent time records across the system.

3.  **Hardware Control Tier (ESP32)**:
    *   ESP32 microcontroller interfaces with sensors and actuators.
    *   Acquires sensor data (temperature, humidity, CO₂, light, pH, TDS, water level, water temp).
    *   Executes control logic for actuators (fan, mist pump, vent servo) based on manual commands or automatic thresholds.
    *   Communicates securely with Firebase Realtime Database via WiFi.

This architecture facilitates real-time data flow, responsive control, and clear separation of concerns.

## 3. Technology Stack

### 3.1. Mobile Application (Android)

*   **Programming Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Architecture**: MVVM
*   **Asynchronous Operations**: Kotlin Coroutines & Flow
*   **Backend Integration**: Firebase Realtime Database, Firebase Authentication
*   **Local Storage**: Room Database (for notifications), DataStore Preferences (for settings)
*   **Charting**: MPAndroidChart (`v3.1.0`)
*   **Navigation**: Navigation Compose (`2.8.6`)
*   **Build System**: Gradle with Kotlin DSL
*   **Minimum SDK**: 29 (Android 10)
*   **Target SDK**: 35 (Android 14+)
*   **Compose Compiler Version**: `1.5.15`

### 3.2. Hardware Control System (ESP32)

*   **Microcontroller**: ESP32-WROOM (Dual-core, WiFi/BLE)
*   **Programming Language**: C++ (Arduino Framework)
*   **Cloud Integration**: Firebase Arduino Client Library (`FirebaseClient.h`)
*   **Connectivity**: WiFi (WPA2/PSK) with SSL/TLS (using Root CA)
*   **Sensor Libraries**: `DHT.h`, `OneWire.h`, `DallasTemperature.h`, `BH1750.h`, `Wire.h`
*   **Actuator Libraries**: `ESP32Servo.h`
*   **Time Synchronization**: NTP (Network Time Protocol) via `time.h`

## 4. Core Components

### 4.1. Mobile Application Components

#### 4.1.1. Models (`com.fsvdevs.agrosphere.models`)

*   **`SensorData`**: Represents sensor readings (temperature, humidity, carbonDioxide, lightLevel, pH, tds, waterLevel, waterTemp, timestamp).
*   **`ActuatorData`**: Represents actuator states (fan, mist, vent, auto mode).
*   **`SensorRangeData`**: Stores configured thresholds (tempRangeLow/High, humRangeLow/High, preset name).
*   **`NotificationData`**: Represents a notification entry stored locally (title, message, timestamp).

#### 4.1.2. Repositories (`com.fsvdevs.agrosphere.repository`)

*   **`SensorDataRepository`**: Manages fetching and caching (memory, SharedPreferences) of current and historical sensor data from Firebase. Handles time-range filtering and incremental updates. Detects database changes.
*   **`ActuatorDataRepository`**: Manages fetching and updating actuator states in Firebase. Listens for real-time changes.
*   **`SensorRangeDataRepository`**: Manages fetching and updating sensor range thresholds and presets in Firebase. Listens for real-time changes.
*   **`NotificationRepository`**: Manages local storage (Room DB) of notification history.

#### 4.1.3. ViewModels (`com.fsvdevs.agrosphere.viewmodel`)

*   **`SensorDataViewModel`**: Exposes sensor data (current and historical) as `StateFlow` to the UI. Handles data fetching logic based on time ranges, loading states, and error reporting. Refreshes data based on repository changes.
*   **`ActuatorDataViewModel`**: Exposes actuator states as `StateFlow`. Provides methods to update actuator states.
*   **`SensorRangeDataViewModel`**: Exposes sensor range settings as `StateFlow`. Provides methods to update thresholds and presets.

#### 4.1.4. UI Screens (`com.fsvdevs.agrosphere.ui`)

*   **`SplashScreen`**: Initial loading screen.
*   **`LoginScreen`**: Handles user authentication (Email/Password, Google Sign-In).
*   **`DashboardScreen`**: Displays current sensor readings, actuator controls, mode switch, and preset/range settings.
*   **`MonitorScreen`**: Visualizes historical sensor data using line charts (MPAndroidChart) with time range selection (Hour, Day, Week, Month, Year). Includes refresh functionality.
*   **`NotificationsScreen`**: Displays a list of past alert notifications stored locally. Allows clearing notifications.
*   **`PreferenceScreen`**: Allows users to configure app settings (Theme, Dynamic Color), view app information, and access privacy policy/terms.

#### 4.1.5. Utilities (`com.fsvdevs.agrosphere.util`)

*   **`FirebaseHelper`**: Initializes Firebase services (Auth, Database).
*   **`AuthHelper`**: Handles Google Sign-In logic.
*   **`NotificationHelper`**: Manages creation of notification channels, sending notifications, grouping, cooldown logic, and permission requests (Android 13+).
*   **`ViewModelFactory`**: Provides instances of ViewModels with their dependencies.
*   **`PreferencesManager`**: Manages app preferences (theme, dynamic color) using DataStore.
*   **`DensityHelper`**: Adjusts UI density for accessibility.
*   **`TextLogo`**: Composable for displaying the app logo.

### 4.2. ESP32 Firmware Modules (`AGROSPHERE_PROD.ino`)

*   **Initialization (`setup`)**: Configures WiFi, initializes sensors (DHT, DS18B20, BH1750, MQ135, PH4502C, TDS), actuators (Servo, Relays), Firebase connection, and authentication.
*   **WiFi Management**: Handles connection (`WIFI_SSID`, `WIFI_PASSWORD`) and automatic reconnection with exponential backoff.
*   **Firebase Communication**:
    *   Secure connection using Root CA and `WiFiClientSecure`.
    *   Authentication using User Email/Password (`API_KEY`, `USER_EMAIL`, `USER_PASSWORD`).
    *   Uploads current sensor data to `/sensorData` (every 20s).
    *   Uploads historical data to `/sensorHistory/{uniqueId}` (every 60s).
    *   Fetches actuator states and control mode from `/actuatorStates`.
    *   Fetches sensor thresholds from `/sensorRanges`.
*   **Sensor Reading (`runSensors`, `read*` functions)**: Reads data from all connected sensors. Includes basic filtering/averaging (e.g., median filter for TDS, averaging for pH). Handles `NaN` or disconnected sensor values (returns 0.0).
*   **Actuator Control (`handleActuators`, `controlActuatorAuto`, `ventServoControl`, `mistPumpControl`)**:
    *   **Manual Mode**: Directly sets actuator states based on Firebase values.
    *   **Automatic Mode**: Implements climate control logic based on temperature and humidity thresholds fetched from `/sensorRanges`. Includes hysteresis (±1.0°C temp, ±2.0% hum margins, 2s delay) to prevent rapid cycling.
    *   **Mist Pump Safety**: Implements a duty cycle (max 2 min ON, required 5 min OFF) to prevent overheating.
*   **Time Synchronization**: Uses `time.h` and NTP (`gmtOffset_sec`, `daylightOffset_sec`) for timestamps, although `timeStatusCB` currently uses a simpler uptime-based approximation for auth.
*   **Mock Sensor Mode (`useMockValues`, `updateMockSensorValues`)**: Allows testing without physical sensors by generating simulated data.
*   **Error Handling**: Basic error printing for Firebase operations (`printResult`, `printError`). WiFi reconnection logic. Restarts ESP32 after max WiFi reconnect attempts. Skips history upload if temp/humidity is 0.

## 5. Key Features

*   **Real-time Monitoring**: Live updates of sensor data (Temp, Humidity, CO₂, Light, pH, TDS, Water Level, Water Temp) displayed on the dashboard.
*   **Historical Data Analysis**: Visualization of sensor trends over various time ranges (Hour to Year) using interactive charts.
*   **Remote Actuator Control**: Manual toggling of Fan, Mist system, and Ventilation via the mobile app.
*   **Automatic Climate Control**: ESP32 automatically manages actuators based on user-defined temperature and humidity thresholds to maintain optimal conditions. Includes hysteresis.
*   **Preset Profiles**: Pre-defined threshold settings for common growth phases (Seedling, Vegetative, Flowering) and a "Manual Range" option.
*   **Alert System**: Mobile app checks sensor values against thresholds and generates notifications (with cooldown) for critical conditions (Temp/Hum out of range, High CO₂, Low Light, pH out of range, Low Water Level). Notifications are stored locally.
*   **Authentication**: Secure user login via Email/Password or Google Sign-In.
*   **Caching**: Multi-level caching (memory, SharedPreferences) in the mobile app reduces Firebase reads and improves offline usability.
*   **Adaptive UI**: Material 3 design with Light/Dark/System themes and Dynamic Color support (Android 12+). Scaled density for accessibility.
*   **Fault Tolerance**: ESP32 includes WiFi reconnection logic. Mobile app handles offline scenarios using cached data.

## 6. Firebase Realtime Database Schema

```json
{
  "sensorData": {
    "carbonDioxide": 985.50,
    "humidity": 68.20,
    "lightLevel": 950.00,
    "pH": 6.75,
    "temperature": 26.50,
    "timestamp": { ".sv": "timestamp" }, // Server-side timestamp
    "tds": 1.50,
    "waterLevel": true,
    "waterTemp": 23.80
  },
  "sensorHistory": {
    "m1678886400000-AbCdEfGh": { // Example unique ID: m{millis}-{random}
      "carbonDioxide": 980.00,
      "humidity": 67.90,
      // ... other sensor values ...
      "timestamp": { ".sv": "timestamp" }
    },
    // ... more historical entries ...
  },
  "actuatorStates": {
    "auto": true, // Control mode (true = auto, false = manual)
    "fan": false,
    "mist": false,
    "vent": false // Represents vent open (true) or closed (false)
  },
  "sensorRanges": {
    "humRangeHigh": 70.0,
    "humRangeLow": 60.0,
    "preset": "Seedling Phase", // e.g., "Seedling Phase", "Manual Range"
    "tempRangeHigh": 24.0,
    "tempRangeLow": 18.0
  }
}
```

## 7. Data Flow

1.  **Sensor Data (ESP32 → Firebase → App)**:
    *   ESP32 reads sensors (`runSensors`).
    *   Uploads current data to `/sensorData` (20s interval).
    *   Uploads historical data to `/sensorHistory/{uniqueId}` (60s interval).
    *   Mobile app's `SensorDataRepository` listens to `/sensorData` for real-time updates.
    *   `SensorDataViewModel` processes data for UI (`DashboardScreen`).
    *   `MonitorScreen` fetches historical data from `/sensorHistory` via `SensorDataRepository` based on selected time range.

2.  **User Control (App → Firebase → ESP32)**:
    *   User interacts with controls on `DashboardScreen`.
    *   `ActuatorDataViewModel` calls `ActuatorDataRepository` to update `/actuatorStates` (e.g., toggling `fan`, `mist`, `vent`, or `auto`).
    *   ESP32 reads `/actuatorStates` (1s interval).
    *   If in manual mode (`auto: false`), ESP32 applies the states directly (`handleActuators`).
    *   If in auto mode (`auto: true`), ESP32 ignores manual states and uses its internal logic (`controlActuatorAuto`).

3.  **Configuration (App → Firebase → ESP32)**:
    *   User changes thresholds or presets on `DashboardScreen` (via `PresetSelection` dialog).
    *   `SensorRangeDataViewModel` calls `SensorRangeDataRepository` to update `/sensorRanges`.
    *   ESP32 reads `/sensorRanges` (1s interval) and uses these thresholds in automatic mode.

4.  **Alerts (App-Side Logic)**:
    *   `MainActivity` observes `sensorData` and `sensorRangeData` from ViewModels.
    *   `checkSensorValuesAndNotify` function compares current `sensorData` against `sensorRangeData` thresholds.
    *   If a threshold is violated, `NotificationHelper` sends a system notification (respecting cooldown).
    *   `NotificationRepository` saves the alert to the local Room database.
    *   `NotificationsScreen` displays alerts from the local database.

## 8. ESP32 Firmware Details

*   **Pin Configuration**:
    *   DHT11: Pin 12
    *   MQ135 (CO₂): Pin 34 (Analog)
    *   BH1750 (Light): I2C (SDA: 21, SCL: 22)
    *   Float Switch (Water Level): Pin 19 (Input Pullup)
    *   DS18B20 (Water Temp): Pin 23 (OneWire)
    *   PH4502C (pH): Pin 33 (Analog)
    *   TDS/EC: Pin 32 (Analog)
    *   Fan Relay: Pin 18 (Output)
    *   Mist Pump Relay: Pin 17 (Output)
    *   Vent Servo: Pin 13 (PWM Output)
*   **Control Logic**: See `controlActuatorAuto` for automatic mode logic with hysteresis. Manual mode directly reflects Firebase states.
*   **Safety Features**: Mist pump duty cycle (2 min ON / 5 min OFF). Hysteresis prevents rapid actuator switching.
*   **Fault Tolerance**:
    *   **WiFi Auto-Reconnect**: Attempts reconnection upon disconnection using `WiFi.reconnect()`. If connection fails repeatedly, the system might enter a loop or eventually restart depending on surrounding logic, but currently, it keeps trying every 5 seconds while offline.
    *   **Power Interruption**: The system relies on the external solar power system with battery backup. If main power and battery fail, the ESP32 will shut down. Upon power restoration, the ESP32 reboots, automatically reconnects to WiFi, re-authenticates with Firebase, fetches the latest states/settings, and resumes operation. No operational state is saved locally on the ESP32 across power cycles, except for data stored in SPIFFS.
    *   **Network Outage**: When WiFi disconnects (`isOffline = true`), the ESP32 continues operating locally.
        *   It forces `isAuto = true`, running `controlActuatorAuto` using the last known thresholds fetched from `/sensorRanges`.
        *   Sensor readings continue (`runSensors`).
        *   Firebase uploads (`setSensorData`, `uploadSensorDataToRealtimeDatabase`) are skipped.
        *   Fetching new settings (`/sensorRanges`, `/actuatorStates/auto`) is paused.
        *   Historical sensor data that fails to upload is saved locally to SPIFFS (`saveDataLocally`).
        *   Upon reconnection, `uploadOfflineHistory` attempts to upload the stored data from SPIFFS.
    *   **Sensor Failure**: Sensor reading functions (`read*`) return `0.0` or a specific error code (e.g., `DEVICE_DISCONNECTED_C` for DS18B20) if a sensor fails or returns `NaN`.
        *   The system prevents uploading historical data if `temperature` or `humidity` is `0.0` (`uploadSensorDataToRealtimeDatabase`).
        *   The automatic control logic (`controlActuatorAuto`) might be affected if it relies on a failed sensor reading (now `0.0`), potentially leading to incorrect actuator states (e.g., turning on mist unnecessarily if humidity reads 0). However, the system continues to operate with the remaining sensors.
*   **Memory**: Basic `Serial.println(ESP.getFreeHeap())` for monitoring. Offline history is stored in SPIFFS, managing potential memory constraints for long offline periods.
*   **Time Sync**: Configured for NTP but `timeStatusCB` uses a simpler approximation for auth. Server timestamps (`{ ".sv": "timestamp" }`) used for data logging ensure consistency regardless of ESP32 time accuracy.

## 9. Mobile Application Details

*   **MVVM Implementation**: Clear separation of UI (Compose), State/Logic (ViewModels), and Data Handling (Repositories).
*   **Caching**: `SensorDataRepository` uses `ConcurrentHashMap` for memory cache and `SharedPreferences` for persistent cache, reducing Firebase dependency.
*   **UI**: Built entirely with Jetpack Compose and Material 3, providing a modern and responsive interface. Uses `MPAndroidChart` for historical data visualization.
*   **Navigation**: Single-Activity architecture using Navigation Compose for screen transitions.
*   **Notifications**: Uses Android's `NotificationManagerCompat`, notification channels, grouping, and handles Android 13+ permission requests. Includes cooldown logic.
*   **Theming**: Supports Light/Dark/System themes and Material You dynamic colors via `AgroSphereTheme` and `PreferencesManager`.

## 10. Build Configuration

*   **Gradle Version**: AGP `8.7.3`
*   **Kotlin Version**: `2.1.0-Beta2`
*   **Key Dependencies**: See `libs.versions.toml` and `app/build.gradle.kts`. Includes Compose BOM, Firebase BOM, Room, DataStore, Navigation, MPAndroidChart, Play Services Auth.
*   **Version**: `1.50` (Build `519`) <!-- Adjusted version to match ESP code header -->

## 11. Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/fsvdevs/agrosphere/
│   │   │   ├── dao/              # Room Database DAOs (NotificationDao)
│   │   │   ├── models/           # Data classes (SensorData, ActuatorData, etc.)
│   │   │   ├── repository/       # Data repositories (SensorDataRepository, etc.)
│   │   │   ├── room/             # Room Database setup (AppDatabase)
│   │   │   ├── routes/           # Navigation routes definition (Routes)
│   │   │   ├── ui/               # Composable UI screens and components
│   │   │   │   ├── dialog/       # Reusable dialog composables
│   │   │   │   └── theme/        # Theming (Color, Shape, Theme, Type)
│   │   │   ├── util/             # Utility classes (FirebaseHelper, NotificationHelper, etc.)
│   │   │   ├── viewmodel/        # ViewModels
│   │   │   └── MainActivity.kt   # Main entry point activity
│   │   ├── res/                  # Android resources (drawables, layouts, etc.)
│   │   └── AndroidManifest.xml
│   └── ...
├── build.gradle.kts              # App-level build script
└── ...
build.gradle.kts                  # Project-level build script
gradle/libs.versions.toml         # Dependency versions catalog
README.md                         # This file
...                               # Other project files (.gitignore, etc.)
```

## 12. Conclusion

AgroSphere provides a comprehensive IoT solution for greenhouse monitoring and control, integrating a user-friendly mobile application with robust hardware control via an ESP32 and Firebase cloud services. The system leverages real-time data, automated control logic with safety features, and efficient data management to optimize growing conditions. Its modular architecture and use of modern technologies make it a scalable and adaptable platform for smart agriculture applications.