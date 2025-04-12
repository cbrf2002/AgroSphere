# AgroSphere - Technical Documentation

## Overview

AgroSphere is a smart agriculture monitoring and control system application designed to provide real-time sensor data visualization and remote control of greenhouse/hydroponics environments. The app connects to IoT sensors and actuators through Firebase Realtime Database, allowing users to monitor environmental conditions and control climate systems for optimal plant growth.

## Architecture

The application follows a multi-tier architecture with three main components:

1. **Mobile Application Tier** (MVVM pattern):
   - **Models**: Data classes representing various entities
   - **Repositories**: Handle data operations and business logic
   - **ViewModels**: Connect the UI with the data layer
   - **UI**: Jetpack Compose based user interface components

2. **Cloud Service Tier**:
   - Firebase Realtime Database for data storage and synchronization
   - Firebase Authentication for secure access control
   - Server-generated timestamps for consistent time records

3. **Hardware Control Tier** (ESP32-based):
   - Sensor data acquisition and processing
   - Actuator control logic implementation
   - Data transmission to cloud services
   - Automatic climate control algorithms

This three-tier architecture enables real-time data flow, responsive control, and separation of concerns between the hardware, cloud, and user interface components.

### Technology Stack

#### Mobile Application
- **UI Framework**: Jetpack Compose with Material 3
- **Programming Language**: Kotlin
- **Backend Integration**: Firebase Realtime Database
- **Local Storage**: Room Database, DataStore Preferences
- **Asynchronous Operations**: Kotlin Coroutines and Flow
- **Charting**: MPAndroidChart library
- **Authentication**: Firebase Authentication with Google Sign-In
- **Dependency Injection**: Manual dependency injection
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: Android 10 (API level 29)
- **Target SDK**: Android 14 (API level 34)
- **Compose Compiler Version**: 1.5.15

#### Hardware Control System
- **Microcontroller**: ESP32 (Dual-core, 240MHz, with WiFi and BLE)
- **Firmware Framework**: Arduino core for ESP32
- **Cloud Integration**: Firebase Arduino Client Library
- **Sensor Libraries**:
  - DHT library (temperature/humidity)
  - OneWire & DallasTemperature (water temperature)
  - Wire & BH1750 (light measurement)
  - ESP32Servo (vent control)
- **Connectivity**: WiFi with SSL/TLS encryption
- **Time Synchronization**: NTP (Network Time Protocol)
- **Authentication**: Firebase User Authentication
- **Memory Management**: Dynamic buffer allocation and cleanup
- **Build System**: PlatformIO/Arduino IDE

## Core Components

### Models

1. **SensorData**
   - Contains real-time sensor readings:
     - Temperature (°C)
     - Humidity (%)
     - CO2 levels (ppm)
     - pH levels
     - Water temperature (°C)
     - Water level (boolean)
     - Light level (lux)
     - TDS (Total Dissolved Solids) (ppm)
     - Timestamp

2. **SensorRangeData**
   - Stores acceptable ranges for sensor values:
     - Temperature range (low/high)
     - Humidity range (low/high)
     - Preset name (for different crop profiles)

3. **ActuatorData**
   - Controls for climate systems:
     - Fan status (on/off)
     - Mist system status (on/off)
     - Vent status (open/closed)
     - Automatic control mode (on/off)

4. **NotificationData**
   - Local storage for alert notifications:
     - Title
     - Message
     - Timestamp

### Repositories

1. **SensorDataRepository**
   - Manages sensor data retrieval and caching
   - Features:
     - Multi-level caching (memory + SharedPreferences)
     - Historical data retrieval with time range filtering
     - Incremental data fetching for optimized performance
     - Cache invalidation strategies
     - Real-time Firebase listeners

2. **SensorRangeDataRepository**
   - Manages threshold settings for sensors
   - Handles preset profiles for different crops
   - Synchronizes settings with Firebase

3. **ActuatorDataRepository**
   - Controls system actuators (fan, mist, vents)
   - Synchronizes control states with Firebase
   - Manages automatic/manual mode settings

4. **NotificationRepository**
   - Handles local storage of alert notifications
   - Uses Room Database for persistent storage

### ViewModels

1. **SensorDataViewModel**
   - Processes and exposes sensor data to UI
   - Manages data fetching with different time ranges (Hour/Day/Week/Month/Year)
   - Handles loading states and error messages
   - Provides methods for cache refreshing

