package com.kuzulabz.nihongorec.ml

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import cn.enaium.onnxruntime.*
import com.kuzulabz.nihongorec.ui.emptyPredictions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nihongorec.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
private suspend fun loadResourceBytes(path: String): ByteArray {
    // 1. Prefer Compose Multiplatform resource (works everywhere except some packaged desktop builds)
    try {
        return Res.readBytes(path)
    } catch (e: Exception) {
        // 2. Fallback for packaged desktop builds that use appResourcesRootDir
        val resourcesDir = System.getProperty("compose.application.resources.dir")
            ?: throw IllegalStateException(
                "Could not load resource '$path'. " +
                        "Neither Compose resource nor compose.application.resources.dir is available.",
                e
            )

        // path is "files/char_classifier.onnx" → we only need the filename
        val fileName = path.substringAfterLast('/')
        val file = java.io.File(resourcesDir, fileName)

        if (!file.exists()) {
            throw IllegalStateException(
                "Resource '$path' not found as Compose resource and not found at ${file.absolutePath}",
                e
            )
        }

        println("Loaded $fileName from packaged resources: ${file.absolutePath}")
        return file.readBytes()
    }
}

data class Category(
    val label: String,
    val score: Float,
    val index: Int
)

class CharClassifierOnnx(
    private val modelPath: String = "files/char_classifier.onnx",
    private var confidenceThreshold: Float = 0.001f
) {

    private var labels: List<String> = emptyList()
    private var env: Env? = null
    private var session: Session? = null
    private var isInitialized = false

    @OptIn(ExperimentalResourceApi::class)
    suspend fun init() = withContext(Dispatchers.Default) {
        if (isInitialized) return@withContext
        println("Initializing CharClassifier (ONNX Runtime)...")

        val labelsText = loadResourceBytes("files/char_classifier_labels.txt").decodeToString()
        labels = labelsText.map { it.toString() }

        val modelBytes = loadResourceBytes(modelPath)

        val currentEnv = createEnv()
        val currentSession = createSession(currentEnv, modelBytes)

        env = currentEnv
        session = currentSession
        isInitialized = true
        println("Initialized CharClassifier!")
    }

    private fun postprocess(outputTensor: FloatTensor): List<Category> {
        val probabilities = outputTensor.data

        val topCategories = probabilities.mapIndexed { index, score ->
            val label = labels.getOrElse(index) { index.toString() }
            Category(label = label, score = score, index = index)
        }
            .filter { it.score > confidenceThreshold}
            .sortedByDescending { it.score }
            .take(10)

        if (topCategories.isEmpty()) return emptyPredictions

        val topSum = topCategories.sumOf { it.score.toDouble() }.toFloat()

        val normalized = if (topSum > 0f) {
            topCategories.map { category ->
                category.copy(score = category.score / topSum)
            }
        } else {
            topCategories
        }

        return if (normalized.size < 10) {
            normalized + List(10 - normalized.size) { index ->
                Category(
                    label = "",
                    score = 0f,
                    index = normalized.size + index
                )
            }
        } else {
            normalized
        }
    }

    suspend fun classify(bitmap: ImageBitmap): List<Category> = withContext(Dispatchers.Default) {
        if (!isInitialized || session == null) {
            init()
        }

        val activeSession = checkNotNull(session) { "ONNX Session failed to initialize." }

        val grayscaleData = bitmapToGrayscaleFloatArray(bitmap)
        val inputShape = longArrayOf(1, 1, bitmap.height.toLong(), bitmap.width.toLong())
        val inputTensor = grayscaleData.toTensor(inputShape)

        val inputName = activeSession.inputNames.firstOrNull() ?: "input"

        val outputs = activeSession.run(inputName to inputTensor)

        val outputName = activeSession.outputNames.firstOrNull() ?: outputs.keys.first()
        val outputTensor = outputs.getValue(outputName) as FloatTensor

        val categories = postprocess(outputTensor)

        inputTensor.close()

        return@withContext categories
    }

    private fun bitmapToGrayscaleFloatArray(bitmap: ImageBitmap): FloatArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixelMap = bitmap.toPixelMap()
        val floatBuffer = FloatArray(width * height)

        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = pixelMap[x, y]

                val intensity = if (color.alpha > 0f) {
                    val luminance = (0.299f * color.red) + (0.587f * color.green) + (0.114f * color.blue)
                    (1.0f - luminance) * color.alpha
                } else {
                    0.0f
                }

                floatBuffer[index++] = intensity
            }
        }
        return floatBuffer
    }

    fun close() {
        session?.close()
        env?.close()
        session = null
        env = null
        isInitialized = false
    }
}