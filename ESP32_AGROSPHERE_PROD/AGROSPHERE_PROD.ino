// ***********************************************************************************************************
// ****   AgroSphere: IoT-Based Automated Greenhouse Monitoring System with Internal Climate Control      ****
// ****   Project by: Fabian, Sumalinog, Vicente                                                          ****
// ****   Version 1.50 | 14/04/25                                                                         ****
// ***********************************************************************************************************

const char* root_ca = \
"-----BEGIN CERTIFICATE-----\n" \
"MIIFVzCCAz+gAwIBAgINAgPlk28xsBNJiGuiFzANBgkqhkiG9w0BAQwFADBHMQsw\n" \
"CQYDVQQGEwJVUzEiMCAGA1UEChMZR29vZ2xlIFRydXN0IFNlcnZpY2VzIExMQzEU\n" \
"MBIGA1UEAxMLR1RTIFJvb3QgUjEwHhcNMTYwNjIyMDAwMDAwWhcNMzYwNjIyMDAw\n" \
"MDAwWjBHMQswCQYDVQQGEwJVUzEiMCAGA1UEChMZR29vZ2xlIFRydXN0IFNlcnZp\n" \
"Y2VzIExMQzEUMBIGA1UEAxMLR1RTIFJvb3QgUjEwggIiMA0GCSqGSIb3DQEBAQUA\n" \
"A4ICDwAwggIKAoICAQC2EQKLHuOhd5s73L+UPreVp0A8of2C+X0yBoJx9vaMf/vo\n" \
"27xqLpeXo4xL+Sv2sfnOhB2x+cWX3u+58qPpvBKJXqeqUqv4IyfLpLGcY9vXmX7w\n" \
"Cl7raKb0xlpHDU0QM+NOsROjyBhsS+z8CZDfnWQpJSMHobTSPS5g4M/SCYe7zUjw\n" \
"TcLCeoiKu7rPWRnWr4+wB7CeMfGCwcDfLqZtbBkOtdh+JhpFAz2weaSUKK0Pfybl\n" \
"qAj+lug8aJRT7oM6iCsVlgmy4HqMLnXWnOunVmSPlk9orj2XwoSPwLxAwAtcvfaH\n" \
"szVsrBhQf4TgTM2S0yDpM7xSma8ytSmzJSq0SPly4cpk9+aCEI3oncKKiPo4Zor8\n" \
"Y/kB+Xj9e1x3+naH+uzfsQ55lVe0vSbv1gHR6xYKu44LtcXFilWr06zqkUspzBmk\n" \
"MiVOKvFlRNACzqrOSbTqn3yDsEB750Orp2yjj32JgfpMpf/VjsPOS+C12LOORc92\n" \
"wO1AK/1TD7Cn1TsNsYqiA94xrcx36m97PtbfkSIS5r762DL8EGMUUXLeXdYWk70p\n" \
"aDPvOmbsB4om3xPXV2V4J95eSRQAogB/mqghtqmxlbCluQ0WEdrHbEg8QOB+DVrN\n" \
"VjzRlwW5y0vtOUucxD/SVRNuJLDWcfr0wbrM7Rv1/oFB2ACYPTrIrnqYNxgFlQID\n" \
"AQABo0IwQDAOBgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4E\n" \
"FgQU5K8rJnEaK0gnhS9SZizv8IkTcT4wDQYJKoZIhvcNAQEMBQADggIBAJ+qQibb\n" \
"C5u+/x6Wki4+omVKapi6Ist9wTrYggoGxval3sBOh2Z5ofmmWJyq+bXmYOfg6LEe\n" \
"QkEzCzc9zolwFcq1JKjPa7XSQCGYzyI0zzvFIoTgxQ6KfF2I5DUkzps+GlQebtuy\n" \
"h6f88/qBVRRiClmpIgUxPoLW7ttXNLwzldMXG+gnoot7TiYaelpkttGsN/H9oPM4\n" \
"7HLwEXWdyzRSjeZ2axfG34arJ45JK3VmgRAhpuo+9K4l/3wV3s6MJT/KYnAK9y8J\n" \
"ZgfIPxz88NtFMN9iiMG1D53Dn0reWVlHxYciNuaCp+0KueIHoI17eko8cdLiA6Ef\n" \
"MgfdG+RCzgwARWGAtQsgWSl4vflVy2PFPEz0tv/bal8xa5meLMFrUKTX5hgUvYU/\n" \
"Z6tGn6D/Qqc6f1zLXbBwHSs09dR2CQzreExZBfMzQsNhFRAbd03OIozUhfJFfbdT\n" \
"6u9AWpQKXCBfTkBdYiJ23//OYb2MI3jSNwLgjt7RETeJ9r/tSQdirpLsQBqvFAnZ\n" \
"0E6yove+7u7Y/9waLd64NnHi/Hm3lCXRSHNboTXns5lndcEZOitHTtNCjv0xyBZm\n" \
"2tIMPNuzjsmhDYAPexZ3FL//2wmUspO8IFgV6dtxQ/PeEMMA3KgqlbbC1j+Qa3bb\n" \
"bP6MvPJwNQzcmRk13NfIRmPVNnGuV/u3gm3c\n" \
"-----END CERTIFICATE-----\n"; // Root CA for Firebase Realtime Database
 
// WiFi credentials
#define WIFI_SSID "Wifi"
#define WIFI_PASSWORD "Password"

// Firebase API key and Realtime Database project details
#define API_KEY "AIzaSyC2U4oWoR_zwYoojjNxPDEjQzYWmeq9Ws0"
#define FIREBASE_PROJECT_ID "agrosphere-fsvdev"
#define USER_EMAIL "cbrf.2002.2@gmail.com"
#define USER_PASSWORD "cbrf123456789"
#define FIREBASE_CLIENT_EMAIL "cbrf.2002@gmail.com"
#define DATABASE_URL "https://agrosphere-fsvdev-default-rtdb.asia-southeast1.firebasedatabase.app/"

// DHT sensor pin and type
#define DHTPIN 12
#define DHTTYPE DHT22 // Or DHT11 if DHT22 is not available

// Imports
#include <Arduino.h>
#include <WiFi.h>
#include <FS.h>
#include <SPIFFS.h>
#include <FirebaseClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>

#include <DHT.h>
#include <OneWire.h>
#include <BH1750.h>
#include <ESP32Servo.h>
#include <Wire.h>
#include <DallasTemperature.h>
#include <time.h>
 
const long  gmtOffset_sec = 28800; // GMT+8 for PHT
const int   daylightOffset_sec = 0;

// Sensor declarations
constexpr int mqPin = 34;         // MQ135
constexpr int lightPin = 21;      // BH1750
constexpr int wlPin = 19;         // Float switch
constexpr int wtPin = 23;         // DS18B20
constexpr int phPin = 33;         // PH4502C
constexpr int tdsPin = 32;        // TDS/EC - 3.3v
constexpr int randGen = 25;       // For sensor history directory randomizer

// SPIFFS file for offline history
#define OFFLINE_HISTORY_FILE "/offline_history.jsonl"
 
//TDS
#define VREF 3.3                  // Analog reference voltage
#define SCOUNT  30                // sample average
int analogBuffer[SCOUNT];         // store the analog value in the array, read from ADC
int analogBufferTemp[SCOUNT];     // temporary buffer for analog values
int analogBufferIndex = 0;
int copyIndex = 0;
float averageVoltage = 0;
float tdsValue = 0;
 
// Initialize sensors
BH1750 lightMeter;
OneWire oneWire(wtPin);
DallasTemperature sensors(&oneWire);
DHT dht(DHTPIN, DHTTYPE);
Servo ventServo;
 
// MQ135 sensor variables
int mq135Value = 0;
float mq135Voltage = 0;
float mq135PPM = 0;
 
// Actuator declarations
constexpr int fanPin = 18;        // Fan relay control
constexpr int mistPin = 17;       // Mist pump relay control
constexpr int ventPin = 13;       // Servo Motor for ventilation
 
// Function declarations
float mockSensorValue(float previousValue, float min, float max, float step);
float randomFloat(float min, float max);
int getMedianNum(int bArray[], int iFilterLen);
void ventServoControl(bool ventStatus, int servoAngle);
void timeStatusCB(uint32_t &ts);
void authHandler();
void handleActuators();
void printResult(AsyncResult &aResult);
void printError(int code, const String &msg);
String generateRandomID(int length);

// Declarations for offline handling
void initializeSPIFFS();
bool saveDataLocally(const JsonObject& sensorData);
void uploadOfflineHistory();
bool createSensorJson(JsonObject& doc, float temperature, float humidity, float carbonDioxide, float lightLevel, bool waterLevel, float waterTemp, float pH, float tds);
unsigned long long getCurrentTimestampEstimate();
void updateLastSuccessfulTime(unsigned long long firebaseTimestamp = 0);
bool uploadOfflineRecord(const JsonObject& sensorData);

