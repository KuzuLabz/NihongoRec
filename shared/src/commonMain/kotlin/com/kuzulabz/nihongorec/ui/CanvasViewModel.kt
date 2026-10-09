package com.kuzulabz.nihongorec.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzulabz.nihongorec.ml.Category
import com.kuzulabz.nihongorec.ml.CharClassifierOnnx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

val emptyPredictions = List(10) { index ->
    Category(
        label = "",
        score = 0f,
        index = index
    )
}

data class CanvasUiState(
    val predictions: List<Category> = emptyPredictions,
    val isProcessing: Boolean = false,
    val isModelReady: Boolean = false,
    val error: String? = null
)

class CanvasViewModel(
    private val classifier: CharClassifierOnnx,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CanvasUiState())
    val uiState: StateFlow<CanvasUiState> = _uiState.asStateFlow()
    val strokes = mutableStateListOf<List<Offset>>()
    val activePoints = mutableStateListOf<Offset>()

    var currentColor: Color = Color.Unspecified
    val strokeWidth: Dp = 12.dp
    private var density: Density = Density(1f)

    private var lastWidth: Int = 0
    private var lastHeight: Int = 0

    var bitmap by mutableStateOf<ImageBitmap?>(null)
        private set
    var canvas: Canvas? = null
        private set

    var redrawTrigger = 0
        private set

    init {
        // Initialize Onnx model on background coroutine
        viewModelScope.launch {
            try {
                classifier.init()
                _uiState.value = _uiState.value.copy(isModelReady = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateThemeColor(color: Color) {
        if (currentColor != color) {
            currentColor = color
            // Re-render canvas if strokes already exist under the previous color theme
            if (strokes.isNotEmpty() && lastWidth > 0 && lastHeight > 0) {
                redrawBitmap(lastWidth, lastHeight)
            }
        }
    }

    fun initializeBuffer(
        newWidth: Int,
        newHeight: Int,
        currentDensity: Density
    ) {
        this.density = currentDensity

        if (lastWidth == 0 || lastHeight == 0) {
            lastWidth = newWidth
            lastHeight = newHeight
            return
        }

        if (lastWidth != newWidth || lastHeight != newHeight) {
            rescaleStrokes(
                oldSize = Size(lastWidth.toFloat(), lastHeight.toFloat()),
                newSize = Size(newWidth.toFloat(), newHeight.toFloat()),
            )

            lastWidth = newWidth
            lastHeight = newHeight
        }
    }

    fun startActiveStroke(startPoint: Offset) {
        activePoints.clear()
        activePoints.add(startPoint)
    }

    fun addActivePoint(point: Offset) {
        val lastPoint = activePoints.lastOrNull()
        if (lastPoint == null) {
            activePoints.add(point)
            return
        }

        // Filter out micro-tremors closer than 4 pixels
        val dx = point.x - lastPoint.x
        val dy = point.y - lastPoint.y
        if ((dx * dx + dy * dy) >= 16f) { // 4px squared distance
            activePoints.add(point)
        }
    }

    fun finalizeActiveStroke(width: Int, height: Int) {
        if (activePoints.isNotEmpty()) { // Changed from > 1
            strokes.add(activePoints.toList())
            redrawBitmap(width, height)
            classifyCanvasSnapshot()
        }
        activePoints.clear()
    }

    fun undo() {
        if (activePoints.isNotEmpty()) {
            activePoints.clear()
            return
        }

        // Pop the last finished stroke from history
        if (strokes.isNotEmpty()) {
            strokes.removeAt(strokes.lastIndex)
            redrawBitmap(lastWidth, lastHeight)

            if (strokes.isEmpty()) {
                _uiState.value = _uiState.value.copy(predictions = emptyPredictions)
            } else {
                classifyCanvasSnapshot()
            }
        }
    }

    fun redrawBitmap(
        width: Int,
        height: Int
    ) {
        val targetWidth = if (width > 0) width else 1
        val targetHeight = if (height > 0) height else 1

        val newBitmap = ImageBitmap(targetWidth, targetHeight)
        val targetCanvas = Canvas(newBitmap)
        val drawScope = CanvasDrawScope()

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = targetCanvas,
            size = Size(targetWidth.toFloat(), targetHeight.toFloat())
        ) {
            strokes.forEach { points ->
                if (points.isNotEmpty()) { // Changed from > 1
                    drawPath(
                        path = points.toSmoothPath(),
                        color = currentColor,
                        style = Stroke(
                            width = strokeWidth.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        bitmap = newBitmap
        canvas = targetCanvas
        redrawTrigger++
    }

    private fun rescaleStrokes(
        oldSize: Size,
        newSize: Size
    ) {
        if (oldSize.width <= 0f || oldSize.height <= 0f) return

        // Uniform scale factor (Fit)
        val scale = min(newSize.width / oldSize.width, newSize.height / oldSize.height)

        // Centering offsets
        val offsetX = (newSize.width - oldSize.width * scale) / 2f
        val offsetY = (newSize.height - oldSize.height * scale) / 2f

        for (i in strokes.indices) {
            strokes[i] = strokes[i].map { pt ->
                Offset(
                    x = pt.x * scale + offsetX,
                    y = pt.y * scale + offsetY
                )
            }
        }

        redrawBitmap(newSize.width.toInt(), newSize.height.toInt())
    }

    fun clearCanvas() {
        strokes.clear()
        activePoints.clear()
        val b = bitmap ?: return
        val newBitmap = ImageBitmap(b.width, b.height)
        bitmap = newBitmap
        canvas = Canvas(newBitmap)
        redrawTrigger++
        _uiState.value = _uiState.value.copy(predictions = emptyPredictions)
    }

    fun classifyCanvasSnapshot() {
        if (!_uiState.value.isModelReady || _uiState.value.isProcessing) return

        val displayBitmap = bitmap ?: return

        println("Classifying canvas snapshot...")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)

            try {
                // Pass the primary user canvas directly.
                // Dynamic spatial resizing and color inversion are handled in CharClassifierOnnx.
                val results = classifier.classify(displayBitmap)
                println("Predictions count: ${results.size}")
                println(results.first().score)
                println(results.last().score)

                _uiState.value = _uiState.value.copy(
                    predictions = results,
                    isProcessing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message
                )
            }
        }
    }

    // Automatically called when the ViewModel is destroyed
    override fun onCleared() {
        // Launch cleanup in a non-cancellable coroutine context
        viewModelScope.launch {
            withContext(NonCancellable + Dispatchers.Default) {
                classifier.close()
            }
        }
    }
}