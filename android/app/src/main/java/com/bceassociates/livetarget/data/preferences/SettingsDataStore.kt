//
//  SettingsDataStore.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

        private val CIRCLE_COLOR_KEY = stringPreferencesKey("circle_color")
        private val NUMBER_COLOR_KEY = stringPreferencesKey("number_color")
        private val CHECK_INTERVAL_KEY = doublePreferencesKey("check_interval")
        private val SELECTED_CALIBER_NAME_KEY = stringPreferencesKey("selected_caliber_name")
        private val ZOOM_FACTOR_KEY = doublePreferencesKey("zoom_factor")
        private val WATCH_INTEGRATION_ENABLED_KEY = booleanPreferencesKey("watch_integration_enabled")
    }

    val circleColor: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[CIRCLE_COLOR_KEY] ?: "FF0000"
        }

    val numberColor: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[NUMBER_COLOR_KEY] ?: "FF0000"
        }

    val checkInterval: Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[CHECK_INTERVAL_KEY] ?: 2.0
        }

    val selectedCaliberName: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[SELECTED_CALIBER_NAME_KEY] ?: ".22 Long Rifle"
        }

    val zoomFactor: Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[ZOOM_FACTOR_KEY] ?: 1.0
        }

    val watchIntegrationEnabled: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[WATCH_INTEGRATION_ENABLED_KEY] ?: false
        }

    suspend fun setCircleColor(color: String) {
        context.dataStore.edit { settings ->
            settings[CIRCLE_COLOR_KEY] = color
        }
    }

    suspend fun setNumberColor(color: String) {
        context.dataStore.edit { settings ->
            settings[NUMBER_COLOR_KEY] = color
        }
    }

    suspend fun setCheckInterval(interval: Double) {
        context.dataStore.edit { settings ->
            settings[CHECK_INTERVAL_KEY] = interval
        }
    }

    suspend fun setSelectedCaliberName(caliberName: String) {
        context.dataStore.edit { settings ->
            settings[SELECTED_CALIBER_NAME_KEY] = caliberName
        }
    }

    suspend fun setZoomFactor(factor: Double) {
        context.dataStore.edit { settings ->
            settings[ZOOM_FACTOR_KEY] = factor
        }
    }

    suspend fun setWatchIntegrationEnabled(enabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[WATCH_INTEGRATION_ENABLED_KEY] = enabled
        }
    }
}