2. **SensorRangeDataViewModel**
   - Manages acceptable sensor range values
   - Handles updates to threshold settings

3. **ActuatorDataViewModel** (implied from repository)
   - Manages actuator states
   - Provides methods for toggling actuators

### UI Screens

1. **DashboardScreen**
   - Main control panel showing:
     - Current sensor readings with status indicators
     - Actuator control buttons (fan, mist, vent)
     - Climate control mode toggle (auto/manual)
     - Temperature and humidity range sliders
     - Preset selection

2. **MonitorScreen**
   - Historical data visualization with:
     - Time range selector tabs (Hour to Year)
     - Line charts for each sensor type
     - Data refresh controls
     - Loading indicators and error handling

## Key Features

### Real-time Monitoring
- Firebase listeners provide instant updates from sensors
- StateFlow objects propagate changes to the UI
- Efficient update throttling (5-second cooldown for sensor data)

### Historical Data Analysis
- Time-range based data retrieval (Hour to Year views)
- Line charts showing sensor value trends
- Custom formatters for time axis based on selected range

### Climate Control System
- Automatic mode: System regulates environment based on set thresholds
- Manual mode: User can directly control actuators (fan, mist, vent)
- Climate range settings: Customizable temperature and humidity ranges

### Preset Profiles
- Pre-configured settings for different crop types
- Custom "Manual Range" option for user-defined settings

### Alert System
- Real-time monitoring of sensor values against thresholds
- Alert notifications when values exceed safety ranges
- Notification cooldown mechanism to prevent alert spam (15 minutes)
- Local storage of alert history

### Caching System
- Multi-level caching strategy:
  - Memory cache for fastest access (1-minute lifetime)
  - SharedPreferences for persistent caching
  - Cache versioning for proper invalidation
  - Incremental fetching to optimize data transfer

## Data Flow

1. **Sensor Data Flow**:
   - IoT devices write sensor data to Firebase
   - Repository listeners detect changes
   - Repository updates StateFlow objects
   - ViewModels process and expose the data
   - UI components observe and display the data

2. **User Interaction Flow**:
   - User interacts with UI controls
   - ViewModel methods are called
   - Repository updates Firebase data
   - Firebase listeners detect changes
   - UI updates to reflect new states

3. **Alert System Flow**:
   - Repository listeners detect new sensor data
   - `checkSensorValuesAndNotify` compares values against thresholds
   - If thresholds exceeded, create notification
   - Store notification in Room Database
   - Display system notification to user

## Technical Implementation Details

### Optimized Data Retrieval
- Smart caching strategies reduce Firebase calls
- Incremental data fetching for efficient updates
- Fallback mechanisms for query failures
- Special handling for sparse data periods

### Responsive UI
- Material 3 design components
- Adaptive layouts for different screen sizes
- Loading indicators and error states

## Firebase Realtime Database Schema

The application uses Firebase Realtime Database with the following structure:
│
├── actuatorStates
│   ├── auto: boolean
│   ├── fan: boolean
│   ├── mist: boolean
│   └── vent: boolean
│
├── sensorData
│   ├── carbonDioxide: number
│   ├── humidity: number
│   ├── lightLevel: number
│   ├── pH: number
│   ├── tds: number
│   ├── temperature: number
│   ├── timestamp: number
│   ├── waterLevel: boolean
│   └── waterTemp: number
│
├── sensorHistory
│   ├── {unique-id-1}
│   │   ├── carbonDioxide: number
│   │   ├── humidity: number
│   │   ├── lightLevel: number
│   │   ├── pH: number
│   │   ├── tds: number
│   │   ├── temperature: number
│   │   ├── timestamp: number
│   │   ├── waterLevel: boolean
│   │   └── waterTemp: number
│   ├── {unique-id-2}
│   │   └── ...
│   └── ...
│
└── sensorRanges
    ├── humRangeHigh: number
    ├── humRangeLow: number
    ├── preset: string
    ├── tempRangeHigh: number
    └── tempRangeLow: number


## Core Components

### Models

1. **SensorData**
   - Contains real-time sensor readings:
     - Temperature (°C)
     - Humidity (%)
     - CO2 levels (ppm)
     - pH levels
     - Water temperature (°C)
     - Water level (boolean)
     - Light level (lux)
     - TDS (Total Dissolved Solids) (ppm)
     - Timestamp

