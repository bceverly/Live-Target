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
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bceassociates.livetarget.data.AmmoType
import com.bceassociates.livetarget.data.BlackPowderType
import com.bceassociates.livetarget.data.CaliberData
import com.bceassociates.livetarget.data.CartridgeType
import com.bceassociates.livetarget.data.OverlayPosition
import com.bceassociates.livetarget.data.OverlaySettings
import com.bceassociates.livetarget.data.ProjectileType
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
    val maxZoom: Float = 20.0f,
    val minZoom: Float = 1.0f,
    val watchIntegrationEnabled: Boolean = false,
    val watchConnectionStatus: WatchConnectionStatus = WatchConnectionStatus.UNKNOWN,
    val isWatchPaired: Boolean = false,
    
    // Overlay Settings
    val overlayEnabled: Boolean = false,
    val overlayPosition: String = OverlayPosition.TOP_LEFT.name,
    val bulletWeight: Double = 55.0,
    val cartridgeType: String = CartridgeType.METALLIC_CARTRIDGE.name,
    val ammoType: String = AmmoType.FACTORY.name,
    val factoryAmmoName: String = "",
    val handloadPowder: String = "",
    val handloadCharge: Double = 0.0,
    val blackPowderType: String = BlackPowderType.TWOF.name,
    val projectileType: String = ProjectileType.ROUND_BALL.name,
    val blackPowderCharge: Double = 0.0,
) {
    val selectedCaliber: com.bceassociates.livetarget.data.Caliber?
        get() = CaliberData.findCaliberByName(selectedCaliberName)
    
    val overlaySettings: OverlaySettings
        get() = OverlaySettings(
            enabled = overlayEnabled,
            position = OverlayPosition.valueOf(overlayPosition),
            bulletWeight = bulletWeight,
            cartridgeType = CartridgeType.valueOf(cartridgeType),
            ammoType = AmmoType.valueOf(ammoType),
            factoryAmmoName = factoryAmmoName,
            handloadPowder = handloadPowder,
            handloadCharge = handloadCharge,
            blackPowderType = BlackPowderType.valueOf(blackPowderType),
            projectileType = ProjectileType.valueOf(projectileType),
            blackPowderCharge = blackPowderCharge,
            selectedCaliberName = selectedCaliberName
        )
    
    val calculatedGridSize: String
        get() {
            val caliber = CaliberData.findCaliberByName(selectedCaliberName)
            return if (caliber != null) {
                // Use typical camera resolution as fallback since we don't have access to currentBitmap here
                val imageWidth = 1280 // Default camera resolution width from CameraPreview
                val imageHeight = 720 // Default camera resolution height from CameraPreview
                
                // Calculate based on camera resolution and current zoom
                val fieldOfViewInches = 36.0
                val pixelsPerInch = minOf(imageWidth, imageHeight) / fieldOfViewInches
                val holeMultiplier = 1.2 // Bullet holes are typically 20% larger than bullet diameter
                val holeDiameterPixels = (caliber.diameterInches * holeMultiplier * pixelsPerInch * zoomFactor).toInt()
                val gridSquareSize = (holeDiameterPixels * 3).coerceAtLeast(10).coerceAtMost(100)
                "${gridSquareSize}×${gridSquareSize} pixels"
            } else {
                "Unknown"
            }
        }
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
                    // Update both old and new detection systems
                    changeDetector.setMinChangeSize(caliber.pixelSize)
                    updateDetectorParameters()
                }
            }
        }

        viewModelScope.launch {
            settingsDataStore.zoomFactor.collect { factor ->
                _uiState.value = _uiState.value.copy(zoomFactor = factor)
                updateDetectorParameters()
            }
        }

        viewModelScope.launch {
            settingsDataStore.watchIntegrationEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(watchIntegrationEnabled = enabled)
            }
        }

        // Observe overlay settings
        viewModelScope.launch {
            settingsDataStore.overlayEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(overlayEnabled = enabled)
            }
        }

        viewModelScope.launch {
            settingsDataStore.overlayPosition.collect { position ->
                _uiState.value = _uiState.value.copy(overlayPosition = position)
            }
        }

        viewModelScope.launch {
            settingsDataStore.bulletWeight.collect { weight ->
                _uiState.value = _uiState.value.copy(bulletWeight = weight)
            }
        }

        viewModelScope.launch {
            settingsDataStore.ammoType.collect { type ->
                _uiState.value = _uiState.value.copy(ammoType = type)
            }
        }

        viewModelScope.launch {
            settingsDataStore.factoryAmmoName.collect { name ->
                _uiState.value = _uiState.value.copy(factoryAmmoName = name)
            }
        }

        viewModelScope.launch {
            settingsDataStore.handloadPowder.collect { powder ->
                _uiState.value = _uiState.value.copy(handloadPowder = powder)
            }
        }

        viewModelScope.launch {
            settingsDataStore.handloadCharge.collect { charge ->
                _uiState.value = _uiState.value.copy(handloadCharge = charge)
            }
        }

        viewModelScope.launch {
            settingsDataStore.cartridgeType.collect { type ->
                _uiState.value = _uiState.value.copy(cartridgeType = type)
            }
        }

        viewModelScope.launch {
            settingsDataStore.blackPowderType.collect { type ->
                _uiState.value = _uiState.value.copy(blackPowderType = type)
            }
        }

        viewModelScope.launch {
            settingsDataStore.projectileType.collect { type ->
                _uiState.value = _uiState.value.copy(projectileType = type)
            }
        }

        viewModelScope.launch {
            settingsDataStore.blackPowderCharge.collect { charge ->
                _uiState.value = _uiState.value.copy(blackPowderCharge = charge)
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
    
    /**
     * Updates the change detector with current caliber and zoom parameters
     */
    private fun updateDetectorParameters() {
        val currentState = _uiState.value
        val caliber = CaliberData.findCaliberByName(currentState.selectedCaliberName)
        if (caliber != null) {
            changeDetector.setCaliberParameters(
                diameterInches = caliber.diameterInches,
                zoomFactor = currentState.zoomFactor
            )
        }
    }

    fun startDetection() {
        Log.d(TAG, "Starting detection")
        changeDetector.startDetection()
        Log.d(TAG, "Detection started - isDetecting: ${changeDetector.isDetecting.value}")
    }

    fun stopDetection() {
        Log.d(TAG, "Stopping detection")
        changeDetector.stopDetection()
        Log.d(TAG, "Detection stopped - isDetecting: ${changeDetector.isDetecting.value}")
    }

    fun clearChanges() {
        Log.d(TAG, "Clearing changes")
        changeDetector.clearChanges()
    }
    

    fun processImage(bitmap: Bitmap) {
        Log.d(TAG, "processImage called with bitmap: ${bitmap.width}x${bitmap.height}")
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
                Log.d(TAG, "saveImage called")
                val bitmap = currentBitmap
                if (bitmap == null) {
                    Log.w(TAG, "No current bitmap to save")
                    val context = getApplication<Application>()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        Toast.makeText(context, "No image to save - take a photo first", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val compositeBitmap = createCompositeImage(bitmap)
                val success = saveImageToGallery(compositeBitmap)
                if (success) {
                    Log.d(TAG, "Image saved successfully")
                    // Show success message on main thread
                    val context = getApplication<Application>()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Failed to save image")
                    val context = getApplication<Application>()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image", e)
            }
        }
    }

    private fun createCompositeImage(originalBitmap: Bitmap): Bitmap {
        // Rotate the bitmap 90 degrees clockwise to fix the orientation
        val matrix = android.graphics.Matrix()
        matrix.postRotate(90f)
        val rotatedBitmap = Bitmap.createBitmap(
            originalBitmap, 0, 0, 
            originalBitmap.width, originalBitmap.height, 
            matrix, true
        )
        
        val compositeBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(compositeBitmap)
        val changes = _uiState.value.detectedChanges


        // Draw impact markers - adjust coordinates for 90-degree rotation
        changes.forEachIndexed { index, impact ->
            // Hit detection was performed on the original (unrotated) bitmap
            // After rotating the bitmap 90° clockwise, we need to transform coordinates
            // Original coordinates are in the unrotated bitmap space
            val centerX = impact.location.x * compositeBitmap.width
            val centerY = impact.location.y * compositeBitmap.height
            
            // Scale radius and stroke based on image size (assume ~1000px is base size)
            val scaleFactor = minOf(compositeBitmap.width, compositeBitmap.height) / 1000f
            val radius = 30f * scaleFactor
            val strokeWidth = 6f * scaleFactor
            

            // Parse colors
            val circleColor = Color.parseColor("#${_uiState.value.circleColor}")
            val numberColor = Color.parseColor("#${_uiState.value.numberColor}")

            // Draw circle
            val circlePaint = Paint().apply {
                color = circleColor
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                isAntiAlias = true
            }
            canvas.drawCircle(centerX, centerY, radius, circlePaint)

            // Draw impact number
            val textPaint = Paint().apply {
                color = numberColor
                textSize = 48f * scaleFactor
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                isFakeBoldText = true
            }

            val textBounds = Rect()
            textPaint.getTextBounds(impact.number.toString(), 0, impact.number.toString().length, textBounds)
            val textY = centerY + textBounds.height() / 2f

            canvas.drawText(impact.number.toString(), centerX, textY, textPaint)
        }

        // Draw overlay if enabled
        val overlaySettings = _uiState.value.overlaySettings
        if (overlaySettings.enabled) {
            drawOverlay(canvas, compositeBitmap, overlaySettings)
        }

        return compositeBitmap
    }

    private fun drawOverlay(canvas: Canvas, bitmap: Bitmap, overlaySettings: OverlaySettings) {
        val overlayText = overlaySettings.getOverlayText()
        
        // Create text paint
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }
        
        // Measure text
        val textBounds = Rect()
        val lines = overlayText.split('\n')
        var maxLineWidth = 0f
        var totalHeight = 0f
        
        lines.forEach { line ->
            textPaint.getTextBounds(line, 0, line.length, textBounds)
            maxLineWidth = maxOf(maxLineWidth, textBounds.width().toFloat())
            totalHeight += textBounds.height().toFloat()
        }
        
        // Add padding
        val padding = 24f
        val overlayWidth = maxLineWidth + (padding * 2)
        val overlayHeight = totalHeight + (padding * 2) + (lines.size - 1) * 12f // line spacing
        
        // Calculate position based on overlay position setting
        val overlayRect = calculateOverlayRect(
            bitmap.width.toFloat(), 
            bitmap.height.toFloat(), 
            overlayWidth, 
            overlayHeight, 
            overlaySettings.position
        )
        
        // Draw rounded rectangle background
        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#B3000000") // 70% transparent black
            isAntiAlias = true
        }
        
        val cornerRadius = 24f
        val rectF = RectF(overlayRect.left, overlayRect.top, overlayRect.right, overlayRect.bottom)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
        
        // Draw text lines
        var currentY = overlayRect.top + padding + textBounds.height()
        lines.forEach { line ->
            canvas.drawText(line, overlayRect.left + padding, currentY, textPaint)
            currentY += textBounds.height() + 12f // line spacing
        }
    }
    
    private fun calculateOverlayRect(
        imageWidth: Float, 
        imageHeight: Float, 
        overlayWidth: Float, 
        overlayHeight: Float, 
        position: OverlayPosition
    ): RectF {
        val margin = 60f
        
        return when (position) {
            OverlayPosition.TOP_LEFT -> 
                RectF(margin, margin, margin + overlayWidth, margin + overlayHeight)
            OverlayPosition.TOP_CENTER -> 
                RectF((imageWidth - overlayWidth) / 2, margin, (imageWidth + overlayWidth) / 2, margin + overlayHeight)
            OverlayPosition.TOP_RIGHT -> 
                RectF(imageWidth - overlayWidth - margin, margin, imageWidth - margin, margin + overlayHeight)
            OverlayPosition.BOTTOM_LEFT -> 
                RectF(margin, imageHeight - overlayHeight - margin, margin + overlayWidth, imageHeight - margin)
            OverlayPosition.BOTTOM_CENTER -> 
                RectF((imageWidth - overlayWidth) / 2, imageHeight - overlayHeight - margin, (imageWidth + overlayWidth) / 2, imageHeight - margin)
            OverlayPosition.BOTTOM_RIGHT -> 
                RectF(imageWidth - overlayWidth - margin, imageHeight - overlayHeight - margin, imageWidth - margin, imageHeight - margin)
        }
    }

    private suspend fun saveImageToGallery(bitmap: Bitmap): Boolean {
        val context = getApplication<Application>()
        
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageToGalleryModern(bitmap, context)
            } else {
                saveImageToGalleryLegacy(bitmap, context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            false
        }
    }
    
    private fun saveImageToGalleryModern(bitmap: Bitmap, context: Application): Boolean {
        val contentResolver = context.contentResolver
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val filename = "LiveTarget_${dateFormat.format(Date())}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/LiveTarget")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        
        Log.d(TAG, "Saving image (modern) with filename: $filename")

        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d(TAG, "MediaStore insert result: $imageUri")

        imageUri?.let { uri ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use { stream ->
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                Log.d(TAG, "Bitmap compress success: $success")
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            Log.d(TAG, "Image saved to gallery (modern): $uri")
            return true
        } ?: run {
            Log.e(TAG, "Failed to create MediaStore entry")
            return false
        }
    }
    
    private fun saveImageToGalleryLegacy(bitmap: Bitmap, context: Application): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val filename = "LiveTarget_${dateFormat.format(Date())}.jpg"
        
        Log.d(TAG, "Saving image (legacy) with filename: $filename")
        
        // Use MediaStore.Images.Media.insertImage for older Android versions
        val savedImageURL = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            filename,
            "Live Target shot analysis"
        )
        
        Log.d(TAG, "Legacy save result: $savedImageURL")
        return savedImageURL != null
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
        Log.d(TAG, "setZoomFactor called with: ${factor}x (current: ${_uiState.value.zoomFactor}x)")
        viewModelScope.launch {
            settingsDataStore.setZoomFactor(factor)
        }
    }

    fun setWatchIntegrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setWatchIntegrationEnabled(enabled)
        }
    }

    // Overlay Settings Setters
    fun setOverlayEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setOverlayEnabled(enabled)
        }
    }

    fun setOverlayPosition(position: String) {
        viewModelScope.launch {
            settingsDataStore.setOverlayPosition(position)
        }
    }

    fun setBulletWeight(weight: Double) {
        viewModelScope.launch {
            settingsDataStore.setBulletWeight(weight)
        }
    }

    fun setAmmoType(type: String) {
        viewModelScope.launch {
            settingsDataStore.setAmmoType(type)
        }
    }

    fun setFactoryAmmoName(name: String) {
        viewModelScope.launch {
            settingsDataStore.setFactoryAmmoName(name)
        }
    }

    fun setHandloadPowder(powder: String) {
        viewModelScope.launch {
            settingsDataStore.setHandloadPowder(powder)
        }
    }

    fun setHandloadCharge(charge: Double) {
        viewModelScope.launch {
            settingsDataStore.setHandloadCharge(charge)
        }
    }

    fun setCartridgeType(type: String) {
        viewModelScope.launch {
            settingsDataStore.setCartridgeType(type)
        }
    }

    fun setBlackPowderType(type: String) {
        viewModelScope.launch {
            settingsDataStore.setBlackPowderType(type)
        }
    }

    fun setProjectileType(type: String) {
        viewModelScope.launch {
            settingsDataStore.setProjectileType(type)
        }
    }

    fun setBlackPowderCharge(charge: Double) {
        viewModelScope.launch {
            settingsDataStore.setBlackPowderCharge(charge)
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

    fun updateZoomCapabilities(minZoom: Float, maxZoom: Float) {
        Log.d(TAG, "Updating zoom capabilities: ${minZoom}x to ${maxZoom}x")
        _uiState.value = _uiState.value.copy(minZoom = minZoom, maxZoom = maxZoom)
        Log.d(TAG, "Zoom capabilities updated in UI state: min=${_uiState.value.minZoom}, max=${_uiState.value.maxZoom}")
    }
}