@file:Suppress("DEPRECATION")
package com.fsvdevs.agrosphere.util

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.fsvdevs.agrosphere.MainActivity // Import MainActivity
import com.fsvdevs.agrosphere.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess // Import for exitProcess

object AuthHelper {
    private const val TAG = "AuthHelper"

    // Function to restart the application
    private fun restartApp(context: Context) {
        Log.d(TAG, "Restarting application...")
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        exitProcess(0) // Terminate the current process
    }

    fun handleGoogleSignIn(result: ActivityResult, signInClient: SignInClient, auth: FirebaseAuth, context: Context) {
        if (result.resultCode == RESULT_OK) {
            val credential = signInClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            idToken?.let {
                val firebaseCredential = GoogleAuthProvider.getCredential(it, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Google Sign-in successful")
                            Toast.makeText(context, "Google Sign-in successful. Restarting...", Toast.LENGTH_SHORT).show()
                            // Restart the app on successful Google sign-in after a short delay
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500) // Wait 500ms
                                restartApp(context)
                            }
                        } else {
                            Log.e(TAG, "Google Sign-in failed", task.exception)
                            Toast.makeText(context, "Google Sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Log.e(TAG, "Google Sign-in canceled")
            Toast.makeText(context, "Google Sign-in canceled", Toast.LENGTH_SHORT).show()
        }
    }

    fun createAccount(context: Context, auth: FirebaseAuth, email: String, password: String) {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context, "Account created! Please verify your email.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_LONG).show() // Show detailed error
                }
            }
    }

    fun signInWithGoogle(
        activity: Activity,
        signInClient: SignInClient,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Google One Tap Sign-in successful")
                Toast.makeText(activity, "Google One Tap Sign-in successful", Toast.LENGTH_SHORT).show()
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Google One Tap Sign-in failed: ${e.message}", e)
                Toast.makeText(activity, "Google One Tap Sign-in failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun signInWithEmail(context: Context, auth: FirebaseAuth, email: String, password: String) {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(context, "Authentication successful. Restarting...", Toast.LENGTH_SHORT).show()
                        // Restart the app on successful email sign-in (if verified) after a short delay
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500) // Wait 500ms
                            restartApp(context)
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:email not verified")
                        Toast.makeText(context, "Please verify your email.", Toast.LENGTH_SHORT).show()
                        // Optionally sign out the user if email is not verified
                        // auth.signOut()
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show() // Show detailed error
                }
            }
    }

    fun resetPassword(context: Context, auth: FirebaseAuth, email: String) {
        require(email.isNotBlank()) { "Email cannot be blank" }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent")
                    Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "Failed to send password reset email", task.exception)
                    Toast.makeText(context, "Failed to send password reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show() // Show detailed error
                }
            }
    }
}