2. **SensorRangeData**
   - Stores acceptable ranges for sensor values:
     - Temperature range (low/high)
     - Humidity range (low/high)
     - Preset name (for different crop profiles)

3. **ActuatorData**
   - Controls for climate systems:
     - Fan status (on/off)
     - Mist system status (on/off)
     - Vent status (open/closed)
     - Automatic control mode (on/off)

4. **NotificationData**
   - Local storage for alert notifications (Room database entity):
     - Title
     - Message
     - Timestamp
     - Auto-generated ID

### Repositories

1. **SensorDataRepository**
   - Manages sensor data retrieval and caching
   - Features:
     - Multi-level caching (memory with ConcurrentHashMap + SharedPreferences)
     - Historical data retrieval with time range filtering (Hour/Day/Week/Month/Year)
     - Incremental data fetching for optimized performance
     - Cache versioning for proper invalidation
     - Real-time Firebase listeners
     - Intelligent throttling (5-second cooldown for sensor data)
     - Query optimizations with fallback mechanisms
     - Specialized handling for sparse data periods

2. **SensorRangeDataRepository**
   - Manages threshold settings for sensors
   - Handles preset profiles for different crops
   - Synchronizes settings with Firebase
   - Real-time listeners for remote changes

3. **ActuatorDataRepository**
   - Controls system actuators (fan, mist, vents)
   - Synchronizes control states with Firebase
   - Manages automatic/manual mode settings
   - Provides direct control methods for UI components

4. **NotificationRepository**
   - Handles local storage of alert notifications
   - Uses Room Database for persistent storage
   - Singleton pattern implementation
   - Methods for retrieving, storing and clearing notifications

### ViewModels

1. **SensorDataViewModel**
   - Processes and exposes sensor data to UI
   - Manages data fetching with different time ranges (Hour/Day/Week/Month/Year)
   - Handles loading states and error messages
   - Provides methods for cache refreshing
   - Implements database change detection
   - Exposes sensor history as StateFlow

2. **SensorRangeDataViewModel**
   - Manages acceptable sensor range values
   - Handles updates to threshold settings
   - Enables preset selection and customization
   - Factory pattern for ViewModel creation

3. **ActuatorDataViewModel**
   - Manages actuator states
   - Provides methods for toggling actuators
   - Handles automatic/manual mode switching
   - Factory pattern for ViewModel creation

### UI Screens

1. **DashboardScreen**
   - Main control panel showing:
     - Current sensor readings with status indicators
     - Actuator control buttons (fan, mist, vent)
     - Climate control mode toggle (auto/manual)
     - Temperature and humidity range sliders
     - Preset profile selection
     - Last update timestamp display

2. **MonitorScreen**
   - Historical data visualization with:
     - Time range selector tabs (Hour/Day/Week/Month/Year)
     - Line charts for each sensor type
     - Data refresh controls
     - Loading indicators and error handling
     - Background data refresh mechanism
     - Resizable and interactive charts
     - Custom marker views for data points

3. **NotificationsScreen**
   - Historical alerts display:
     - Timestamp-ordered notification list
     - Notification deletion option
     - Confirmation dialog for bulk actions
     - Empty state handling

4. **PreferenceScreen**
   - Application settings:
     - Theme selection (Light/Dark/System)
     - Dynamic color toggle (Android 12+)
     - Notification settings access
     - Account information display
     - App information with author credits
     - Privacy policy and terms of service

5. **LoginScreen**
   - Authentication options:
     - Email/password sign-in
     - Google account integration
     - Account creation
     - Password reset functionality

## Key Features

### Authentication System
- Multiple sign-in methods (Email/Google)
- Secure credential handling
- Session management
- Password reset functionality
- Email verification

### Real-time Monitoring
- Firebase listeners provide instant updates from sensors
- StateFlow objects propagate changes to the UI
- Efficient update throttling (5-second cooldown for sensor data)
- Comprehensive sensor data display (temperature, humidity, CO2, pH, etc.)

### Historical Data Analysis
- Time-range based data retrieval (Hour to Year views)
- Line charts showing sensor value trends
- Custom formatters for time axis based on selected range
- Interactive data point markers
- Special handling for sparse data periods

### Climate Control System
- Automatic mode: System regulates environment based on set thresholds
- Manual mode: User can directly control actuators (fan, mist, vent)
- Climate range settings: Customizable temperature and humidity ranges
- Visual feedback for current system states