// New Helper Function Declarations
bool checkFirebaseError(const String& operation);
bool setFirebaseNumber(const String& path, float value, int precision = 2);
bool setFirebaseBool(const String& path, bool value);
bool setFirebaseObject(const String& path, const object_t& value);
bool getFirebaseFloat(const String& path, float& value, float defaultValue);
bool getFirebaseBool(const String& path, bool& value, bool defaultValue);
bool readFirebaseTimestamp(const String& path, unsigned long long& timestamp);
bool validateSensorData(float temp, float hum, float waterT);

// Firebase authentication and configuration
DefaultNetwork network;
UserAuth user_auth(API_KEY, USER_EMAIL, USER_PASSWORD);

// Initialize Firebase and network objects
FirebaseApp app;
WiFiClientSecure ssl_client;
using AsyncClient = AsyncClientClass;
AsyncClient aClient(ssl_client, getNetwork(network));
RealtimeDatabase Database;
AsyncResult aResult_no_callback;

// Async Timers
unsigned long previousMillis = 0;
unsigned long wifiReconnectMillis = 0;
unsigned long sensorUploadMillis = 0;
unsigned long updateMillis = 0;
unsigned long mistPumpMillis = 0;
unsigned long lastMistStart = 0;
unsigned long actuatorFetchMillis = 0;
unsigned long previousFirestoreUpload = 0;

// Interval for sensor, actuator, and history updates
const long sensorInterval = 5000; // Every 5 seconds
const long actuatorInterval = 1000; // Every 1 second
const long sensorHistoryUploadInterval = 60000; // Every 60 seconds

// Sensor variables
float temperature, humidity, carbonDioxide, lightLevel, waterTemp, pH, tds;
bool waterLevel;
float tempRangeHigh, tempRangeLow, humRangeHigh, humRangeLow;

// Add variables to store last known valid ranges with defaults
float lastKnownTempRangeHigh = 25.0;
float lastKnownTempRangeLow = 20.0;
float lastKnownHumRangeHigh = 80.0;
float lastKnownHumRangeLow = 70.0;

// Variables to store the previous mock values
bool useMockValues = false;
float prevTemperature = 27.0, prevHumidity = 65.0, prevCarbonDioxide = 960.0;
float prevLightLevel = 900.0, prevWaterLevel = 20.0, prevWaterTemp = 22.0;
float prevPH = 6.5, prevTDS = 100.0;

// Actuator variables
bool fanStatus, mistStatus, ventStatus, isAuto;
bool misting = false;
int servoAngle;

// Offline state tracking
bool isOffline = true;          // Start assuming offline until connection confirmed
bool lastOnlineState = false;   // Track previous online state for transitions

// Tracker for reconnects
int reconnectAttempts = 0;
const int maxReconnectAttempts = 20;
bool wasConnected = false;

// Variables for approximate time tracking
unsigned long long lastSuccessfulTimestamp = 0;     // Store epoch ms
unsigned long millisAtLastSuccessfulTimestamp = 0;  // Store millis() at that time

// Variables for Internet Connectivity Check
bool internetAvailable = true;                              // Assume available if WiFi is connected, until check fails
unsigned long lastInternetCheckMillis = 0;
const unsigned long internetCheckInterval = 5000;           // Check every 5 seconds
unsigned long firstInternetFailMillis = 0;
const unsigned long internetFailConfirmDuration = 10000;    // 10 seconds to confirm internet outage

// Function declaration for internet check
void checkInternetConnection();

// Mock values for sensors simulation
float mockSensorValue(float previousValue, float min, float max, float step) {
    float newValue = previousValue + randomFloat(-step, step);   // Slight change
    // Ensure the value stays within bounds
    if (newValue < min) newValue = min;
    if (newValue > max) newValue = max;
    return newValue;
}

// Function to generate random float values
float randomFloat(float min, float max) {
    return min + static_cast<float>(rand()) / (static_cast<float>(RAND_MAX / (max - min)));
}

// TDS average voltage calculation
int getMedianNum(int bArray[], int iFilterLen) {
  int bTab[iFilterLen];
  for (byte i = 0; i < iFilterLen; i++) {
    bTab[i] = bArray[i];
  }

  int i, j, bTemp;
  for (j = 0; j < iFilterLen - 1; j++) {
    for (i = 0; i < iFilterLen - j - 1; i++) {
      if (bTab[i] > bTab[i + 1]) {
        bTemp = bTab[i];
        bTab[i] = bTab[i + 1];
        bTab[i + 1] = bTemp;
      }
    }
  }
  if ((iFilterLen & 1) > 0) {
    bTemp = bTab[(iFilterLen - 1) / 2];
  } else {
    bTemp = (bTab[iFilterLen / 2] + bTab[iFilterLen / 2 - 1]) / 2;
  }
  return bTemp;
}

// Authentication handler
void authHandler() {
  unsigned long startMillis = millis();
  const unsigned long timeout = 120000; // 2-minute timeout for auth/init

  Serial.println("Entering authHandler...");

  // Ensure JWT processing happens while waiting for app readiness
  while (app.isInitialized() && !app.ready() && (millis() - startMillis < timeout)) {
      Serial.print("authHandler: Waiting for Firebase App to be ready... ");
      JWT.loop(app.getAuth()); // Process JWT events (like refresh)
      // Optional: Add a small delay if needed, but JWT.loop should be non-blocking mostly
      delay(100);
      // Check result if needed, though app.ready() is the main indicator
      // printResult(aResult_no_callback); // Can be noisy, use if debugging specific auth issues
      Serial.println(app.ready() ? "Ready!" : "Not Ready.");
  }

  if (!app.ready()) {
      Serial.printf("authHandler: Firebase App failed to become ready within %lu ms.\n", timeout);
      // Optionally check the last error if available
      if (aClient.lastError().code() != 0) {
           Serial.print("authHandler: Last Firebase error: ");
           printError(aClient.lastError().code(), aClient.lastError().message());
      } else if (app.isInitialized()) {
           Serial.println("authHandler: App initialized but not ready. Possible token issue or network problem.");
      } else {
           Serial.println("authHandler: App not initialized.");
      }
  } else {
      Serial.println("authHandler: Firebase App is ready.");
  }
}

// Function to print AsyncResult (Ensure this is present if used by authHandler)
void printResult(AsyncResult &aResult) {
  if (aResult.isEvent()) {
    Firebase.printf("Event task: %s, msg: %s, code: %d\n", aResult.uid().c_str(), aResult.appEvent().message().c_str(), aResult.appEvent().code());
  }

  if (aResult.isDebug()) {
    Firebase.printf("Debug task: %s, msg: %s\n", aResult.uid().c_str(), aResult.debug().c_str());
  }

  if (aResult.isError()) {
    Firebase.printf("Error task: %s, msg: %s, code: %d\n", aResult.uid().c_str(), aResult.error().message().c_str(), aResult.error().code());
  }

  if (aResult.available()) {
    Firebase.printf("task: %s, payload: %s\n", aResult.uid().c_str(), aResult.c_str());
  }
}

// Function to print errors
void printError(int code, const String &msg) {
  Firebase.printf("Error, msg: %s, code: %d\n", msg.c_str(), code);
}

// Checks the last Firebase operation error and prints a message if an error occurred.
bool checkFirebaseError(const String& operation) {
    if (aClient.lastError().code() == 0) {
        return true;
    } else {
        Serial.printf("Firebase error during %s: ", operation.c_str());
        printError(aClient.lastError().code(), aClient.lastError().message());
        return false;
    }
}

// Firebase RTDB number set
bool setFirebaseNumber(const String& path, float value, int precision) {
    Database.set<number_t>(aClient, path, number_t(value, precision));
    return checkFirebaseError("set number at " + path);
}

// Firebase RTDB boolean set
bool setFirebaseBool(const String& path, bool value) {
    Database.set<bool>(aClient, path, value);
    return checkFirebaseError("set boolean at " + path);
}

// Firebase RTDB object set
bool setFirebaseObject(const String& path, const object_t& value) {
    Database.set<object_t>(aClient, path, value);
    return checkFirebaseError("set object at " + path);
}

// Firebase RTDB float get
bool getFirebaseFloat(const String& path, float& value, float defaultValue) {
    float fetchedValue = Database.get<float>(aClient, path);
    if (checkFirebaseError("get float from " + path)) {
        value = fetchedValue;
        return true;
    } else {
        value = defaultValue; // Use default on failure
        return false;
    }
}

// Firebase RTDB boolean get
bool getFirebaseBool(const String& path, bool& value, bool defaultValue) {
    bool fetchedValue = Database.get<bool>(aClient, path);
    if (checkFirebaseError("get boolean from " + path)) {
        value = fetchedValue;
        return true;
    } else {
        value = defaultValue; // Use default on failure
        return false;
    }
}

