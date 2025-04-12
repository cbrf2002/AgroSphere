# AgroSphere - Technical Documentation

## Overview

AgroSphere is a smart agriculture monitoring and control system application designed to provide real-time sensor data visualization and remote control of greenhouse/hydroponics environments. The app connects to IoT sensors and actuators through Firebase Realtime Database, allowing users to monitor environmental conditions and control climate systems for optimal plant growth.

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern with the following components:

- **Models**: Data classes representing various entities
- **Repositories**: Handle data operations and business logic
- **ViewModels**: Connect the UI with the data layer
- **UI**: Jetpack Compose based user interface components

### Technology Stack

- **UI Framework**: Jetpack Compose with Material 3
- **Programming Language**: Kotlin
- **Backend**: Firebase Realtime Database
- **Local Storage**: Room Database, DataStore Preferences
- **Asynchronous Operations**: Kotlin Coroutines and Flow
- **Charting**: MPAndroidChart library
- **Authentication**: Firebase Authentication with Google Sign-In
- **Dependency Injection**: Manual dependency injection
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: Android 10 (API level 29)
- **Target SDK**: Android 14 (API level 34)
- **Compose Compiler Version**: 1.5.15

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