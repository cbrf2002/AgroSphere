@file:Suppress("DEPRECATION")
package com.fsvdevs.agrosphere.util

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.fsvdevs.agrosphere.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object AuthHelper {
    private const val TAG = "AuthHelper"

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
                            Toast.makeText(context, "Google Sign-in successful", Toast.LENGTH_SHORT).show()
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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context, "Account created! Please verify your email.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Account creation failed.", Toast.LENGTH_SHORT).show()
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(context, "Authentication successful.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "signInWithEmail:email not verified")
                        Toast.makeText(context, "Please verify your email.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun resetPassword(context: Context, auth: FirebaseAuth, email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent")
                    Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "Failed to send password reset email", task.exception)
                    Toast.makeText(context, "Failed to send password reset email.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}