// Firebase timestamp get
bool readFirebaseTimestamp(const String& path, unsigned long long* timestampPtr) {
    // Allow some time for the server timestamp to be processed
    delay(100);
    double fbTimestampDouble = Database.get<double>(aClient, path);

    if (checkFirebaseError("read back timestamp from " + path)) {
        unsigned long long firebaseTimestamp = (unsigned long long)fbTimestampDouble;
        Serial.printf("Successfully read back Firebase timestamp: %llu\n", firebaseTimestamp);
        updateLastSuccessfulTime(firebaseTimestamp); // Update sync point with accurate time
        if (timestampPtr != nullptr) {
            *timestampPtr = firebaseTimestamp;
        }
        return true;
    } else {
        Serial.println("Failed to read back Firebase timestamp. Using estimation for sync.");
        updateLastSuccessfulTime(); // Fallback to estimation
        if (timestampPtr != nullptr) {
            *timestampPtr = 0; // Indicate failure if pointer provided
        }
        return false;
    }
}

// Sensor data validation (for invalid readings)
bool validateSensorData(float temp, float hum, float waterT) {
    if (temp == 0.0 || hum == 0.0 || waterT == -127.0 || waterT == 85.0) {
        Serial.printf("VALIDATION FAILED: Temp=%.2f, Hum=%.2f, WaterT=%.2f\n", temp, hum, waterT);
        return false;
    }
    return true;
}

// Setup function
void setup() {
  Serial.begin(115200);
  delay(1000);            // Allow time for serial to initialize

  // Initialize SPIFFS first
  initializeSPIFFS();

  // Set up WiFi event handlers for better connection management
  WiFi.onEvent([](WiFiEvent_t event, WiFiEventInfo_t info) {
    Serial.print("WiFi lost connection. Reason: ");
    Serial.println(info.wifi_sta_disconnected.reason);
    wasConnected = false;                                     // Mark as disconnected
    isOffline = true;                                         // Set offline state
  }, WiFiEvent_t::ARDUINO_EVENT_WIFI_STA_DISCONNECTED);

  // Once wifi connection were successful
  WiFi.onEvent([](WiFiEvent_t event, WiFiEventInfo_t info) {
    Serial.println("WiFi connected successfully");
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
    wasConnected = true;                                      // Mark as connected
    isOffline = false;                                        // Set online state
    reconnectAttempts = 0;                                    // Reset reconnect attempts on successful connection
  }, WiFiEvent_t::ARDUINO_EVENT_WIFI_STA_GOT_IP);

  // Connect to Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");

  int wifiAttempts = 0;
  while (WiFi.status() != WL_CONNECTED && wifiAttempts < 20) {
    Serial.print(".");
    delay(500);
    wifiAttempts++;
  }

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println();
    Serial.println("Failed to connect to WiFi initially. Starting in offline mode.");
    isOffline = true;
  } else {
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();
    isOffline = false; // Connected, set online state
  }
  lastOnlineState = !isOffline; // Initialize last state

  // Initialize I2C with explicit SDA/SCL pins and lower clock speed for DS18B20
  Wire.begin(21, 22, 100000); // Use 100kHz

  // Initialize Firebase only if connected
  if (!isOffline) {
    Serial.println("Attempting Firebase Initialization...");
    Firebase.printf("Firebase Client v%s\n", FIREBASE_CLIENT_VERSION);

    // Configure secure connection
    Serial.println("Attempting secure connection with certificate validation...");
    ssl_client.setCACert(root_ca);

    // Verify user credentials directly during app initialization
    Serial.print("Verifying user... ");

    // Initialize Firebase App - This implicitly verifies credentials
    Serial.println("Initializing app...");
    initializeApp(aClient, app, getAuth(user_auth), aResult_no_callback);
    authHandler(); // Wait for initialization and authentication

    // Check if initialization and authentication were successful
    if (app.ready()) {
        Serial.println("Secure connection and authentication successful!");
        // Print auth info
        Serial.println("Authentication Information");
        Serial.printf("User UID: %s\n", app.getUid().c_str());
        Serial.printf("Auth Token: %s\n", app.getToken().c_str());
        Serial.printf("Refresh Token: %s\n", app.getRefreshToken().c_str());

        app.getApp<RealtimeDatabase>(Database);
        aClient.setAsyncResult(aResult_no_callback);
        Database.url(DATABASE_URL);
        updateLastSuccessfulTime(); // Try to get an initial timestamp
    } else {
        Serial.println("Firebase initialization or authentication failed.");
        Serial.println("Check credentials, certificate, time sync, and network connection.");
        Serial.println("Continuing in offline mode...");
        isOffline = true; // Mark as offline if Firebase init fails
    }
  } else {
      Serial.println("Skipping Firebase initialization due to no WiFi connection.");
  }

  pinInitialization(); // Initialize the pins

  // Initialize random number generator with a semi-random seed
  randomSeed(analogRead(randGen) + millis());
}

