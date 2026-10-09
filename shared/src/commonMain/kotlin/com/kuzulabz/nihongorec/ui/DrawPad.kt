package com.kuzulabz.nihongorec.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

fun List<Offset>.toSmoothPath(): Path {
    val path = Path()
    if (isEmpty()) return path

    if (size == 1) {
        val point = first()
        path.moveTo(point.x, point.y)
        path.lineTo(point.x, point.y)
        return path
    }

    if (size == 2) {
        path.moveTo(first().x, first().y)
        path.lineTo(last().x, last().y)
        return path
    }

    val smoothed = applyChaikinSmoothing(this, iterations = 2)

    path.moveTo(smoothed[0].x, smoothed[0].y)

    for (i in 1 until smoothed.size - 1) {
        val p0 = smoothed[i]
        val p1 = smoothed[i + 1]

        val midX = (p0.x + p1.x) / 2f
        val midY = (p0.y + p1.y) / 2f

        path.quadraticTo(p0.x, p0.y, midX, midY)
    }

    path.lineTo(smoothed.last().x, smoothed.last().y)
    return path
}

private fun applyChaikinSmoothing(points: List<Offset>, iterations: Int): List<Offset> {
    var current = points
    repeat(iterations) {
        if (current.size < 3) return@repeat
        val next = mutableListOf<Offset>()
        next.add(current.first())

        for (i in 0 until current.size - 1) {
            val p0 = current[i]
            val p1 = current[i + 1]

            val q = Offset(0.75f * p0.x + 0.25f * p1.x, 0.75f * p0.y + 0.25f * p1.y)
            val r = Offset(0.25f * p0.x + 0.75f * p1.x, 0.25f * p0.y + 0.75f * p1.y)

            next.add(q)
            next.add(r)
        }

        next.add(current.last())
        current = next
    }
    return current
}

@Composable
fun DrawPad(width: Int, height: Int, viewModel: CanvasViewModel) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(viewModel.currentColor, viewModel.strokeWidth) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    viewModel.startActiveStroke(down.position)

                    drag(down.id) { change ->
                        change.consume()
                        viewModel.addActivePoint(change.position)
                    }

                    viewModel.finalizeActiveStroke(width, height)
                }
            }
    ) {

        viewModel.bitmap?.let { b ->
            drawImage(image = b)
        }

        if (viewModel.activePoints.isNotEmpty()) {
            drawPath(
                path = viewModel.activePoints.toSmoothPath(),
                color = viewModel.currentColor,
                style = Stroke(
                    width = viewModel.strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}