### Preset Profiles
- Pre-configured settings for different growth phases:
  - Seedling Phase (18-24°C, 60-70%)
  - Flowering Phase (20-25°C, 70-80%)
  - Vegetative Phase (22-29°C, 50-60%)
- Custom "Manual Range" option for user-defined settings

### Alert System
- Real-time monitoring of sensor values against thresholds
- Alert notifications when values exceed safety ranges:
  - Temperature out of range
  - Humidity out of range
  - High CO2 levels (>1000 ppm)
  - pH level out of range (ideal: 5.5-6.5)
  - Low water level
  - Insufficient light (<30000 lux)
- Notification cooldown mechanism (15 minutes) to prevent alert spam
- Local storage of alert history using Room Database
- Android system notifications with proper channels

### Caching System
- Multi-level caching strategy:
  - In-memory cache for fastest access (1-minute lifetime)
  - SharedPreferences for persistent caching
  - Cache versioning for proper invalidation
  - Incremental fetching to optimize data transfer
  - Fallback mechanisms when network requests fail

### Adaptive UI/UX
- Material 3 theming with dynamic color support
- Dark/light/system theme options
- Accessibility considerations (scaled density for text)
- Custom font families:
  - Maven Pro for display and headings
  - Roboto for body text
- Smooth animations and transitions between screens
- Splash screen with logo and app description

## Data Flow

1. **Sensor Data Flow**:
   - IoT devices write sensor data to Firebase
   - Repository listeners detect changes
   - Repository updates StateFlow objects
   - ViewModels process and expose the data
   - UI components observe and display the data

2. **User Interaction Flow**:
   - User interacts with UI controls
   - ViewModel methods are called
   - Repository updates Firebase data
   - Firebase listeners detect changes
   - UI updates to reflect new states

3. **Alert System Flow**:
   - Repository listeners detect new sensor data
   - `checkSensorValuesAndNotify` compares values against thresholds
   - If thresholds exceeded, create notification
   - Store notification in Room Database
   - Display system notification to user
   - Implement cooldown to prevent spam

## Technical Implementation Details

### Optimized Data Retrieval
- Smart caching strategies reduce Firebase calls
- Incremental data fetching for efficient updates
- Fallback mechanisms for query failures
- Special handling for sparse data periods
- Cache versioning for proper invalidation
- Memory and disk caching layers

### Navigation and UI Architecture
- Navigation controller with route definitions
- Animated transitions between screens
- Custom composable functions for reusable components
- Bottom navigation bar with four main sections
- Top app bar with context-appropriate actions
- Dialog components for settings and information

### Notification System
- Android notification channels for proper categorization
- Permission handling for notifications (Android 13+)
- Room database for local notification storage
- Group notifications for better organization
- Cooldown mechanism to prevent alert spam
- Custom notification icons and styling

### Theme and Styling
- Material 3 design implementation
- Dynamic color support for Android 12+
- Light/dark theme options with system default option
- Custom color scheme with semantic color roles
- Typography system with custom font families
- Consistent spacing and padding patterns
- Preference persistence using DataStore

### Build Configuration
- Gradle with Kotlin DSL
- Compose Compiler version: 1.5.15
- Min SDK: 29 (Android 10)
- Target SDK: 35 (Android 14+)
- Version: 1.49 (build 519)
- Key Dependencies:
  - AndroidX Core KTX
  - Compose Material 3
  - Room Database
  - DataStore Preferences
  - Navigation Compose
  - Firebase Auth/Database
  - MPAndroidChart
  - Google Play Services Auth
  - WorkManager

### Room Database Schema
╔═══════════════════════╗ 
║ NotificationData      ║ 
╠═══════════════════════╣ 
║ id: Integer (PK, auto)║ 
║ title: String         ║ 
║ message: String       ║ 
║ timestamp: Long       ║ 
╚═══════════════════════╝


## Project Structure