// Main loop function
void loop() {
  unsigned long currentMillis = millis();

  // Network Connection Management
  bool wifiConnected = (WiFi.status() == WL_CONNECTED);

  // Update overall offline state: Offline if WiFi is down OR internet is confirmed unavailable
  isOffline = !wifiConnected || !internetAvailable;

  // This block handles transitions primarily driven by WiFi status changes OR
  // the initial detection of internet restoration by checkInternetConnection.
  if (isOffline != lastOnlineState) {                         // State changed (either Wi-Fi or Internet availability)
    if (!isOffline) {
      Serial.println("Network connection restored (Wi-Fi and Internet).");
      // Reset potential failure timer on confirmed restoration
      firstInternetFailMillis = 0;
      // Re-initialize Firebase if it wasn't initialized or failed before
      if (!app.ready()) {
          Serial.println("Re-initializing Firebase connections...");
          // Ensure certificate is set before initializing
          ssl_client.setCACert(root_ca);                      // Re-apply cert just in case
          initializeApp(aClient, app, getAuth(user_auth), aResult_no_callback);
          authHandler();
          if (app.ready()) {
              app.getApp<RealtimeDatabase>(Database);
              aClient.setAsyncResult(aResult_no_callback);
              Database.url(DATABASE_URL);
              Serial.println("Firebase re-initialized successfully.");
              updateLastSuccessfulTime();                     // Attempt time sync immediately
          } else {
              Serial.println("Firebase re-initialization failed.");
              isOffline = true;                               // Stay offline if re-init fails
              internetAvailable = false;                      // Assume internet issue if Firebase fails
          }
      }

      // Fetch 'isAuto' state immediately after confirming online and Firebase ready
      if (app.ready()) {                                      // Proceed only if Firebase is ready
          Serial.println("Fetching latest 'auto' mode state from Firebase (after reconnect)...");
          bool firebaseAutoState = Database.get<bool>(aClient, "/actuatorStates/auto");
          if (aClient.lastError().code() == 0) {              // Check error code
              isAuto = firebaseAutoState;
              Serial.printf("Successfully fetched 'auto' state: %s\n", isAuto ? "true" : "false");
          } else {
              Serial.println("Failed to fetch 'auto' state from Firebase after reconnect. Defaulting to auto mode locally.");
              printError(aClient.lastError().code(), aClient.lastError().message());
              isAuto = true;                                  // Default to auto if fetch fails
          }

          // Attempt to upload any stored offline data
          uploadOfflineHistory();
      }
    } else {
      // Transitioned from Online to Offline (due to Wi-Fi loss OR internet loss confirmation)
      Serial.println("Network connection lost or internet unavailable. Switching to offline auto mode.");
      isAuto = true; // Force auto mode when offline
      firstInternetFailMillis = 0;
    }
    lastOnlineState = isOffline;                              // Update the last known state
  }

  // Simplified Wi-Fi Reconnection Logic (runs only if Wi-Fi specifically is disconnected)
  if (!wifiConnected && currentMillis - wifiReconnectMillis >= 5000) {
      wifiReconnectMillis = currentMillis;
      Serial.println("Attempting Wi-Fi reconnection...");
      WiFi.reconnect();
  }

  // Handle Firebase auth tasks only when online (Wi-Fi and Internet) and initialized
  if (!isOffline && app.isInitialized()) {
      // *** Call JWT.loop() frequently when online and initialized ***
      JWT.loop(app.getAuth()); // Allow the library to manage token refresh

      // Explicitly check if Firebase App is ready (includes auth token validity) AFTER processing JWT events
      if (!app.ready()) {
          Serial.println("Firebase App not ready (token likely expired or auth issue). Attempting auth handler...");
          authHandler();                                      // Try to re-authenticate/refresh token using the refined handler
          // Re-check readiness after attempting auth
          if (!app.ready()) {
              Serial.println("Auth handler failed to make Firebase App ready. Potential persistent auth issue.");
              // Consider marking internet as unavailable if auth consistently fails
              internetAvailable = false;
              isOffline = true;                               // Force offline state due to auth failure
              if (isOffline != lastOnlineState) { // Trigger offline transition logic if state changed
                  Serial.println("Network connection lost or internet unavailable due to auth failure. Switching to offline auto mode.");
                  isAuto = true;                              // Force auto mode when offline
                  firstInternetFailMillis = 0;
                  lastOnlineState = isOffline;
              }
          } else {
              Serial.println("Auth handler successful. Firebase App is ready again.");
              // If auth success made us online again, ensure state reflects this
              if (wifiConnected && internetAvailable) { // Check underlying network state too
                  isOffline = false;
                  if (isOffline != lastOnlineState) {
                       lastOnlineState = isOffline;
                       // Optionally trigger actions needed when coming back online after auth fix
                       uploadOfflineHistory();
                  }
              } else {
                  // Auth succeeded but network is still down? Should be rare.
                  Serial.println("Warning: Auth succeeded but network check indicates offline.");
                  isOffline = true; // Keep offline state consistent with network checks
                  lastOnlineState = isOffline;
              }
          }
      }
  }

  // Sensor Reading and Actuator Control Interval
  if (currentMillis - updateMillis >= actuatorInterval) {
    updateMillis = currentMillis;

    // Perform internet check only if Wi-Fi is connected. Allows detection of restoration.
    if (wifiConnected && currentMillis - lastInternetCheckMillis >= internetCheckInterval) {
        lastInternetCheckMillis = currentMillis;
        checkInternetConnection(); // Check if we can reach Firebase
        // Re-evaluate isOffline state immediately after check
        bool previousOfflineState = isOffline; // Store state before re-evaluation
        isOffline = !wifiConnected || !internetAvailable;
        // Check if the state *changed* due to the check (either outage confirmed or restored)
        if (isOffline != previousOfflineState) { // Use state before re-evaluation for comparison
             if (isOffline) { // Outage confirmed
                 Serial.println("Internet check confirmed outage. Switching to offline auto mode.");
                 isAuto = true;
                 firstInternetFailMillis = 0; // Reset when offline
                 // Read sensors before handling actuators in offline mode
                 if (useMockValues) updateMockSensorValues(); else runSensors();
                 handleActuators(); // Run actuators in offline mode
                 goto endActuatorInterval; // Skip Firebase reads below
             } else { // Internet restored
                 Serial.println("Internet check confirmed restoration.");
                 // Since WiFi was already connected, Firebase should ideally be ready.
                 // Perform necessary "back online" actions immediately.
                 firstInternetFailMillis = 0; // Ensure reset
                 if (app.ready()) {
                     // Fetch the latest 'auto' state from Firebase immediately after internet restoration
                     Serial.println("Fetching latest 'auto' mode state after internet restoration...");
                     bool firebaseAutoState = Database.get<bool>(aClient, "/actuatorStates/auto");
                     if (aClient.lastError().code() == 0) {
                         isAuto = firebaseAutoState;
                         Serial.printf("Successfully fetched 'auto' state: %s\n", isAuto ? "true" : "false");
                     } else {
                         Serial.println("Failed to fetch 'auto' state after internet restoration. Defaulting to auto mode locally.");
                         printError(aClient.lastError().code(), aClient.lastError().message());
                         isAuto = true; // Default to auto if fetch fails
                     }
                     // Attempt to upload any stored offline data
                     uploadOfflineHistory(); // Trigger upload immediately
                 } else {
                     Serial.println("Warning: Internet restored but Firebase not ready. Re-init might be needed.");
                     // The main transition logic might handle re-init if needed later.
                 }
                 // Update lastOnlineState here to prevent main transition logic re-running this
                 lastOnlineState = isOffline;
             }
        }
    }

    // Fetch 'isAuto' state periodically when online and stable
    if (!isOffline && app.ready() && firstInternetFailMillis == 0) {
        Serial.println("Checking 'auto' mode state from Firebase...");
        bool fetchedAutoState = Database.get<bool>(aClient, "/actuatorStates/auto");
        if (aClient.lastError().code() == 0) {
            if (isAuto != fetchedAutoState) {
                Serial.printf("Mode changed via Firebase: %s -> %s\n", isAuto ? "Auto" : "Manual", fetchedAutoState ? "Auto" : "Manual");
                isAuto = fetchedAutoState; // Update local state
            } else {
                 Serial.println("'auto' mode state unchanged.");
            }
        } else {
            Serial.println("Failed to fetch 'auto' state. Keeping previous mode.");
            printError(aClient.lastError().code(), aClient.lastError().message());
            // Keep the current isAuto value if fetch fails
        }
    }

    // Decision whether to use the mock value sensor simulation or real sensor readings from pins
    if (useMockValues) {
      updateMockSensorValues();
    } else {
      runSensors();
    }

    // Update ranges only when online AND internet connection is confirmed stable
    if (!isOffline && app.ready() && firstInternetFailMillis == 0) {
        tempRangesUpdate();
    } else if (!isOffline && app.ready() && firstInternetFailMillis != 0) {
        Serial.println("Skipping Firebase reads (ranges) - internet connection unstable.");
    }
    else {
        // Offline: Use default/last known ranges and force auto mode
        isAuto = true; // Ensure isAuto is true if offline
        if (!wifiConnected) {
            Serial.println("Operating in offline mode (No Wi-Fi).");
        } else {
            // This case means wifi is connected but internetAvailable is false
            Serial.println("Operating in offline mode (No Internet). Using last known ranges.");
        }
    }

    handleActuators(); // Handle actuators based on current (potentially just updated) isAuto state
    endActuatorInterval:; // Label for goto jump
  }

  // Sensor Data Upload Interval (Current Values)
  if (currentMillis - sensorUploadMillis >= sensorInterval) {
    Serial.print("ESP Free Heap: ");
    Serial.println(ESP.getFreeHeap());
    sensorUploadMillis = currentMillis;

    // Upload only if online (Wi-Fi and Internet confirmed stable) and Firebase ready
    if (!isOffline && app.ready() && firstInternetFailMillis == 0) { // Added check for app.ready() here too
        Serial.println("Uploading current sensor data to Firebase...");
        // setSensorData handles calling updateLastSuccessfulTime internally on success
        setSensorData(temperature, humidity, carbonDioxide, lightLevel, waterLevel, waterTemp, pH, tds);
        // Removed explicit call to updateLastSuccessfulTime here
    } else {
        if (!app.ready() && !isOffline) {
             Serial.println("Offline: Skipping current sensor data upload (Firebase not ready).");
        } else if (firstInternetFailMillis != 0) {
            Serial.println("Offline: Skipping current sensor data upload (Internet unstable).");
        } else {
            Serial.println("Offline: Skipping current sensor data upload.");
        }
    }
  }

  // Sensor History Upload Interval (Historical Values)
  if (currentMillis - previousFirestoreUpload >= sensorHistoryUploadInterval) {
    previousFirestoreUpload = currentMillis; // Update timer immediately
    // Prepare data regardless of connection status
    DynamicJsonDocument doc(1024);
    JsonObject sensorData = doc.to<JsonObject>();
    bool dataValid = createSensorJson(sensorData, temperature, humidity, carbonDioxide, lightLevel, waterLevel, waterTemp, pH, tds);

    if (dataValid) {
        // Try to upload if online and stable, otherwise save locally
        if (!isOffline && app.ready() && firstInternetFailMillis == 0) { // Check stable online state and app.ready() for upload
            Serial.println("Attempting to upload historical sensor data...");
            // uploadSensorDataToRealtimeDatabase calling updateLastSuccessfulTime internally on success
            bool success = uploadSensorDataToRealtimeDatabase(sensorData);
            if (success) {
                Serial.println("Historical sensor data uploaded successfully.");
                // Removed explicit call to updateLastSuccessfulTime here
            } else {
                Serial.println("Historical data upload failed. Saving locally.");
                saveDataLocally(sensorData); // Save if upload fails
            }
        } else { // Save locally if offline OR internet unstable OR Firebase not ready
             if (!app.ready() && !isOffline) {
                 Serial.println("Offline: Saving historical sensor data locally (Firebase not ready).");
             } else if (isOffline) {
                 if (!wifiConnected) {
                     Serial.println("Offline: Saving historical sensor data locally (No WiFi).");
                 } else { // WiFi connected, but internet down
                     Serial.println("Offline: Saving historical sensor data locally (No Internet).");
                 }
             } else { // Not offline, but internet unstable (firstInternetFailMillis != 0)
                 Serial.println("Offline: Saving historical sensor data locally (Internet unstable).");
             }
            saveDataLocally(sensorData);
        }
    } else {
        Serial.println("Skipping history upload/save due to invalid sensor data.");
    }
  }

  // Mist pump control runs regardless of network state
  mistPumpControl(mistStatus);
}
 
