<<<<<<< HEAD
=======
<div align="center">
  <img src="assets/AGS.png" alt="AgroSphere Logo" width="150"/>
  <h1>AgroSphere</h1>
  <p>IoT-Based Automated Greenhouse Monitoring & Climate Control by <b>AGROBROS</b></p>

  <!-- Badges -->
  <p>
    <img src="https://img.shields.io/badge/ESP32-E7332C?style=for-the-badge&logo=espressif&logoColor=white" alt="ESP32 Badge"/>
    <img src="https://img.shields.io/badge/C++-00599C?style=for-the-badge&logo=cplusplus&logoColor=white" alt="C++ Badge"/>
    <img src="https://img.shields.io/badge/Arduino-00979D?style=for-the-badge&logo=arduino&logoColor=white" alt="Arduino Badge"/>
    <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase Badge"/>
    <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android Badge"/>
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin Badge"/>
    <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose Badge"/>
    <img src="https://img.shields.io/badge/Material_Design_3-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3 Badge"/>
    <img src="https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge" alt="MVVM Badge"/>
  </p>
</div>

>>>>>>> 8333e81c54078574b4e1ef9654d2549b9b25dbe8
# AgroSphere - Technical Documentation

## 1. Project Overview

AgroSphere is an IoT-based system designed, implemented, and validated for automated monitoring and internal climate control within a prototype greenhouse environment. It aims to demonstrate the feasibility of applying IoT technology to enhance precision agriculture concepts, improve resource efficiency, and bolster food security strategies, particularly by showcasing a functional and user-friendly system adaptable for resource-constrained environments. The system integrates hardware (ESP32, sensors, actuators), cloud services (Firebase), and a mobile application (Android) for comprehensive greenhouse management.

## 2. Features

*   **Real-time Monitoring:** Continuous tracking of Temperature, Humidity, CO₂, Light Intensity, Water Level, Water Temperature, pH, and TDS.
*   **Remote Control:** Manual control of Fan, Mist System, and Ventilation via the Android app.
*   **Automated Climate Control:** ESP32 automatically manages actuators based on configurable thresholds (fetched from Firebase) and sensor readings, with hysteresis to prevent rapid cycling.
*   **Dual Control Modes:** Seamless switching between Manual and Automatic control.
*   **Preset Growth Profiles:** Allows selection of predefined sensor range settings (e.g., Seedling, Flowering) or manual configuration.
*   **Historical Data Logging & Visualization:** Sensor data logged to Firebase with timestamps, visualized in charts on the mobile app.
*   **Offline Resilience:**
    *   ESP32 continues automatic control using last known settings during network outages.
    *   Historical sensor data is buffered locally on ESP32's SPIFFS filesystem when offline and uploaded upon reconnection.
    *   Mobile app utilizes multi-level caching (memory, SharedPreferences) for data persistence.
*   **Alert Notifications:** Mobile app notifications triggered when sensor readings exceed safe thresholds (with cooldown).
*   **User Authentication:** Secure login via Email/Password or Google Sign-In using Firebase Authentication.
*   **User Preferences:** Customizable app theme (Light/Dark/System) and dynamic color options (Android 12+).
*   **System Information:** In-app display of version, authors, and privacy policy/terms of service.

## 3. Architecture

AgroSphere employs a multi-tier architecture:

1.  **Hardware Tier (ESP32):**
    *   Acquires data from various sensors (DHT11/22, MQ135, BH1750, DS18B20, PH4502C, TDS, Float Switch).
    *   Controls actuators (Fan Relay, Mist Relay, Servo Motor).
    *   Executes automatic climate control logic.
    *   Communicates securely with Firebase via WiFi (HTTPS).
    *   Handles offline data buffering using SPIFFS.
2.  **Cloud Tier (Firebase):**
    *   **Realtime Database:** Stores current sensor data, actuator states, sensor range settings, and historical data logs. Provides real-time data synchronization.
    *   **Authentication:** Manages user authentication and authorization.
