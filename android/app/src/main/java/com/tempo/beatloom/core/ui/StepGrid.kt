package com.tempo.beatloom.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tempo.beatloom.R
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.MonoTiny
import com.tempo.beatloom.core.theme.Outline
import com.tempo.beatloom.core.theme.PrimaryBright
import com.tempo.beatloom.core.theme.PrimaryDeep
import com.tempo.beatloom.core.theme.SurfaceAlt
import com.tempo.beatloom.core.theme.SurfaceCard
import com.tempo.beatloom.core.theme.TextMuted
import com.tempo.beatloom.core.theme.TextSecondary
import com.tempo.beatloom.domain.model.Voice

private const val COLUMNS = 8

@Composable
fun StepGrid(
    voices: List<Voice>,
    pattern: List<List<Boolean>>,
    lockedRows: List<Boolean>,
    threadProgress: List<Float>,
    playheadStep: Int,
    labelsVisible: Boolean,
    highContrast: Boolean,
    interactive: Boolean,
    onToggle: (Int, Int) -> Unit,
    onPaint: (Int, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val boardWidth = if (maxWidth < 380.dp) maxWidth else 380.dp
        val frame = 11.dp
        val cell = (boardWidth - frame * 2) / COLUMNS
        val padHeight: Dp = if (voices.size >= 5) 36.dp else 44.dp
        val cellPx = with(LocalDensity.current) { cell.toPx() }
        val patternState = rememberUpdatedState(pattern)
        val lockedState = rememberUpdatedState(lockedRows)

        Column(
            modifier = Modifier
                .width(boardWidth)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceCard.copy(alpha = 0.7f))
                .border(1.dp, Outline, RoundedCornerShape(18.dp))
                .padding(frame)
        ) {
            StepNumberStrip(cell = cell)
            Spacer(modifier = Modifier.height(8.dp))
            voices.forEachIndexed { row, voice ->
                if (labelsVisible) {
                    VoiceChip(
                        voice = voice,
                        matched = threadProgress.getOrElse(row) { 0f } > 0f
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                PadLine(
                    row = row,
                    voice = voice,
                    line = 0,
                    cell = cell,
                    cellPx = cellPx,
                    padHeight = padHeight,
                    pattern = patternState.value,
                    locked = lockedState.value.getOrElse(row) { false },
                    threadProgress = threadProgress.getOrElse(row) { 0f },
                    playheadStep = playheadStep,
                    highContrast = highContrast,
                    interactive = interactive,
                    onToggle = onToggle,
                    onPaint = onPaint,
                    patternProvider = { patternState.value }
                )
                Spacer(modifier = Modifier.height(6.dp))
                PadLine(
                    row = row,
                    voice = voice,
                    line = 1,
                    cell = cell,
                    cellPx = cellPx,
                    padHeight = padHeight,
                    pattern = patternState.value,
                    locked = lockedState.value.getOrElse(row) { false },
                    threadProgress = threadProgress.getOrElse(row) { 0f },
                    playheadStep = playheadStep,
                    highContrast = highContrast,
                    interactive = interactive,
                    onToggle = onToggle,
                    onPaint = onPaint,
                    patternProvider = { patternState.value }
                )
                if (row != voices.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun StepNumberStrip(cell: Dp) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row {
            repeat(COLUMNS) { index ->
                Text(
                    text = stepLabel(index + 1),
                    style = MonoTiny,
                    color = if (index % 4 == 0) Accent else TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(cell)
                )
            }
        }
        Row {
            repeat(COLUMNS) { index ->
                Text(
                    text = stepLabel(index + 1 + COLUMNS),
                    style = MonoTiny,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(cell)
                )
            }
        }
    }
}

@Composable
private fun VoiceChip(voice: Voice, matched: Boolean) {
    val tint = voiceTint(voice)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(tint, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = voiceLabel(voice),
            style = MaterialTheme.typography.labelMedium,
            color = if (matched) Accent else tint
        )
    }
}

@Composable
private fun PadLine(
    row: Int,
    voice: Voice,
    line: Int,
    cell: Dp,
    cellPx: Float,
    padHeight: Dp,
    pattern: List<List<Boolean>>,
    locked: Boolean,
    threadProgress: Float,
    playheadStep: Int,
    highContrast: Boolean,
    interactive: Boolean,
    onToggle: (Int, Int) -> Unit,
    onPaint: (Int, Int, Boolean) -> Unit,
    patternProvider: () -> List<List<Boolean>>
) {
    val base = line * COLUMNS
    val enabled = interactive && !locked
    val threadVisible = threadProgress > 0f
    Box(
        modifier = Modifier
            .width(cell * COLUMNS)
            .height(padHeight)
            .pointerInput(enabled, row, line) {
                if (!enabled) return@pointerInput
                detectTapGestures { offset ->
                    val column = columnAt(offset.x, cellPx)
                    onToggle(row, base + column)
                }
            }
            .pointerInput(enabled, row, line) {
                if (!enabled) return@pointerInput
                var paintValue = true
                var lastColumn = -1
                detectDragGestures(
                    onDragStart = { offset ->
                        val column = columnAt(offset.x, cellPx)
                        lastColumn = column
                        val current = patternProvider()
                            .getOrNull(row)?.getOrNull(base + column) ?: false
                        paintValue = !current
                        onPaint(row, base + column, paintValue)
                    },
                    onDragEnd = { lastColumn = -1 },
                    onDragCancel = { lastColumn = -1 }
                ) { change, _ ->
                    val column = columnAt(change.position.x, cellPx)
                    if (column != lastColumn) {
                        lastColumn = column
                        onPaint(row, base + column, paintValue)
                    }
                }
            }
    ) {
        Row {
            repeat(COLUMNS) { column ->
                val step = base + column
                val on = pattern.getOrNull(row)?.getOrNull(step) ?: false
                Pad(
                    voice = voice,
                    step = step,
                    on = on,
                    locked = locked,
                    underPlayhead = playheadStep == step,
                    downbeat = step % 4 == 0,
                    highContrast = highContrast,
                    width = cell,
                    height = padHeight
                )
            }
        }
        if (threadVisible) {
            ThreadLine(
                progress = threadProgress,
                matched = true,
                modifier = Modifier
                    .width(cell * COLUMNS)
                    .height(padHeight)
            )
        }
        Canvas(
            modifier = Modifier
                .width(cell * COLUMNS)
                .height(padHeight)
        ) {
            if (playheadStep < base || playheadStep >= base + COLUMNS) return@Canvas
            val column = playheadStep - base
            val x = (column + 0.5f) * cellPx
            drawLine(
                color = Accent.copy(alpha = 0.9f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

@Composable
private fun Pad(
    voice: Voice,
    step: Int,
    on: Boolean,
    locked: Boolean,
    underPlayhead: Boolean,
    downbeat: Boolean,
    highContrast: Boolean,
    width: Dp,
    height: Dp
) {
    val tint = voiceTint(voice)
    val offColor = when {
        highContrast -> Color(0xFF101320)
        downbeat -> SurfaceAlt
        else -> SurfaceAlt.copy(alpha = 0.72f)
    }
    val targetTop = when {
        on && underPlayhead -> Color.White.copy(alpha = 0.78f)
        on && locked -> tint.copy(alpha = 0.55f)
        on -> PrimaryBright
        else -> offColor
    }
    val animatedTop by animateColorAsState(
        targetValue = targetTop,
        animationSpec = tween(GameConfig.PAD_DECAY_MS),
        label = "pad"
    )
    val bottom = when {
        on && locked -> tint.copy(alpha = 0.38f)
        on -> PrimaryDeep
        else -> offColor
    }
    val borderColor = when {
        on -> tint.copy(alpha = if (highContrast) 0.95f else 0.6f)
        downbeat -> Outline
        else -> Outline.copy(alpha = 0.6f)
    }
    val description = stringResource(R.string.cd_pad, voiceLabel(voice), step + 1)
    val state = stringResource(if (on) R.string.state_on else R.string.state_off)
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .padding(horizontal = 1.5.dp, vertical = 1.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (on) {
                    Brush.verticalGradient(listOf(animatedTop, bottom))
                } else {
                    SolidColor(animatedTop)
                }
            )
            .border(
                width = if (on) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .semantics {
                contentDescription = description
                stateDescription = state
            }
    )
}

private fun columnAt(x: Float, cellPx: Float): Int {
    if (cellPx <= 0f) return 0
    val raw = (x / cellPx).toInt()
    return raw.coerceIn(0, COLUMNS - 1)
}

private fun stepLabel(step: Int): String = if (step < 10) "0$step" else step.toString()