void pinInitialization() {
  // Initialize I2C again with safer parameters just to be sure
  Wire.begin(21, 22, 100000); // Lower clock speed for more reliable I2C
  
  // Try alternate address for BH1750 (some modules use 0x5C instead of 0x23)
  if (!lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE, 0x23)) {
    Serial.println("Could not find BH1750 sensor at default address 0x23, trying alternate address 0x5C...");
    if (!lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE, 0x5C)) {
      Serial.println("Could not find BH1750 sensor!");
      // Continue anyway, we'll use mock values if needed
    } else {
      Serial.println("BH1750 sensor found at alternate address 0x5C");
    }
  } else {
    Serial.println("BH1750 sensor initialized successfully at address 0x23");
  }
  
  // Initialize other sensors
  sensors.begin();
  dht.begin();
  servoAngle = 0;
  Serial.print("Servo status: ");
  Serial.println(servoAngle);
 
  pinMode(wlPin, INPUT_PULLUP); //Normally closed setup
  pinMode(tdsPin, INPUT);
  pinMode(fanPin, OUTPUT);
  pinMode(mistPin, OUTPUT);
  pinMode(phPin, INPUT);
  ventServo.attach(ventPin);
}

// Mock value sensor simulation with ranges
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
 
  prevTemperature = temperature;
  prevHumidity = humidity;
  prevCarbonDioxide = carbonDioxide;
  prevLightLevel = lightLevel;
  prevWaterLevel = mockWater;
  prevWaterTemp = waterTemp;
  prevPH = pH;
  prevTDS = tds;
}

// Real sensor readings
void runSensors() {
  temperature = readTemperature();    // DHT11/22
  humidity = readHumidity();          // DHT11/22
  carbonDioxide = readCO2();          // MQ135
  lightLevel = readLightLevel();      // BH1750
  waterLevel = readWaterLevel();      // Float Switch
  waterTemp = readWaterTemperature(); // DS18B20
  pH = readPHLevel();                 // PH4502C
  tds = readTDS();                    // TDS/EC sensor
 
  // Print sensor data for debugging
  Serial.printf("Temperature: %.2f°C, Humidity: %.2f%%, CO2: %.2fppm, Light: %.2f lx, Water Temp: %.2f°C, pH: %.2f, TDS: %.2fppm\n",
                temperature, humidity, carbonDioxide, lightLevel, waterTemp, pH, tds);
}
 
void handleActuators() {
  if (isAuto) {                                 // Check the potentially updated isAuto state
      if (isOffline) {                          // Fallback to auto mode if device is offline.
          Serial.println("Handling actuators in forced offline auto mode.");
      } else {
          Serial.println("Handling actuators in online auto mode.");
      }
      controlActuatorAuto();
  } else {                                      // Manual Mode
      if (isOffline) {                          // Should not happen if offline logic forces isAuto = true, but handle defensively
          Serial.println("Warning: In manual mode while offline? Forcing actuators OFF for safety.");
          digitalWrite(fanPin, LOW);
          mistPumpControl(false);               // Turn off mist
          ventServoControl(false, servoAngle);  // Close vent
      } else if (app.ready()) {                 // Online, Firebase ready, and in Manual mode
          // Fetch the status of the actuators from Firebase
          // ONLY if internet is not suspected unstable
          if (firstInternetFailMillis == 0) {
              Serial.println("Handling actuators in manual mode (fetching states)...");
              getFirebaseBool("/actuatorStates/fan", fanStatus, fanStatus);
              getFirebaseBool("/actuatorStates/mist", mistStatus, mistStatus);
              getFirebaseBool("/actuatorStates/vent", ventStatus, ventStatus);

              // Debugging logs after attempting fetches
              Serial.println("Manual Actuator Status (fetched or previous):");
              Serial.print("Fan: "); Serial.println(fanStatus ? "ON" : "OFF");
              Serial.print("Mist: "); Serial.println(mistStatus ? "ON" : "OFF");
              Serial.print("Vent: "); Serial.println(ventStatus ? "ON" : "OFF");

          } else {
              Serial.println("Handling actuators in manual mode (skipping fetch - internet unstable). Using last known states.");
              // Keep last known fanStatus, mistStatus, ventStatus
              Serial.println("Manual Actuator Status (last known due to unstable internet):");
              Serial.print("Fan: "); Serial.println(fanStatus ? "ON" : "OFF");
              Serial.print("Mist: "); Serial.println(mistStatus ? "ON" : "OFF");
              Serial.print("Vent: "); Serial.println(ventStatus ? "ON" : "OFF");
          }

          // Apply actuator logic based on fetched/last known state
          digitalWrite(fanPin, fanStatus ? HIGH : LOW);
          mistPumpControl(mistStatus);
          ventServoControl(ventStatus, servoAngle);
      } else {
          // Online but Firebase not ready (should ideally not happen here due to checks in loop)
          Serial.println("Warning: Online but Firebase not ready in handleActuators (Manual Mode). Defaulting to safety (actuators off).");
          digitalWrite(fanPin, LOW);
          mistPumpControl(false); // Turn off mist
          ventServoControl(false, servoAngle); // Close vent
      }
  }
}

bool setSensorData(float temperature, float humidity, float carbonDioxide, float lightLevel, bool waterLevel, float waterTemp, float pH, float tds) {
  // Use server timestamp for current data uploads
  object_t ts_json;
  JsonWriter writer;
  writer.create(ts_json, ".sv", "timestamp");
  String timestampPath = "/sensorData/timestamp";

  Serial.println("Current Values: ");
  Serial.printf("Temperature: %.2f, Humidity: %.2f, Carbon Dioxide Level: %.2f, Light Level: %.2f, Water Level: %s, Water Temp: %.2f, pH: %.2f, tds: %.2f\n",
                temperature, humidity, carbonDioxide, lightLevel, waterLevel ? "OK" : "LOW", waterTemp, pH, tds);

  Serial.println("Set sensor values...");

  bool success = true;
  success &= setFirebaseNumber("/sensorData/temperature", temperature);
  success &= setFirebaseNumber("/sensorData/humidity", humidity);
  success &= setFirebaseNumber("/sensorData/carbonDioxide", carbonDioxide);
  success &= setFirebaseNumber("/sensorData/lightLevel", lightLevel);
  success &= setFirebaseBool("/sensorData/waterLevel", waterLevel);
  success &= setFirebaseNumber("/sensorData/waterTemp", waterTemp);
  success &= setFirebaseNumber("/sensorData/pH", pH);
  success &= setFirebaseNumber("/sensorData/tds", tds);
  success &= setFirebaseObject(timestampPath, ts_json);

  // Check success before attempting to read back timestamp
  if (success) {
      Serial.println("Sensor data set successfully. Attempting to read back timestamp for sync...");
      readFirebaseTimestamp(timestampPath, nullptr);
      return true; // Overall operation was successful in setting data
  } else {
      Serial.println("Failed to set sensor data (one or more fields failed).");
      return false; // Indicate failure
  }
}

// Uploads historical data when ONLINE. Uses server timestamp.
bool uploadSensorDataToRealtimeDatabase(const JsonObject& sensorData) {
  if (isOffline || !app.ready()) {
      Serial.println("Upload skipped: Offline or Firebase not ready.");
      return false; // Indicate failure/skip
  }

  // Validate sensor data using the helper function
  if (!validateSensorData(temperature, humidity, waterTemp)) {
      Serial.println("UPLOAD SUSPENDED: Invalid sensor data detected by validation function.");
      return false; // Indicate failure/skip due to bad data
  }

  // Generate a unique ID using current millis + random characters
  String uniqueID = "h" + String(millis()) + "-" + generateRandomID(8); // "h" for online history upload
  String basePath = "/sensorHistory/" + uniqueID;
  String timestampPath = basePath + "/timestamp"; // Path for the timestamp

  // Create server timestamp object for online history uploads
  object_t ts_json;
  JsonWriter writer;
  writer.create(ts_json, ".sv", "timestamp");

  Serial.printf("Attempting to upload online history data entry with unique ID: %s\n", uniqueID.c_str());

  bool success = true;
  success &= setFirebaseNumber(basePath + "/temperature", temperature);
  success &= setFirebaseNumber(basePath + "/humidity", humidity);
  success &= setFirebaseNumber(basePath + "/carbonDioxide", carbonDioxide);
  success &= setFirebaseNumber(basePath + "/lightLevel", lightLevel);
  success &= setFirebaseBool(basePath + "/waterLevel", waterLevel);
  success &= setFirebaseNumber(basePath + "/waterTemp", waterTemp);
  success &= setFirebaseNumber(basePath + "/pH", pH);
  success &= setFirebaseNumber(basePath + "/tds", tds);
  success &= setFirebaseObject(timestampPath, ts_json); // Set timestamp last

  // Check the final success status
  if (success) {
    Serial.println("Online historical sensor data uploaded to: " + basePath);
    Serial.println("Attempting to read back timestamp for sync...");
    // Read back timestamp and update sync time using the helper
    readFirebaseTimestamp(timestampPath, nullptr);
    return true; // Overall upload operation was successful
  } else {
    Serial.println("Failed to upload online historical data to: " + basePath + " (one or more fields failed).");
    return false; // Indicate failure
  }
}