3.  **Mobile Application Tier (Android):**
    *   **UI (Jetpack Compose):** Provides user interface for monitoring, control, and configuration using Material 3 design principles.
    *   **ViewModels (Kotlin):** Expose state to the UI and handle user interactions.
    *   **Repositories (Kotlin):** Abstract data sources (Firebase, local cache), implement caching logic, and manage data synchronization.
    *   **Models (Kotlin):** Data classes representing sensor data, actuator states, etc.
    *   **Local Storage:** Room Database (for notifications), SharedPreferences (for preferences and caching).

## 4. Technology Stack

*   **Hardware:** ESP32-WROOM, DHT22, MQ135, BH1750, DS18B20, PH4502C, DFRobot TDS Sensor, Float Switch, SG90 Servo, Relays, 5V Fan, 5V Atomizer.
*   **Firmware:** C++ (Arduino Framework), FirebaseClient library, ArduinoJson, Sensor-specific libraries.
*   **Cloud:** Google Firebase (Realtime Database, Authentication).
*   **Mobile App:** Android (Kotlin), Jetpack Compose, Material 3, MVVM Architecture, Coroutines, StateFlow, ViewModel, LiveData (potentially via `collectAsState`), Room, Retrofit (if used, though Firebase SDK is primary), MPAndroidChart (via Compose wrapper), Google Sign-In.
*   **Communication:** WiFi, HTTPS, MQTT (via Firebase library).

## 5. Hardware Components

*   **Microcontroller:** ESP32-WROOM-32E
*   **Sensors:**
    *   Temperature & Humidity: DHT22
    *   Carbon Dioxide: MQ135 (Analog)
    *   Light Intensity: BH1750 (I2C)
    *   Water Temperature: DS18B20 (OneWire)
    *   pH Level: PH4502C (Analog)
    *   Total Dissolved Solids (TDS): DFRobot Analog TDS Sensor
    *   Water Level: Float Switch (Digital)
*   **Actuators:**
    *   Fan: 5V DC Fan controlled via Relay
    *   Mist System: 5V Atomizer controlled via Relay
    *   Ventilation: SG90 Servo Motor

## 6. Software Components

### 6.1. ESP32 Firmware (`.ino`)

*   **Setup:** Initializes WiFi, Firebase connection (with authentication), sensors, and actuators.
*   **Sensor Reading:** Periodically reads data from all connected sensors, applying smoothing or calibration where necessary.
*   **Actuator Control:**
    *   Implements `controlActuatorAuto()` logic based on fetched thresholds and current sensor readings.
    *   Includes hysteresis for temperature and humidity control.
    *   Includes safety cycle for mist pump (e.g., 2 min ON / 5 min OFF).
    *   Responds to manual control commands fetched from Firebase.
*   **Firebase Communication:**
    *   Uploads current sensor data (`/sensorData`).
    *   Uploads historical sensor data (`/sensorHistory`) with timestamps.
    *   Fetches actuator states (`/actuatorStates`) and sensor ranges (`/sensorRanges`).
    *   Handles authentication token refresh (`JWT.loop()`).
*   **Offline Handling:**
    *   Detects network/internet unavailability.
    *   Saves historical data to SPIFFS (`/offline_history.jsonl`) when offline.
    *   Uploads buffered data upon reconnection (`uploadOfflineHistory()`).
    *   Uses last known ranges/settings for automatic control when offline.
*   **Time Management:** Uses estimated timestamps based on last successful Firebase sync or uptime approximation.

### 6.2. Android Application (`app` module)

*   **Architecture:** Follows MVVM (Model-View-ViewModel).
*   **UI:** Built entirely with Jetpack Compose and Material 3 components.
    *   `DashboardScreen`: Displays current sensor values, actuator controls, preset selection.
    *   `MonitorScreen`: Shows historical data using charts (MPAndroidChart wrapper).
    *   `NotificationsScreen`: Lists historical alerts fetched from Room DB.
    *   `PreferenceScreen`: Allows theme selection, dynamic color toggle, account info, sign out.
    *   `LoginScreen`, `SplashScreen`.
