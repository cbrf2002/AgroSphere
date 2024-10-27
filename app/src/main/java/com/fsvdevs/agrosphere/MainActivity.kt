@file:Suppress("DEPRECATION")
package com.fsvdevs.agrosphere

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.routes.Routes
import com.fsvdevs.agrosphere.ui.DashboardScreen
import com.fsvdevs.agrosphere.ui.LoginScreen
import com.fsvdevs.agrosphere.ui.MonitorScreen
import com.fsvdevs.agrosphere.ui.NotificationsScreen
import com.fsvdevs.agrosphere.ui.PreferenceScreen
import com.fsvdevs.agrosphere.ui.SplashScreen
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme
import com.fsvdevs.agrosphere.ui.theme.AppTheme
import com.fsvdevs.agrosphere.util.AuthHelper
import com.fsvdevs.agrosphere.util.AuthHelper.handleGoogleSignIn
import com.fsvdevs.agrosphere.util.FirebaseHelper
import com.fsvdevs.agrosphere.util.FirebaseHelper.auth
import com.fsvdevs.agrosphere.util.FirebaseHelper.signInClient
import com.fsvdevs.agrosphere.util.NotificationHelper
import com.fsvdevs.agrosphere.util.NotificationHelper.checkAndRequestNotificationPermission
import com.fsvdevs.agrosphere.util.NotificationHelper.requestNotificationPermission
import com.fsvdevs.agrosphere.util.PreferencesManager
import com.fsvdevs.agrosphere.util.ViewModelFactory
import com.fsvdevs.agrosphere.util.checkSensorValuesAndNotify
import com.fsvdevs.agrosphere.util.getTitleForRoute
import com.fsvdevs.agrosphere.viewmodel.ActuatorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorRangeDataViewModel
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var preferencesManager: PreferencesManager

    private var isLoggedIn by mutableStateOf(false)
    private var lastCheckJob: Job? = null
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        handleGoogleSignIn(result, signInClient, auth, this)
    }
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.e("MainActivity", "Notification permission denied")
        }
    }

    private val sensorDataViewModel: SensorDataViewModel by lazy { ViewModelFactory.provideSensorDataViewModel(this) }
    private val actuatorDataViewModel: ActuatorDataViewModel by lazy { ViewModelFactory.provideActuatorDataViewModel(this) }
    private val sensorRangeDataViewModel: SensorRangeDataViewModel by lazy { ViewModelFactory.provideSensorRangeDataViewModel(this) }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Initialize Firebase and other dependencies
        FirebaseApp.initializeApp(this)
        FirebaseHelper.initialize(this)
        preferencesManager = PreferencesManager(this)
        requestNotificationPermission(requestNotificationPermissionLauncher)
        checkAndRequestNotificationPermission(this, requestNotificationPermissionLauncher)
        NotificationHelper.createNotificationChannel(this)
        Log.d("MainActivity", "Notification channel created")
        Utils.init(this)

        // Listen for changes in authentication state
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            isLoggedIn = firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified == true
        }

        setContent {
            var selectedTheme by remember { mutableStateOf(AppTheme.AUTO) }
            val dynamicColorEnabled by preferencesManager.dynamicColorFlow.collectAsState(initial = false)

            val navController = rememberNavController()
            val navigateTo = intent.getStringExtra("navigate_to")

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val sensorData by sensorDataViewModel.sensorData.collectAsState()
            val actuatorData by actuatorDataViewModel.actuatorData.collectAsState()
            val sensorRangeData by sensorRangeDataViewModel.sensorRangeData.collectAsState()

            var showSplash by remember { mutableStateOf(true) }

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                preferencesManager.themeFlow.collect { theme ->
                    selectedTheme = theme
                }
            }

            LaunchedEffect(sensorData, sensorRangeData) {

                // Ensure non-null sensor data for the notification trigger
                if (sensorData != null && sensorRangeData != null) {
                    lastCheckJob?.cancel() // Cancel the previous job if it exists
                    lastCheckJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(5000) // 2 seconds delay
                        checkSensorValuesAndNotify(context, sensorData!!, sensorRangeData!!)
                    }
                }
            }

            LaunchedEffect(Unit) {
                delay(1500)
                showSplash = false
            }

            AgroSphereTheme(
                appTheme = selectedTheme,
                dynamicColorEnabled = dynamicColorEnabled
            ) {
                AnimatedContent(targetState = showSplash, transitionSpec = {
                    fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(1000))
                }, label = "SplashScreen Animation") { targetState ->
                    when (targetState) {
                        true -> SplashScreen()
                        false -> {
                            if (isLoggedIn) {
                                MainContent(
                                    navController = navController,
                                    currentRoute = currentRoute,
                                    sensorData = sensorData,
                                    actuatorData = actuatorData,
                                    sensorRangeData = sensorRangeData,
                                    sensorDataViewModel = sensorDataViewModel,
                                    actuatorDataViewModel = actuatorDataViewModel,
                                    sensorRangeDataViewModel = sensorRangeDataViewModel,
                                    selectedTheme = selectedTheme,
                                    dynamicColorEnabled = dynamicColorEnabled,
                                    navigateTo = navigateTo,
                                    saveThemePreference = { theme -> saveThemePreference(theme) },
                                    saveDynamicColorPreference = { isEnabled -> saveDynamicColorPreference(isEnabled) }
                                )
                            } else {
                                LoginScreen(
                                    onGoogleSignIn = { AuthHelper.signInWithGoogle(context as Activity, signInClient, googleSignInLauncher) },
                                    onEmailSignIn = { email, password -> AuthHelper.signInWithEmail(context, auth, email, password) },
                                    onSignUp = { email, password -> AuthHelper.createAccount(context, auth, email, password) },
                                    onForgotPassword = { email -> AuthHelper.resetPassword(context, auth, email) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveThemePreference(theme: AppTheme) {
        lifecycleScope.launch {
            preferencesManager.saveThemePreference(theme)
        }
    }

    private fun saveDynamicColorPreference(isEnabled: Boolean) {
        lifecycleScope.launch {
            preferencesManager.saveDynamicColorPreference(isEnabled)
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(authStateListener)
        lastCheckJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
        checkAndRequestNotificationPermission(this, requestNotificationPermissionLauncher)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
        lastCheckJob?.cancel()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    navController: NavHostController,
    currentRoute: String?,
    sensorData: SensorData? = null,
    actuatorData: ActuatorData? = null,
    sensorRangeData: SensorRangeData? = null,
    sensorDataViewModel: SensorDataViewModel,
    actuatorDataViewModel: ActuatorDataViewModel,
    sensorRangeDataViewModel: SensorRangeDataViewModel,
    selectedTheme: AppTheme = AppTheme.AUTO,
    dynamicColorEnabled: Boolean = false,
    navigateTo: String? = null,
    saveThemePreference: (AppTheme) -> Unit = {},
    saveDynamicColorPreference: (Boolean) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            AnimatedVisibility(
                visible = currentRoute != Routes.DASHBOARD_SCREEN,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInVertically(
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutVertically(
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = getTitleForRoute(currentRoute),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = currentRoute ?: Routes.DASHBOARD_SCREEN,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavRoutes(
            sensorData = sensorData,
            actuatorData = actuatorData,
            sensorRangeData = sensorRangeData,
            navController = navController,
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            sensorDataViewModel = sensorDataViewModel,
            actuatorDataViewModel = actuatorDataViewModel,
            sensorRangeDataViewModel = sensorRangeDataViewModel,
            navigateTo = navigateTo,
            selectedTheme = selectedTheme,
            dynamicColorEnabled = dynamicColorEnabled,
            onThemeChange = { newTheme ->
                saveThemePreference(newTheme)
            },
            onDynamicColorChange = { isEnabled ->
                saveDynamicColorPreference(isEnabled)
            },
            saveThemePreference = { theme ->
                saveThemePreference(theme)
            },
            saveDynamicColorPreference = { isEnabled ->
                saveDynamicColorPreference(isEnabled)
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavRoutes(
    sensorData: SensorData?,
    actuatorData: ActuatorData?,
    sensorRangeData: SensorRangeData?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sensorDataViewModel: SensorDataViewModel,
    actuatorDataViewModel: ActuatorDataViewModel,
    sensorRangeDataViewModel: SensorRangeDataViewModel,
    navigateTo: String?,
    selectedTheme: AppTheme,
    dynamicColorEnabled: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    saveThemePreference: (AppTheme) -> Unit,
    saveDynamicColorPreference: (Boolean) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    var previousRoute by remember { mutableStateOf(Routes.DASHBOARD_SCREEN) }
    var currentRoute by remember { mutableStateOf(Routes.DASHBOARD_SCREEN) }

    LaunchedEffect(navBackStackEntry) {
        previousRoute = currentRoute
        currentRoute = navBackStackEntry?.destination?.route ?: Routes.DASHBOARD_SCREEN
    }

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            val currentPosition = getPosition(previousRoute)
            val targetPosition = getPosition(currentRoute)
            val tweenDuration = 300
            Log.d("MainActivityAnimatedContent", "Previous Position: $currentPosition, Target Position: $targetPosition")

            if (targetPosition > currentPosition) {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(tweenDuration)) with
                        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(tweenDuration))
            } else {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(tweenDuration)) with
                        slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(tweenDuration))
            }
        },
        modifier = modifier, label = ""
    ) { targetRoute ->
        previousRoute = currentRoute
        currentRoute = targetRoute
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD_SCREEN
        ) {
            composable(Routes.DASHBOARD_SCREEN) {
                if (targetRoute == Routes.DASHBOARD_SCREEN) {
                    DashboardScreen(
                        sensorData = sensorData,
                        actuatorData = actuatorData,
                        sensorRangeData = sensorRangeData,
                        actuatorDataViewModel = actuatorDataViewModel,
                        sensorRangeDataViewModel = sensorRangeDataViewModel
                    )
                }
            }
            composable(Routes.MONITOR_SCREEN) {
                if (targetRoute == Routes.MONITOR_SCREEN) {
                    MonitorScreen(sensorDataViewModel)
                }
            }
            composable(Routes.NOTIFICATIONS_SCREEN) {
                if (targetRoute == Routes.NOTIFICATIONS_SCREEN) {
                    NotificationsScreen()
                }
            }
            composable(Routes.PREFERENCES_SCREEN) {
                if (targetRoute == Routes.PREFERENCES_SCREEN) {
                    PreferenceScreen(
                        navController = navController,
                        currentTheme = selectedTheme,
                        dynamicColorEnabled = dynamicColorEnabled,
                        onThemeChange = onThemeChange,
                        onDynamicColorChange = onDynamicColorChange,
                        saveThemePreference = saveThemePreference,
                        saveDynamicColorPreference = saveDynamicColorPreference
                    )
                }
            }
        }
    }

    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            navController.navigate(it) {
                popUpTo(Routes.DASHBOARD_SCREEN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

val screenOrder = listOf(
    Routes.DASHBOARD_SCREEN,
    Routes.MONITOR_SCREEN,
    Routes.NOTIFICATIONS_SCREEN,
    Routes.PREFERENCES_SCREEN
)

fun getPosition(route: String): Int {
    return screenOrder.indexOf(route)
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == Routes.DASHBOARD_SCREEN,
            onClick = { onItemSelected(Routes.DASHBOARD_SCREEN) },
            label = { Text("Dashboard") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.rounded_team_dashboard_24),
                    contentDescription = "Dashboard"
                )
            }
        )
        NavigationBarItem(
            selected = selectedItem == Routes.MONITOR_SCREEN,
            onClick = { onItemSelected(Routes.MONITOR_SCREEN) },
            label = { Text("Monitor") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.rounded_insert_chart_24),
                    contentDescription = "Monitor"
                )
            }
        )
        NavigationBarItem(
            selected = selectedItem == Routes.NOTIFICATIONS_SCREEN,
            onClick = { onItemSelected(Routes.NOTIFICATIONS_SCREEN) },
            label = { Text("Notifications") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.rounded_notifications_24),
                    contentDescription = "Notifications"
                )
            }
        )
        NavigationBarItem(
            selected = selectedItem == Routes.PREFERENCES_SCREEN,
            onClick = { onItemSelected(Routes.PREFERENCES_SCREEN) },
            label = { Text("Preferences") },
            icon = {
                Icon(
                    painterResource(id = R.drawable.rounded_settings_24),
                    contentDescription = "Preferences"
                )
            }
        )
    }
}