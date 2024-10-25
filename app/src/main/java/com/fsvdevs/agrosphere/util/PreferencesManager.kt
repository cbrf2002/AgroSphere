package com.fsvdevs.agrosphere.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.fsvdevs.agrosphere.ui.theme.AppTheme


val Context.dataStore by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
}

class PreferencesManager(private val context: Context) {

    // Save theme preference
    suspend fun saveThemePreference(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
            Log.d("PreferencesManager", "Saved theme preference: ${theme.name}")
        }
    }

    // Save dynamic color preference
    suspend fun saveDynamicColorPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = isEnabled
            Log.d("PreferencesManager", "Saved dynamic color preference: $isEnabled")
        }
    }

    // Retrieve theme preference
    val themeFlow: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.AUTO.name
            AppTheme.valueOf(themeName)
        }

    // Retrieve dynamic color preference
    val dynamicColorFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] == true // Default to true
        }
}