*   **ViewModels:** (`SensorDataViewModel`, `ActuatorDataViewModel`, `SensorRangeDataViewModel`) Manage UI state, interact with Repositories, handle user actions using Coroutines and StateFlow.
*   **Repositories:** (`SensorDataRepository`, `ActuatorDataRepository`, `SensorRangeDataRepository`, `NotificationRepository`) Abstract data sources, manage Firebase listeners, implement multi-level caching (memory/SharedPreferences), interact with Room DB (for notifications).
*   **Utilities:** Helper functions for authentication (`AuthHelper`), Firebase setup (`FirebaseHelper`), notifications (`NotificationHelper`), preferences (`PreferencesManager`), date formatting, etc.

## 7. Cloud Integration (Firebase)

*   **Realtime Database:** Acts as the central hub for data synchronization.
    *   `/sensorData`: Holds the latest readings from the ESP32.
    *   `/sensorHistory`: Stores timestamped historical records (from ESP32, including offline buffer uploads).
    *   `/actuatorStates`: Reflects the current state of each actuator and the control mode (auto/manual). Updated by both ESP32 (auto mode) and App (manual mode).
    *   `/sensorRanges`: Stores the user-defined or preset temperature/humidity thresholds used by the ESP32 in auto mode.
*   **Authentication:** Secures access to the database and identifies users. Supports Email/Password and Google Sign-In.

## 8. Key Performance Highlights (Prototype Validation)

*   **Sensor Network:** ~2.07s average update interval, ~1.51s average end-to-end latency (sensor-to-app).
*   **Actuator Response:** <1.2s average for manual control (app-to-actuator), <0.2s average for automatic control (ESP32 internal).
*   **Reliability:** Successfully handled simulated power and network outages, recovering and synchronizing data. Maintained local control during outages.
*   **Data Optimization:** Multi-level caching in the app resulted in an estimated ~8.7% reduction in Firebase data transfer.
*   **Usability:** High task completion rates (>93%) and positive user feedback (>85% satisfaction) in testing.

## 9. File Structure

```
AgroSphere/
├── .gradle/
├── .idea/
├── app/                          # Android Application Module
│   ├── build/
│   ├── libs/
│   ├── src/
│   │   ├──androidTest/
│   │   ├── main/
│   │   │   ├── java/com/fsvdevs/agrosphere/
│   │   │   │   ├── data/         # Data models, repositories, local data sources (Room, Prefs)
│   │   │   │   ├── di/           # Dependency Injection (if used)
│   │   │   │   ├── models/       # Data classes (SensorData, ActuatorData, etc.)
│   │   │   │   ├── repository/   # Repository implementations
│   │   │   │   ├── routes/       # Navigation routes definition
│   │   │   │   ├── ui/           # Jetpack Compose UI elements (Screens, Composables)
│   │   │   │   │   ├── dialog/   # Reusable dialog composables
│   │   │   │   │   └── theme/    # Theming (Color, Shape, Theme, Type)
│   │   │   │   ├── util/         # Utility classes (FirebaseHelper, NotificationHelper, etc.)
│   │   │   │   ├── viewmodel/    # ViewModels
│   │   │   │   └── MainActivity.kt # Main entry point activity
│   │   │   ├── res/              # Android resources (drawables, layouts, mipmap, values, etc.)
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── .gitignore
│   ├── build.gradle.kts          # App-level build script
│   └── proguard-rules.pro
├── build/
├── ESP32_AGROSPHERE_PROD/        # ESP32 Firmware Code
│   └── AGROSPHERE_PROD.ino
├── gradle/
│   └── wrapper/
├── .gitignore
├── build.gradle.kts              # Project-level build script
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
├── README.md                     # This file
├── settings.gradle.kts
└── gradle/libs.versions.toml     # Dependency versions catalog
```

## 10. Conclusion (Based on Prototype Validation)

AgroSphere successfully demonstrates the feasibility of an integrated IoT system for automated greenhouse monitoring and control within a prototype environment. The system met or exceeded performance expectations for real-time monitoring, responsive actuator control (both manual and automatic), logical automatic control execution, alert reliability, and system resilience during fault tolerance tests. The effective offline data buffering and synchronization capabilities, coupled with data transfer optimization via caching, highlight its potential for deployment in areas with unreliable connectivity. High usability scores affirm the user-centered design approach. While validated on a prototype, AgroSphere provides a robust blueprint and proof-of-concept for accessible smart agriculture technology, paving the way for future scaling and field testing to realize its potential in improving resource efficiency and supporting sustainable agriculture.
