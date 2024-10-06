package com.fsvdevs.agrosphere

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.repository.ActuatorDataRepository
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import com.fsvdevs.agrosphere.repository.SensorRangeDataRepository
import com.fsvdevs.agrosphere.routes.Routes
import com.fsvdevs.agrosphere.ui.DashboardScreen
import com.fsvdevs.agrosphere.ui.LoginScreen
import com.fsvdevs.agrosphere.ui.MonitorScreen
import com.fsvdevs.agrosphere.ui.NotificationsScreen
import com.fsvdevs.agrosphere.ui.PreferencesScreen
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme
import com.fsvdevs.agrosphere.viewmodel.ActuatorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorRangeDataViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient
    private var isLoggedIn by mutableStateOf(false)
    private val tag = "MainActivity"
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var database: DatabaseReference
    private lateinit var firestore: FirebaseFirestore

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val credential = signInClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential?.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(tag, "Google Sign-in successful")
                            Toast.makeText(baseContext, "Google Sign-in successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(tag, "Google Sign-in failed", task.exception)
                            Toast.makeText(baseContext, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Log.e(tag, "Google Sign-in canceled")
            Toast.makeText(baseContext, "Google Sign-in canceled", Toast.LENGTH_SHORT).show()
        }
    }

    // Instantiate ViewModels using a factory or dependency injection
    private val sensorDataViewModel: SensorDataViewModel by viewModels {
        SensorDataViewModel.Factory(SensorDataRepository(database))
    }
    private val actuatorDataViewModel: ActuatorDataViewModel by viewModels {
        ActuatorDataViewModel.Factory(ActuatorDataRepository(database))
    }
    private val sensorRangeDataViewModel: SensorRangeDataViewModel by viewModels {
        SensorRangeDataViewModel.Factory(SensorRangeDataRepository(database))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        signInClient = Identity.getSignInClient(this)
        database = FirebaseDatabase.getInstance("https://agrosphere-fsvdev-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        firestore = FirebaseFirestore.getInstance()

        // Listen for changes in authentication state
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            isLoggedIn = user != null && user.isEmailVerified
            Log.d(tag, "Auth state changed: isLoggedIn = $isLoggedIn")
        }

        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            AgroSphereTheme(isDarkTheme) {
                if (!isLoggedIn) {
                    LoginScreen(
                        onGoogleSignIn = { signInWithGoogle() },
                        onEmailSignIn = { email, password -> signInWithEmail(email, password) },
                        onSignUp = { email, password -> createAccount(email, password) },
                        onForgotPassword = { email -> resetPassword(email) }
                    )
                } else {
                    val sensorData by sensorDataViewModel.sensorData.collectAsState()
                    val actuatorData by actuatorDataViewModel.actuatorData.collectAsState()
                    val sensorRangeData by sensorRangeDataViewModel.sensorRangeData.collectAsState()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                            sensorRangeDataViewModel = sensorRangeDataViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                    Log.d(tag, "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Account created! Please verify your email.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(tag, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Account creation failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                Log.d(tag, "Google One Tap Sign-in successful")
                Toast.makeText(baseContext, "Google One Tap Sign-in successful", Toast.LENGTH_SHORT).show()
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                googleSignInLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Google One Tap Sign-in failed: ${e.message}", e)
                Toast.makeText(baseContext, "Google One Tap Sign-in failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        Log.d(tag, "signInWithEmail:success")
                        Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(tag, "signInWithEmail:email not verified")
                        Toast.makeText(baseContext, "Please verify your email.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Password reset email sent")
                    Toast.makeText(baseContext, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(tag, "Failed to send password reset email", task.exception)
                    Toast.makeText(baseContext, "Failed to send password reset email.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun NavRoutes(
    sensorData: SensorData?,
    actuatorData: ActuatorData?,
    sensorRangeData: SensorRangeData?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sensorDataViewModel: SensorDataViewModel,
    actuatorDataViewModel: ActuatorDataViewModel,
    sensorRangeDataViewModel: SensorRangeDataViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD_SCREEN,
        modifier = modifier
    ) {
        composable(Routes.DASHBOARD_SCREEN) { DashboardScreen(
            sensorData = sensorData,
            actuatorData = actuatorData,
            sensorRangeData = sensorRangeData,
            sensorDataViewModel = sensorDataViewModel,
            actuatorDataViewModel = actuatorDataViewModel,
            sensorRangeDataViewModel = sensorRangeDataViewModel,
            navController = navController
        ) }
        composable(Routes.MONITOR_SCREEN) { MonitorScreen(
            sensorDataViewModel
        ) }
        composable(Routes.NOTIFICATIONS_SCREEN) { NotificationsScreen(navController) }
        composable(Routes.PREFERENCES_SCREEN) { PreferencesScreen(navController) }
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                onGoogleSignIn = {},
                onEmailSignIn = { _, _ -> },
                onSignUp = { _, _ -> },
                onForgotPassword = {}
            )
        }
    }
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