void tempRangesUpdate() {
  // Skip if offline, Firebase not ready, or internet suspected unstable
  if (isOffline || !app.ready()) {
    Serial.println("Skipping range update: Offline or Firebase not ready.");
    return;
  }
  if (firstInternetFailMillis != 0) {
    Serial.println("Skipping range update: Internet connection suspected unstable.");
    return;
  }

  Serial.println("Fetching sensor ranges from Firebase...");
  float fetchedTempHigh, fetchedTempLow, fetchedHumHigh, fetchedHumLow;

  // Use helper functions. Store to lastKnown only on success.
  if (getFirebaseFloat("/sensorRanges/tempRangeHigh", fetchedTempHigh, tempRangeHigh)) {
      tempRangeHigh = fetchedTempHigh;
      lastKnownTempRangeHigh = tempRangeHigh; // Store successfully fetched value
      Serial.printf("Fetched tempRangeHigh: %.2f\n", tempRangeHigh);
  } else {
      Serial.println("Failed to fetch tempRangeHigh. Keeping previous value.");
      // Error already printed by helper
  }

  if (getFirebaseFloat("/sensorRanges/tempRangeLow", fetchedTempLow, tempRangeLow)) {
      tempRangeLow = fetchedTempLow;
      lastKnownTempRangeLow = tempRangeLow; // Store successfully fetched value
      Serial.printf("Fetched tempRangeLow: %.2f\n", tempRangeLow);
  } else {
      Serial.println("Failed to fetch tempRangeLow. Keeping previous value.");
  }

  if (getFirebaseFloat("/sensorRanges/humRangeHigh", fetchedHumHigh, humRangeHigh)) {
      humRangeHigh = fetchedHumHigh;
      lastKnownHumRangeHigh = humRangeHigh; // Store successfully fetched value
      Serial.printf("Fetched humRangeHigh: %.2f\n", humRangeHigh);
  } else {
      Serial.println("Failed to fetch humRangeHigh. Keeping previous value.");
  }

  if (getFirebaseFloat("/sensorRanges/humRangeLow", fetchedHumLow, humRangeLow)) {
      humRangeLow = fetchedHumLow;
      lastKnownHumRangeLow = humRangeLow; // Store successfully fetched value
      Serial.printf("Fetched humRangeLow: %.2f\n", humRangeLow);
  } else {
      Serial.println("Failed to fetch humRangeLow. Keeping previous value.");
  }
}

void controlActuatorAuto() {
  // Use last known ranges if offline, otherwise use current (potentially just updated) ranges
  float currentTempLow = isOffline ? lastKnownTempRangeLow : tempRangeLow;
  float currentTempHigh = isOffline ? lastKnownTempRangeHigh : tempRangeHigh;
  float currentHumLow = isOffline ? lastKnownHumRangeLow : humRangeLow;
  float currentHumHigh = isOffline ? lastKnownHumRangeHigh : humRangeHigh;

  // Debug prints for sensor values and thresholds being used
  Serial.printf("Auto control -> Temp: %.2f, Humidity: %.2f\n", temperature, humidity);
  Serial.printf("Using Ranges -> TempLow: %.2f, TempHigh: %.2f, HumLow: %.2f, HumHigh: %.2f %s\n",
                currentTempLow, currentTempHigh, currentHumLow, currentHumHigh, isOffline ? "(Offline - Last Known)" : "(Online)");

  // Hysteresis margins, so that actuators would not destroy themselves (rapid switching) when readings are in threshold edge
  const float tempMargin = 1.0;  // Temperature margin in °C
  const float humMargin  = 2.0;  // Humidity margin in %

  // Hysteresis delay (in milliseconds) - Consider if still needed with range usage change
  const unsigned long hysteresisDelay = 2000; // 2 seconds

  // Store current time
  unsigned long now = millis();

  // Compute desired states using the refined logic (without hysteresis initially)
  bool desiredFan = false;
  bool desiredVent = false;
  bool desiredMist = false;

  // Actuator control logic
  if (temperature > currentTempHigh) {
      // High Temperature: Turn on fan and vent.
      desiredFan = true;
      desiredVent = true;
      // If humidity is also low, turn on mist for evaporative cooling.
      if (humidity < currentHumLow) {
          desiredMist = true;
      }
  } else if (temperature < currentTempLow) {
      // Low Temperature: Turn off fan and vent (no cooling needed).
      desiredFan = false;
      desiredVent = false;
      // If humidity is low, turn on mist to increase moisture.
      if (humidity < currentHumLow) {
          desiredMist = true;
      }
      // If humidity is high (unlikely at low temp, but possible), vent to reduce it.
      else if (humidity > currentHumHigh) {
          desiredVent = true; // Prioritize venting over fan if temp is low
      }
  } else {
      // Temperature is Optimal: Control based on humidity.
      desiredFan = false; // Fan primarily for cooling, keep off if temp is ok.
      desiredVent = false; // Vent primarily for cooling/dehumidifying.
      desiredMist = false; // Mist primarily for cooling/humidifying.

      if (humidity < currentHumLow) {
          // Humidity Low: Turn on mist.
          desiredMist = true;
      } else if (humidity > currentHumHigh) {
          // Humidity High: Turn on vent and fan to remove moisture.
          desiredFan = true;
          desiredVent = true;
      }
  }


  // Apply Hysteresis
  static unsigned long lastFanChangeTime = 0;
  static unsigned long lastVentChangeTime = 0;
  static unsigned long lastMistChangeTime = 0;
  static bool lastDesiredFan = fanStatus;
  static bool lastDesiredVent = ventStatus;
  static bool lastDesiredMist = mistStatus;

  // Update status only if the desired state has changed AND enough time has passed
  if (desiredFan != fanStatus && (desiredFan != lastDesiredFan || now - lastFanChangeTime > hysteresisDelay)) {
      fanStatus = desiredFan;
      lastFanChangeTime = now;
  }
  if (desiredVent != ventStatus && (desiredVent != lastDesiredVent || now - lastVentChangeTime > hysteresisDelay)) {
      ventStatus = desiredVent;
      lastVentChangeTime = now;
  }
  if (desiredMist != mistStatus && (desiredMist != lastDesiredMist || now - lastMistChangeTime > hysteresisDelay)) {
      mistStatus = desiredMist;
      lastMistChangeTime = now;
  }
  // Store the current desired state for the next iteration's comparison
  lastDesiredFan = desiredFan;
  lastDesiredVent = desiredVent;
  lastDesiredMist = desiredMist;


  // Update actuator states in Firebase ONLY if online and Firebase is ready
  if (!isOffline && app.ready()) {
      Serial.println("Updating actuator states in Firebase (Auto Mode)...");
      Database.set<bool>(aClient, "/actuatorStates/fan", fanStatus);
      Database.set<bool>(aClient, "/actuatorStates/vent", ventStatus);
      Database.set<bool>(aClient, "/actuatorStates/mist", mistStatus);
  } else {
      Serial.println("Skipping Firebase actuator state update (Offline or Firebase not ready).");
  }

  // Apply the actuator commands locally regardless of network state
  Serial.printf("Applying local actuator states: Fan=%s, Vent=%s, Mist=%s\n",
                fanStatus ? "ON" : "OFF", ventStatus ? "OPEN" : "CLOSED", mistStatus ? "ON" : "OFF");
  digitalWrite(fanPin, fanStatus ? HIGH : LOW);
  mistPumpControl(mistStatus); // Handles its own timing/logic
  ventServoControl(ventStatus, servoAngle); // Pass servoAngle by value if not modified inside
}

// Helper function for vent servo control
void ventServoControl(bool ventStatus, int servoAngle) {
  if (ventStatus) {
    servoAngle = 180;  // Vent open
    ventServo.write(servoAngle);  // Set the servo to open position
    Serial.print("Servo status: ");
    Serial.println("Vent Open (180 degrees)");
  } else {
    servoAngle = 0;  // Vent closed
    ventServo.write(servoAngle);  // Set the servo to closed position
    Serial.print("Servo status: ");
    Serial.println("Vent Closed (0 degrees)");
  }
}
 
// Helper function to handle mist pump control with a cycle to avoid overheating
void mistPumpControl(bool mistStatus) {
  unsigned long currentMillis = millis();
 
  // If mistStatus is false, ensure mist is off and return
  if (!mistStatus) {
    misting = false;
    digitalWrite(mistPin, LOW);
    return;
  }
 
  // Only proceed with misting cycle if mistStatus is true
  if (!misting && currentMillis - mistPumpMillis >= 300000) {  // 5 minutes (300000ms) have passed since last mist
    misting = true;
    lastMistStart = currentMillis;
    digitalWrite(mistPin, HIGH);  // Start misting
  } else if (misting && currentMillis - lastMistStart >= 120000) {  // Has been misting for 2 minutes (120000ms)
    misting = false;
    digitalWrite(mistPin, LOW);  // Stop misting
    mistPumpMillis = currentMillis;  // Reset 5-minute timer
  }
}
 
