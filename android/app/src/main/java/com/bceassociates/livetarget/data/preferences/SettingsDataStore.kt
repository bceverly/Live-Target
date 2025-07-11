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
import com.bceassociates.livetarget.data.AmmoType
import com.bceassociates.livetarget.data.OverlayPosition
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
        
        // Overlay Settings
        private val OVERLAY_ENABLED_KEY = booleanPreferencesKey("overlay_enabled")
        private val OVERLAY_POSITION_KEY = stringPreferencesKey("overlay_position")
        private val BULLET_WEIGHT_KEY = doublePreferencesKey("bullet_weight")
        private val AMMO_TYPE_KEY = stringPreferencesKey("ammo_type")
        private val FACTORY_AMMO_NAME_KEY = stringPreferencesKey("factory_ammo_name")
        private val HANDLOAD_POWDER_KEY = stringPreferencesKey("handload_powder")
        private val HANDLOAD_CHARGE_KEY = doublePreferencesKey("handload_charge")
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
    
    // Overlay Settings Flows
    val overlayEnabled: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[OVERLAY_ENABLED_KEY] ?: false
        }
    
    val overlayPosition: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[OVERLAY_POSITION_KEY] ?: OverlayPosition.TOP_LEFT.name
        }
    
    val bulletWeight: Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[BULLET_WEIGHT_KEY] ?: 55.0
        }
    
    val ammoType: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[AMMO_TYPE_KEY] ?: AmmoType.FACTORY.name
        }
    
    val factoryAmmoName: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[FACTORY_AMMO_NAME_KEY] ?: ""
        }
    
    val handloadPowder: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[HANDLOAD_POWDER_KEY] ?: ""
        }
    
    val handloadCharge: Flow<Double> =
        context.dataStore.data.map { preferences ->
            preferences[HANDLOAD_CHARGE_KEY] ?: 0.0
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
    
    // Overlay Settings Setters
    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[OVERLAY_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun setOverlayPosition(position: String) {
        context.dataStore.edit { settings ->
            settings[OVERLAY_POSITION_KEY] = position
        }
    }
    
    suspend fun setBulletWeight(weight: Double) {
        context.dataStore.edit { settings ->
            settings[BULLET_WEIGHT_KEY] = weight
        }
    }
    
    suspend fun setAmmoType(type: String) {
        context.dataStore.edit { settings ->
            settings[AMMO_TYPE_KEY] = type
        }
    }
    
    suspend fun setFactoryAmmoName(name: String) {
        context.dataStore.edit { settings ->
            settings[FACTORY_AMMO_NAME_KEY] = name
        }
    }
    
    suspend fun setHandloadPowder(powder: String) {
        context.dataStore.edit { settings ->
            settings[HANDLOAD_POWDER_KEY] = powder
        }
    }
    
    suspend fun setHandloadCharge(charge: Double) {
        context.dataStore.edit { settings ->
            settings[HANDLOAD_CHARGE_KEY] = charge
        }
    }
}