- **models/**: Data classes representing domain entities
- **viewmodel/**: ViewModels connecting UI with data layer
- **repository/**: Data access and business logic
- **ui/**: Compose UI components and screens
- **ui/dialog/**: Reusable dialog components
- **ui/theme/**: Theming, colors and typography
- **util/**: Utility functions and helper classes
- **dao/**: Data Access Objects for Room
- **room/**: Room database configuration
- **routes/**: Navigation route definitions

## Conclusion

AgroSphere provides a comprehensive solution for monitoring and controlling agricultural environments. Its architecture emphasizes efficiency, responsiveness, and reliability through smart caching, real-time updates, and fallback mechanisms. The application is designed to work seamlessly in both online and offline scenarios, providing users with valuable insights into their growing environment and precise control over climate conditions.

## ESP32 Firmware and Firebase Communication

The AgroSphere system uses an ESP32 microcontroller as the hardware interface between sensors, actuators, and the Firebase Realtime Database. The ESP32 firmware handles data collection, processing, transmission, and control logic execution based on settings received from the Android application.

### Hardware Components

The ESP32 interfaces with the following hardware components:

#### Sensors
- **DHT11**: Temperature and humidity monitoring
- **MQ135**: Carbon dioxide (CO2) measurement
- **BH1750**: Light intensity measurement
- **Float Switch**: Water level detection
- **DS18B20**: Water temperature measurement
- **PH4502C**: pH level measurement
- **TDS/EC Sensor**: Total Dissolved Solids measurement

#### Actuators
- **Ventilation Servo**: Controls greenhouse vent opening/closing
- **Fan Relay**: Controls circulation fan
- **Mist Pump Relay**: Controls hydroponic misting system

#### Physical Construction
- **ESP32 Development Board**: Main microcontroller
- **Prototype PCB**: For sensor and actuator connections
- **12V Power Supply**: Powering the system and actuators
- **3.3V/5V Regulators**: For sensor power requirements
- **Relay Module**: For high-current actuator control
- **Waterproof Enclosure**: For protecting electronics

### Pin Configuration

```cpp
//Sensor declarations
constexpr int mqPin = 34;      // MQ135 CO2 sensor
constexpr int lightPin = 21;    // BH1750 I2C interface
constexpr int wlPin = 19;       // Float switch water level
constexpr int wtPin = 23;       // DS18B20 water temperature
constexpr int phPin = 33;       // PH4502C pH sensor
constexpr int tdsPin = 32;      // TDS/EC sensor
constexpr int dhtPin = 12;      // DHT11 temperature/humidity

//Actuator declarations
constexpr int fanPin = 18;      // Fan relay control
constexpr int mistPin = 17;     // Mist pump relay control
constexpr int ventPin = 13;     // Servo motor for ventilation
```

### Firebase Communication Protocol

The ESP32 communicates with the Android application through Firebase Realtime Database, which serves as the central communication hub. The communication follows this pattern:

#### Authentication Flow
1. ESP32 connects to WiFi network
2. Authenticates with Firebase using email/password credentials
3. Receives authentication tokens for secure database access
4. Establishes persistent connection to the Realtime Database

#### Data Upload Cycle
1. ESP32 reads all sensor values at regular intervals (20 seconds)
2. Processes raw sensor readings into standardized units
3. Uploads current sensor values to `/sensorData` node
4. Periodically (every 60 seconds) creates timestamped entries in `/sensorHistory` node

#### Control Signal Reception
1. ESP32 monitors the `/actuatorStates` and `/sensorRanges` nodes for changes
2. Reads control mode setting (`auto` boolean value)
3. In manual mode: reads and applies actuator states directly
4. In automatic mode: uses sensor range thresholds to determine actuator states

### Firebase Database Structure Used by ESP32

```
│
├── actuatorStates
│   ├── auto: boolean     // Control mode (automatic vs manual)
│   ├── fan: boolean      // Fan state
│   ├── mist: boolean     // Mist system state
│   └── vent: boolean     // Ventilation state
│
├── sensorData            // Current sensor readings
│   ├── carbonDioxide: number
│   ├── humidity: number
│   ├── lightLevel: number
│   ├── pH: number
│   ├── temperature: number
│   ├── timestamp: number
│   ├── tds: number
│   ├── waterLevel: boolean
│   └── waterTemp: number
│
├── sensorHistory         // Historical time-series data
│   ├── m{timestamp}-{random}    // Unique ID for each entry
│   │   ├── carbonDioxide: number
│   │   ├── humidity: number
│   │   ├── lightLevel: number
│   │   ├── pH: number
│   │   ├── temperature: number
│   │   ├── timestamp: number
│   │   ├── tds: number
│   │   ├── waterLevel: boolean
│   │   └── waterTemp: number
│   └── ...
│
└── sensorRanges          // Threshold settings for automatic control
    ├── humRangeHigh: number
    ├── humRangeLow: number
    ├── preset: string
    ├── tempRangeHigh: number
    └── tempRangeLow: number
```

### Control Logic

The ESP32 firmware implements two control modes:

#### Manual Mode
When `actuatorStates/auto` is set to `false`:
- ESP32 directly applies the boolean values from Firebase:
  - `actuatorStates/fan` controls fan state
  - `actuatorStates/mist` controls mist pump state
  - `actuatorStates/vent` controls ventilation servo position

#### Automatic Mode
When `actuatorStates/auto` is set to `true`, the ESP32 implements climate control logic:

1. **Temperature Control Logic**
   - If temperature > `tempRangeHigh`:
     - Activate fan and open ventilation
     - If humidity < `humRangeLow`, activate mist for evaporative cooling
   - If temperature < `tempRangeLow`:
     - No cooling required
     - Control humidity as needed

2. **Humidity Control Logic**
   - If humidity < `humRangeLow`:
     - Activate mist system to increase humidity
   - If humidity > `humRangeHigh`:
     - Activate fan and open ventilation to reduce humidity

3. **Hysteresis Implementation**
   - Temperature margin: ±1.0°C around thresholds
   - Humidity margin: ±2.0% around thresholds
   - 2-second minimum delay between state changes
   - Prevents oscillation and rapid cycling of actuators
   - Extends equipment life and improves climate stability

#### Mist Pump Safety Cycle
To prevent overheating and equipment damage:
- When activated, mist pump runs for maximum 2 minutes
- After running, enforces 5-minute cooldown period
- Automatically manages duty cycle during extended operation

### Data Flow Between ESP32 and Android App

```
┌─────────────┐                  ┌──────────────────┐                  ┌──────────────┐
│             │  1. Read Sensors │                  │  5. Read Data    │              │
│             │─────────────────►│                  │◄─────────────────│              │
│             │                  │                  │                  │              │
│             │  2. Process Data │                  │  6. Display in   │              │
│    ESP32    │─────────────────►│  Firebase        │─────────────────►│  Android App │
│ Microcontrol│                  │  Realtime        │                  │              │
│    Board    │  3. Upload Data  │  Database        │  7. User Control │              │
│             │─────────────────►│                  │◄─────────────────│              │
│             │                  │                  │                  │              │
│             │  4. Read Settings│                  │  8. Save Settings│              │
│             │◄─────────────────│                  │◄─────────────────│              │
└─────────────┘                  └──────────────────┘                  └──────────────┘
```

1. **ESP32 to Firebase**:
   - Current sensor readings → `/sensorData` node (20-second intervals)
   - Historical sensor data → `/sensorHistory/{uniqueID}` (60-second intervals)
   - Actuator states in auto mode → `/actuatorStates/*` (real-time updates)

2. **Firebase to ESP32**:
   - Control mode setting → `actuatorStates/auto` (polled every 1 second)
   - Manual actuator controls → `actuatorStates/{fan|mist|vent}` (polled every 1 second)
   - Climate thresholds → `sensorRanges/*` (polled every 1 second)

3. **Firebase to Android App**:
   - Current sensor data → Real-time listeners on `/sensorData`
   - Actuator states → Real-time listeners on `/actuatorStates/*`
   - Historical data → Query-based fetching from `/sensorHistory`
   - Climate thresholds → Real-time listeners on `/sensorRanges/*`

4. **Android App to Firebase**:
   - User control inputs → Updates to `/actuatorStates/*`
   - Climate threshold settings → Updates to `/sensorRanges/*`

### Implementation Highlights

#### Fault Tolerance Features
- WiFi reconnection handling
- Firebase authentication refresh
- Sensor error detection and default values
- Watchdog timers to prevent system hangs

#### Memory Optimization
- Efficient string handling to prevent heap fragmentation
- Strategic use of static variables for repetitive operations
- Buffer management for sensors with multiple readings

#### Time Synchronization
- NTP time server synchronization
- Server-generated timestamps for consistent time records
- Millisecond precision for analytical accuracy

### Testing and Simulation Mode

The firmware includes a testing mode that can generate simulated sensor data:

```cpp
void updateMockSensorValues() {
  temperature = mockSensorValue(prevTemperature, 25.0, 27.0, 0.1);
  humidity = mockSensorValue(prevHumidity, 65.0, 70.0, 0.2);
  carbonDioxide = mockSensorValue(prevCarbonDioxide, 970.0, 1020.0, 5.0);
  lightLevel = mockSensorValue(prevLightLevel, 880.0, 1023.0, 10.0);
  float mockWater = mockSensorValue(prevWaterLevel, 10.0, 30.0, 1.0);
  waterLevel = (mockWater > 15.0);
  waterTemp = mockSensorValue(prevWaterTemp, 20.0, 25.0, 0.1);
  pH = mockSensorValue(prevPH, 6.0, 7.0, 0.05);
  tds = mockSensorValue(prevTDS, 0.8, 2.2, 0.05);
}
```

This allows for system testing without physical sensors and provides a consistent data stream for application development and demonstration purposes.

## Project
1. **ESP32 to Firebase**:
   - Current sensor readings → `/sensorData` node (20-second intervals)
   - Historical sensor data → `/sensorHistory/{uniqueID}` (60-second intervals)
   - Actuator states in auto mode → `/actuatorStates/*` (real-time updates)

2. **Firebase to ESP32**:
   - Control mode setting → `actuatorStates/auto` (polled every 1 second)
   - Manual actuator controls → `actuatorStates/{fan|mist|vent}` (polled every 1 second)
   - Climate thresholds → `sensorRanges/*` (polled every 1 second)

3. **Firebase to Android App**:
   - Current sensor data → Real-time listeners on `/sensorData`
   - Actuator states → Real-time listeners on `/actuatorStates/*`
   - Historical data → Query-based fetching from `/sensorHistory`
   - Climate thresholds → Real-time listeners on `/sensorRanges/*`

4. **Android App to Firebase**:
   - User control inputs → Updates to `/actuatorStates/*`
   - Climate threshold settings → Updates to `/sensorRanges/*`

### Implementation Highlights

#### Fault Tolerance Features
- WiFi reconnection handling
- Firebase authentication refresh
- Sensor error detection and default values
- Watchdog timers to prevent system hangs

#### Memory Optimization
- Efficient string handling to prevent heap fragmentation
- Strategic use of static variables for repetitive operations
- Buffer management for sensors with multiple readings

#### Time Synchronization
- NTP time server synchronization
- Server-generated timestamps for consistent time records
- Millisecond precision for analytical accuracy

### Testing and Simulation Mode

The firmware includes a testing mode that can generate simulated sensor data:

```cpp
void updateMockSensorValues() {
  temperature = mockSensorValue(prevTemperature, 25.0, 27.0, 0.1);
  humidity = mockSensorValue(prevHumidity, 65.0, 70.0, 0.2);
  carbonDioxide = mockSensorValue(prevCarbonDioxide, 970.0, 1020.0, 5.0);
  lightLevel = mockSensorValue(prevLightLevel, 880.0, 1023.0, 10.0);
  float mockWater = mockSensorValue(prevWaterLevel, 10.0, 30.0, 1.0);
  waterLevel = (mockWater > 15.0);
  waterTemp = mockSensorValue(prevWaterTemp, 20.0, 25.0, 0.1);
  pH = mockSensorValue(prevPH, 6.0, 7.0, 0.05);
  tds = mockSensorValue(prevTDS, 0.8, 2.2, 0.05);
}
```

This allows for system testing without physical sensors and provides a consistent data stream for application development and demonstration purposes.
 Structure

- **models/**: Data classes representing domain entities
- **viewmodel/**: ViewModels connecting UI with data layer
- **repository/**: Data access and business logic
- **ui/**: Compose UI components and screens
- **ui/dialog/**: Reusable dialog components
- **ui/theme/**: Theming, colors and typography
- **util/**: Utility functions and helper classes
- **dao/**: Data Access Objects for Room
- **room/**: Room database configuration
- **routes/**: Navigation route definitions

## Conclusion

AgroSphere provides a comprehensive solution for monitoring and controlling agricultural environments. The integrated system combines mobile application technology with IoT hardware control through ESP32 microcontrollers, creating a seamless experience for greenhouse management. Its architecture emphasizes efficiency, responsiveness, and reliability through smart caching, real-time updates, and fallback mechanisms. Both the mobile application and ESP32 firmware are designed to work together seamlessly in both online and offline scenarios, providing users with valuable insights into their growing environment and precise control over climate conditions.