// Function to read DHT22 temperature sensor using DHTesp
float readTemperature() {
  float temp = dht.readTemperature();
  if (isnan(temp)) {
    temp = 0.0;
  }
  return temp;
}
 
// Function to read DHT22 humidity sensor using DHTesp
float readHumidity() {
  float hum = dht.readHumidity();
  if (isnan(hum)) {
    hum = 0.0;
  }
  return hum;
}
 
float readCO2() {
  mq135Value = analogRead(mqPin);  // Read the analog value
  mq135Voltage = mq135Value * (3.3 / 4096.0);  // Convert ADC value to voltage
  mq135PPM = (mq135Voltage - 0.04) * 400.0;  // Adjust based on calibration
  if (mq135PPM < 0.0) {
    mq135PPM = 0.0;
  }
  return mq135PPM;
}
 
// BH1750 light intensity sensor reading
int readLightLevel() {
  float lux = lightMeter.readLightLevel();
  if (lux < 0.0) {
    lux = 0.0;
  }
  return lux;
}
 
// Function to read DS18B20 water temperature sensor reading
float readWaterTemperature() {
  sensors.requestTemperatures();  // Request temperature from DS18B20
  float waterTemp = sensors.getTempCByIndex(0);  // Get the temperature in Celsius
  // Check for error codes specific to DS18B20
  if (waterTemp == DEVICE_DISCONNECTED_C || waterTemp == 85.0 || waterTemp == -127.0) {
    Serial.printf("Warning: Invalid water temperature reading (%.2f). Returning 0.\n", waterTemp);
    waterTemp = 0.0; // Return 0 or another indicator for invalid reading
  }
  return waterTemp;
}

// Water Level Sensor reading (float switch)
bool readWaterLevel() {
  return digitalRead(wlPin) == LOW; // Assuming LOW means sufficient water in a normally closed setup
}
 
// PH sensor reading (analog)
float readPHLevel() {
  int buffer[10];
  unsigned long avgval = 0;
 
  for (int i = 0; i < 10; i++) {
    buffer[i] = analogRead(phPin);
    delay(30);
  }
 
  // Sort the buffer values
  for (int i = 0; i < 9; i++) {
    for (int j = i + 1; j < 10; j++) {
      if (buffer[i] > buffer[j]) {
        int temp = buffer[i];
        buffer[i] = buffer[j];
        buffer[j] = temp;
      }
    }
  }
 
  // Get the average of the middle 6 values
  for (int i = 2; i < 8; i++) {
    avgval += buffer[i];
  }
 
  float voltage = (float)avgval * 3.3 / 4095 / 6;
  float ph = -5.70 * voltage + 21.34; // Adjust this formula based on your calibration
  if (ph < 0) ph = 0;
  if (ph > 14) ph = 14;
 
  return ph;
}
 
float readTDS() {
  static unsigned long analogSampleTimepoint = millis();
  if (millis() - analogSampleTimepoint > 40U) {  // Every 40 milliseconds, read the analog value from the ADC
    analogSampleTimepoint = millis();
    analogBuffer[analogBufferIndex] = analogRead(tdsPin);  // Read the analog value and store it in the buffer
    analogBufferIndex++;
    if (analogBufferIndex == SCOUNT) {
      analogBufferIndex = 0;
    }
  }
 
  static unsigned long printTimepoint = millis();
  if (millis() - printTimepoint > 800U) {  // Print every 800 milliseconds
    printTimepoint = millis();
    for (copyIndex = 0; copyIndex < SCOUNT; copyIndex++) {
      analogBufferTemp[copyIndex] = analogBuffer[copyIndex];
    }
 
    averageVoltage = getMedianNum(analogBufferTemp, SCOUNT) * (float)VREF / 4096.0;
 
    float compensationCoefficient = 1.0 + 0.02 * (temperature - 25.0);
    float compensationVoltage = averageVoltage / compensationCoefficient;
 
    tdsValue = (133.42 * compensationVoltage * compensationVoltage * compensationVoltage
                - 255.86 * compensationVoltage * compensationVoltage
                + 857.39 * compensationVoltage) * 0.5;
    Serial.print("TDS Value: ");
    Serial.println(tdsValue);  // Debugging: Print TDS value
  }
 
  return tdsValue;
}

// For the sensor history directory
String generateRandomID(int length) {
  const char charset[] = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  String result = "";
  
  // Use millis() to seed the first character - adds some time-based variation
  unsigned long m = millis();
  result += charset[m % 62];
  
  // Add remaining random characters
  for (int i = 1; i < length; i++) {
    int randomIndex = random(0, 62); // 62 is the length of charset
    result += charset[randomIndex];
  }
  
  return result;
}

// Function to check actual internet/Firebase connectivity
void checkInternetConnection() {
    Serial.println("Checking internet connectivity via Firebase read...");
    Database.get<String>(aClient, "/sensorData/timestamp");
    unsigned long currentMillis = millis();

    if (checkFirebaseError("internet connectivity check")) {
        // Success! Internet is available.
        if (!internetAvailable) {
            Serial.println("Internet connection confirmed.");
            internetAvailable = true; // Mark as available
            // State transition (Offline -> Online) will be handled in the main loop
        } else {
            // Internet was already available, check successful.
             Serial.println("Internet connection check successful.");
        }
        firstInternetFailMillis = 0; // Reset failure timer
    } else {
        if (firstInternetFailMillis == 0) {
            // First failure detected
            Serial.println("First internet check failure, starting confirmation timer.");
            firstInternetFailMillis = currentMillis;
        } else if (currentMillis - firstInternetFailMillis >= internetFailConfirmDuration) {
            if (internetAvailable) {
                Serial.printf("Internet connection unavailable for %lu ms. Switching to offline mode.\n", internetFailConfirmDuration);
                internetAvailable = false; // CONFIRM internet outage
                // State transition (Online -> Offline) will be handled in the main loop
            }
        } else {
             // Failure continues, but confirmation duration not yet met
             Serial.println("Internet check failed again, confirmation pending...");
        }
    }
}

// Helper function to get the best estimate of the current Epoch time in milliseconds
unsigned long long getCurrentTimestampEstimate() {
    // Use lastSuccessfulTimestamp if available, otherwise fallback to uptime approx.
    if (lastSuccessfulTimestamp != 0) {
        // Estimate based on last sync + millis() delta, works even if currently offline
        unsigned long long estimatedTime = lastSuccessfulTimestamp + (millis() - millisAtLastSuccessfulTimestamp);
        if (isOffline) {
             Serial.println("Estimating time based on last sync point (offline).");
        }
        return estimatedTime;
    } else {
        // No sync point available (offline from boot or sync failed): use uptime approximation
        unsigned long long approxTime = 1744608526000ULL + millis(); // Base time + uptime in ms
        Serial.println("Estimating time based on uptime approximation (no sync point).");
        return approxTime;
    }
}

// Helper function to update the time sync point after a successful Firebase interaction
void updateLastSuccessfulTime(unsigned long long firebaseTimestamp) {
    if (firebaseTimestamp != 0) {
        // Use the accurate timestamp provided by Firebase
        lastSuccessfulTimestamp = firebaseTimestamp;
        millisAtLastSuccessfulTimestamp = millis(); // Record current millis() at the time of sync
        Serial.printf("Updated time sync point using Firebase timestamp: %llu ms epoch\n", lastSuccessfulTimestamp);
    } else {
        // Fallback to estimation if no Firebase timestamp was provided or read failed
        Serial.println("Updating time sync point using estimation (Firebase timestamp not available).");
        lastSuccessfulTimestamp = getCurrentTimestampEstimate(); // Use the existing estimation logic
        millisAtLastSuccessfulTimestamp = millis();
        Serial.printf("Estimated time sync point: %llu ms epoch (approx)\n", lastSuccessfulTimestamp);
    }
}

// Initialize SPIFFS
void initializeSPIFFS() {
  if (!SPIFFS.begin(true)) { // Format SPIFFS if mount fails
    Serial.println("An Error has occurred while mounting SPIFFS");
    // Handle error: maybe restart or disable local saving?
    return;
  }
  Serial.println("SPIFFS mounted successfully.");

  // Optional: List files for debugging
  fs::File root = SPIFFS.open("/"); // Use fs::File
  fs::File file = root.openNextFile(); // Use fs::File
  Serial.println("Files on SPIFFS:");
  while(file){
      Serial.print("  FILE: ");
      Serial.println(file.name());
      file = root.openNextFile();
  }
  root.close();
}

