package com.tempo.beatloom.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.Outline
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ThreadLine(
    progress: Float,
    matched: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Accent,
    waves: Int = 6,
    strokeWidth: Dp = 2.5.dp,
    amplitude: Dp = 5.dp
) {
    Canvas(modifier = modifier) {
        val centerY = size.height / 2f
        if (!matched) {
            drawDashedRail(centerY, Outline, strokeWidth.toPx())
            return@Canvas
        }
        drawDashedRail(centerY, Outline.copy(alpha = 0.5f), strokeWidth.toPx())
        drawThread(
            progress = progress.coerceIn(0f, 1f),
            centerY = centerY,
            color = color,
            waves = waves,
            stroke = strokeWidth.toPx(),
            amplitudePx = amplitude.toPx()
        )
    }
}

private fun DrawScope.drawDashedRail(centerY: Float, color: Color, stroke: Float) {
    val dash = 8f
    var x = 0f
    while (x < size.width) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(x, centerY),
            end = androidx.compose.ui.geometry.Offset(minOf(x + dash, size.width), centerY),
            strokeWidth = stroke * 0.5f,
            cap = StrokeCap.Round
        )
        x += dash * 2f
    }
}

private fun DrawScope.drawThread(
    progress: Float,
    centerY: Float,
    color: Color,
    waves: Int,
    stroke: Float,
    amplitudePx: Float
) {
    if (progress <= 0f || size.width <= 0f) return
    val visibleWidth = size.width * progress
    val samples = 64
    val path = Path()
    for (index in 0..samples) {
        val fraction = index.toFloat() / samples
        val x = visibleWidth * fraction
        val phase = fraction * progress * waves * 2f * PI.toFloat()
        val y = centerY + sin(phase) * amplitudePx
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(
        path = path,
        color = color.copy(alpha = 0.28f),
        style = Stroke(width = stroke * 2.4f, cap = StrokeCap.Round)
    )
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = stroke, cap = StrokeCap.Round)
    )
}
