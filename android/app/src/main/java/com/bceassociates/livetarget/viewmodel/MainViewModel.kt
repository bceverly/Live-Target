//
//  MainViewModel.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.viewmodel

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bceassociates.livetarget.data.CaliberData
import com.bceassociates.livetarget.data.model.ChangePoint
import com.bceassociates.livetarget.data.preferences.SettingsDataStore
import com.bceassociates.livetarget.detection.ChangeDetector
import com.bceassociates.livetarget.watch.WatchConnectivityManager
import com.bceassociates.livetarget.watch.WatchConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MainUiState(
    val isDetecting: Boolean = false,
    val detectedChanges: List<ChangePoint> = emptyList(),
    val circleColor: String = "FF0000",
    val numberColor: String = "FF0000",
    val checkInterval: Double = 2.0,
    val selectedCaliberName: String = ".22 Long Rifle",
    val zoomFactor: Double = 1.0,
    val watchIntegrationEnabled: Boolean = false,
    val watchConnectionStatus: WatchConnectionStatus = WatchConnectionStatus.UNKNOWN,
    val isWatchPaired: Boolean = false,
) {
    val selectedCaliber: com.bceassociates.livetarget.data.Caliber?
        get() = CaliberData.findCaliberByName(selectedCaliberName)
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val settingsDataStore = SettingsDataStore(application)
    private val changeDetector = ChangeDetector()
    private val watchConnectivityManager = WatchConnectivityManager.getInstance(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var currentBitmap: Bitmap? = null

    init {
        // Observe settings changes
        viewModelScope.launch {
            settingsDataStore.circleColor.collect { color ->
                _uiState.value = _uiState.value.copy(circleColor = color)
            }
        }

        viewModelScope.launch {
            settingsDataStore.numberColor.collect { color ->
                _uiState.value = _uiState.value.copy(numberColor = color)
            }
        }

        viewModelScope.launch {
            settingsDataStore.checkInterval.collect { interval ->
                _uiState.value = _uiState.value.copy(checkInterval = interval)
                changeDetector.setCheckInterval(interval)
            }
        }

        viewModelScope.launch {
            settingsDataStore.selectedCaliberName.collect { caliberName ->
                _uiState.value = _uiState.value.copy(selectedCaliberName = caliberName)
                val caliber = CaliberData.findCaliberByName(caliberName)
                if (caliber != null) {
                    changeDetector.setMinChangeSize(caliber.pixelSize)
                }
            }
        }

        viewModelScope.launch {
            settingsDataStore.zoomFactor.collect { factor ->
                _uiState.value = _uiState.value.copy(zoomFactor = factor)
            }
        }

        viewModelScope.launch {
            settingsDataStore.watchIntegrationEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(watchIntegrationEnabled = enabled)
            }
        }

        // Observe watch connectivity state
        viewModelScope.launch {
            watchConnectivityManager.watchConnectionStatus.collect { status ->
                _uiState.value = _uiState.value.copy(watchConnectionStatus = status)
            }
        }

        viewModelScope.launch {
            watchConnectivityManager.isWatchPaired.collect { paired ->
                _uiState.value = _uiState.value.copy(isWatchPaired = paired)
            }
        }

        // Observe change detector state
        viewModelScope.launch {
            changeDetector.isDetecting.collect { isDetecting ->
                _uiState.value = _uiState.value.copy(isDetecting = isDetecting)
            }
        }

        viewModelScope.launch {
            changeDetector.detectedChanges.collect { changes ->
                _uiState.value = _uiState.value.copy(detectedChanges = changes)
            }
        }
    }

    fun startDetection() {
        Log.d(TAG, "Starting detection")
        changeDetector.startDetection()
    }

    fun stopDetection() {
        Log.d(TAG, "Stopping detection")
        changeDetector.stopDetection()
    }

    fun clearChanges() {
        Log.d(TAG, "Clearing changes")
        changeDetector.clearChanges()
    }

    fun processImage(bitmap: Bitmap) {
        currentBitmap = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false)
        changeDetector.detectChanges(bitmap)
        
        // Send to watch if enabled
        if (_uiState.value.watchIntegrationEnabled) {
            // This will be handled by the ChangeDetector when it detects changes
        }
    }

    fun saveImage() {
        viewModelScope.launch {
            try {
                val bitmap = currentBitmap
                if (bitmap == null) {
                    Log.w(TAG, "No current bitmap to save")
                    return@launch
                }

                val compositeBitmap = createCompositeImage(bitmap)
                saveImageToGallery(compositeBitmap)
                Log.d(TAG, "Image saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image", e)
            }
        }
    }

    private fun createCompositeImage(originalBitmap: Bitmap): Bitmap {
        val compositeBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(compositeBitmap)
        val changes = _uiState.value.detectedChanges

        // Draw impact markers
        changes.forEach { impact ->
            val centerX = impact.location.x * compositeBitmap.width
            val centerY = impact.location.y * compositeBitmap.height
            val radius = 30f

            // Parse colors
            val circleColor = Color.parseColor("#${_uiState.value.circleColor}")
            val numberColor = Color.parseColor("#${_uiState.value.numberColor}")

            // Draw circle
            val circlePaint = Paint().apply {
                color = circleColor
                style = Paint.Style.STROKE
                strokeWidth = 6f
                isAntiAlias = true
            }
            canvas.drawCircle(centerX, centerY, radius, circlePaint)

            // Draw impact number
            val textPaint = Paint().apply {
                color = numberColor
                textSize = 48f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }

            val textBounds = Rect()
            textPaint.getTextBounds(impact.number.toString(), 0, impact.number.toString().length, textBounds)
            val textY = centerY + textBounds.height() / 2f

            canvas.drawText(impact.number.toString(), centerX, textY, textPaint)
        }

        return compositeBitmap
    }

    private suspend fun saveImageToGallery(bitmap: Bitmap) {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver

        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val filename = "LiveTarget_${dateFormat.format(Date())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/LiveTarget")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }

            Log.d(TAG, "Image saved to gallery: $uri")
        } ?: run {
            Log.e(TAG, "Failed to create MediaStore entry")
        }
    }

    fun setCircleColor(color: String) {
        viewModelScope.launch {
            settingsDataStore.setCircleColor(color)
        }
    }

    fun setNumberColor(color: String) {
        viewModelScope.launch {
            settingsDataStore.setNumberColor(color)
        }
    }

    fun setCheckInterval(interval: Double) {
        viewModelScope.launch {
            settingsDataStore.setCheckInterval(interval)
        }
    }

    fun setSelectedCaliberName(caliberName: String) {
        viewModelScope.launch {
            settingsDataStore.setSelectedCaliberName(caliberName)
        }
    }

    fun setZoomFactor(factor: Double) {
        viewModelScope.launch {
            settingsDataStore.setZoomFactor(factor)
        }
    }

    fun setWatchIntegrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setWatchIntegrationEnabled(enabled)
        }
    }

    fun testWatchConnectivity() {
        watchConnectivityManager.testWatchConnectivity()
    }

    fun sendImpactToWatch(impact: ChangePoint, image: Bitmap) {
        if (_uiState.value.watchIntegrationEnabled) {
            val circleColor = Color.parseColor("#${_uiState.value.circleColor}")
            val numberColor = Color.parseColor("#${_uiState.value.numberColor}")
            watchConnectivityManager.sendImpactToWatch(impact, image, circleColor, numberColor)
        }
    }
}