package com.fsvdevs.agrosphere.util

import android.content.Context
import com.fsvdevs.agrosphere.R
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {
    private lateinit var appContext: Context
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val signInClient: SignInClient by lazy { Identity.getSignInClient(appContext) }
    val database: DatabaseReference
        get() = FirebaseDatabase.getInstance(appContext.getString(R.string.firebase_reference)).reference

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
        appContext = context.applicationContext  // Use application context to prevent memory leaks
    }
}