// Helper to create the JSON object for sensor data
bool createSensorJson(JsonObject& doc, float temperature, float humidity, float carbonDioxide, float lightLevel, bool waterLevel, float waterTemp, float pH, float tds) {
    if (!validateSensorData(temperature, humidity, waterTemp)) {
        Serial.println("createSensorJson: Invalid sensor data detected by validation function.");
        return false; // Indicate invalid data
    }

    doc["temperature"] = round(temperature * 100.0) / 100.0; // 2 decimal places
    doc["humidity"] = round(humidity * 100.0) / 100.0;
    doc["carbonDioxide"] = round(carbonDioxide * 100.0) / 100.0;
    doc["lightLevel"] = round(lightLevel * 100.0) / 100.0;
    doc["waterLevel"] = waterLevel;
    doc["waterTemp"] = round(waterTemp * 100.0) / 100.0;
    doc["pH"] = round(pH * 100.0) / 100.0;
    doc["tds"] = round(tds * 100.0) / 100.0;

    // Add the estimated timestamp
    unsigned long long currentTs = getCurrentTimestampEstimate();
    doc["timestamp"] = currentTs; // Add as epoch milliseconds
    Serial.printf("Timestamp added to JSON: %llu\n", currentTs);

    return true; // Indicate success
}

// Save sensor data JSON object to SPIFFS file (append)
bool saveDataLocally(const JsonObject& sensorData) {
  fs::File file = SPIFFS.open(OFFLINE_HISTORY_FILE, FILE_APPEND);
  if (!file) {
    Serial.println("Failed to open offline history file for appending.");
    return false;
  }

  // Serialize JSON to file
  if (serializeJson(sensorData, file) == 0) {
    Serial.println("Failed to write sensor data to offline file.");
    file.close();
    return false;
  }

  // Add a newline character to separate JSON objects (JSON Lines format)
  file.println();
  file.close();
  Serial.println("Sensor data saved locally.");

  // Optional: Check file size and implement rotation/deletion if it gets too large
  file = SPIFFS.open(OFFLINE_HISTORY_FILE, FILE_READ);
    if (file) { // Check if file opened successfully
        size_t fileSize = file.size();
        file.close();
        Serial.printf("Offline history file size: %u bytes\n", fileSize);
        // Example: Limit file size to ~1MB
        if (fileSize > 1024 * 1024) {
            Serial.println("Warning: Offline history file is large. Consider implementing rotation.");
        }
    } else {
        Serial.println("Could not open offline history file to check size.");
    }
  return true;
}

// Upload stored offline history data
void uploadOfflineHistory() {
  // Check combined offline state
  if (isOffline || !app.ready()) {
    Serial.println("Cannot upload offline history: Network offline/unavailable or Firebase not ready.");
    return;
  }

  fs::File file = SPIFFS.open(OFFLINE_HISTORY_FILE, FILE_READ);
  if (!file || file.size() == 0) {
    if (file) file.close();
    Serial.println("No offline history data to upload.");
    return;
  }

  Serial.println("Starting upload of offline sensor history...");

  // Create a temporary file to write data that *fails* to upload
  String tempFileName = String(OFFLINE_HISTORY_FILE) + ".tmp";
  fs::File tempFile = SPIFFS.open(tempFileName, FILE_WRITE);
  if (!tempFile) {
      Serial.println("Failed to open temporary file for offline sync. Aborting.");
      file.close();
      return;
  }

  bool allUploadedSuccessfully = true;
  int recordsUploaded = 0;
  int recordsFailed = 0;

  // Read file line by line
  while (file.available()) {
    String line = file.readStringUntil('\n');
    line.trim(); // Remove potential whitespace/newlines

    if (line.length() == 0) continue; // Skip empty lines

    // Check network connection before each upload attempt
    if (WiFi.status() != WL_CONNECTED || !app.ready()) { // Also check Firebase readiness
        Serial.println("Network connection lost or Firebase not ready during offline sync. Aborting.");
        allUploadedSuccessfully = false;
        // Write the current line and remaining lines to the temp file
        tempFile.println(line); // Write the line we were processing
        while (file.available()) { // Write the rest of the original file
            tempFile.write(file.read());
        }
        break; // Exit the loop
    }

    DynamicJsonDocument doc(1024); // Document size
    DeserializationError error = deserializeJson(doc, line);

    if (error) {
      Serial.print("Failed to parse line from offline file: ");
      Serial.println(line);
      Serial.print("Error: ");
      Serial.println(error.c_str());
      // Write the bad line to the temp file to keep it
      tempFile.println(line);
      recordsFailed++;
      allUploadedSuccessfully = false;
      continue;
    }

    JsonObject sensorData = doc.as<JsonObject>();

    // Attempt to upload this record using the dedicated function
    if (uploadOfflineRecord(sensorData)) { // Pass the parsed JSON object
      recordsUploaded++;
      // Don't write successfully uploaded records to the temp file
      Serial.printf("Successfully uploaded offline record %d.\n", recordsUploaded);
    } else {
      Serial.println("Failed to upload offline record. Keeping it for later.");
      // Write the failed record back to the temp file
      serializeJson(sensorData, tempFile); // Use the parsed object
      tempFile.println(); // Add newline
      recordsFailed++;
      allUploadedSuccessfully = false;
      delay(500);
    }
     yield(); // Allow background tasks (like WiFi) to run
  }

  // Close both files
  file.close();
  tempFile.close();

  // Replace the original file with the temporary file (which contains only failed/remaining records)
  SPIFFS.remove(OFFLINE_HISTORY_FILE);
  SPIFFS.rename(tempFileName, OFFLINE_HISTORY_FILE);

  Serial.printf("Offline history sync finished. Uploaded: %d, Failed/Kept: %d\n", recordsUploaded, recordsFailed);

  // Update time sync point ONCE after the batch upload if any records succeeded
  if (recordsUploaded > 0) {
      updateLastSuccessfulTime(); // Use estimation after offline batch upload
      Serial.println("Updated time sync point after offline batch upload (using estimation).");
  }

  if (recordsFailed == 0) {
      Serial.println("All offline data uploaded successfully.");
      file = SPIFFS.open(OFFLINE_HISTORY_FILE, FILE_READ); // Use FILE_READ
      if (file && file.size() == 0) {
          Serial.println("Offline history file is now empty.");
      } else if (file) {
          Serial.printf("Offline history file size after sync: %u bytes (should be 0 if all succeeded).\n", file.size());
      } else {
          Serial.println("Could not open offline history file to check size after sync.");
      }
      if (file) file.close();
  } else {
      Serial.println("Some records remain in the offline history file.");
  }
}

// Uploads a single record parsed from the offline file, using the stored timestamp
bool uploadOfflineRecord(const JsonObject& sensorData) {
    // Check combined offline state
    if (isOffline || !app.ready()) {
        Serial.println("Upload offline record skipped: Network offline/unavailable or Firebase not ready.");
        return false;
    }

    // Extract values from JsonObject
    float temp = sensorData["temperature"].as<float>();
    float hum = sensorData["humidity"].as<float>();
    float co2 = sensorData["carbonDioxide"].as<float>();
    float light = sensorData["lightLevel"].as<float>();
    bool wl = sensorData["waterLevel"].as<bool>();
    float wt = sensorData["waterTemp"].as<float>();
    float ph_val = sensorData["pH"].as<float>();
    float tds_val = sensorData["tds"].as<float>();
    unsigned long long storedTimestamp = sensorData["timestamp"].as<unsigned long long>();

    // Validate core sensor data using the helper function
    if (!validateSensorData(temp, hum, wt)) {
        Serial.println("UPLOAD OFFLINE RECORD SUSPENDED: Invalid sensor data detected by validation function.");
        return false; // Indicate failure/skip due to bad data
    }
    // Check if timestamp was parsed correctly
    if (storedTimestamp == 0) {
        Serial.println("UPLOAD OFFLINE RECORD SUSPENDED: Invalid or missing timestamp in stored data.");
        return false; // Indicate failure/skip due to bad timestamp
    }

    // Generate a unique ID
    String uniqueID = "o" + String(millis()) + "-" + generateRandomID(8); // Prefix 'o' for offline upload
    String basePath = "/sensorHistory/" + uniqueID;
    Serial.printf("Attempting to upload offline record data entry with unique ID: %s (Timestamp: %llu)\n", uniqueID.c_str(), storedTimestamp);

    // Set each field individually using helpers
    bool success = true;
    success &= setFirebaseNumber(basePath + "/temperature", temp);
    success &= setFirebaseNumber(basePath + "/humidity", hum);
    success &= setFirebaseNumber(basePath + "/carbonDioxide", co2);
    success &= setFirebaseNumber(basePath + "/lightLevel", light);
    success &= setFirebaseBool(basePath + "/waterLevel", wl);
    success &= setFirebaseNumber(basePath + "/waterTemp", wt);
    success &= setFirebaseNumber(basePath + "/pH", ph_val);
    success &= setFirebaseNumber(basePath + "/tds", tds_val);

    // Use the stored timestamp - use setFirebaseNumber for unsigned long long
    success &= Database.set<number_t>(aClient, basePath + "/timestamp", number_t((double)storedTimestamp)); // Cast to double for number_t
    success &= checkFirebaseError("set offline timestamp at " + basePath + "/timestamp"); // Check error specifically for timestamp
    // Check final status
    if (success) {
        return true;
    } else {
        Serial.println("Failed to upload offline record to: " + basePath + " (one or more fields failed).");
        return false;
    }
}