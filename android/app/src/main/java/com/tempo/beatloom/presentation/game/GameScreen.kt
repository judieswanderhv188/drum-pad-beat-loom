package com.tempo.beatloom.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.beatloom.R
import com.tempo.beatloom.core.di.AppViewModelFactory
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.AccentInk
import com.tempo.beatloom.core.theme.MonoLabel
import com.tempo.beatloom.core.theme.Primary
import com.tempo.beatloom.core.theme.PrimaryInk
import com.tempo.beatloom.core.theme.SurfaceCard
import com.tempo.beatloom.core.theme.TextMuted
import com.tempo.beatloom.core.theme.TextPrimary
import com.tempo.beatloom.core.theme.TextSecondary
import com.tempo.beatloom.core.ui.PrimaryButton
import com.tempo.beatloom.core.ui.ScreenScaffold
import com.tempo.beatloom.core.ui.SecondaryButton
import com.tempo.beatloom.core.ui.StepGrid
import com.tempo.beatloom.domain.model.SubmissionResult

@Composable
fun GameScreen(
    puzzleId: Int,
    onBack: () -> Unit,
    onFinished: (SubmissionResult) -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(puzzleId) {
        viewModel.start(puzzleId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { result -> onFinished(result) }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stop() }
    }

    GameContent(
        state = state,
        onBack = onBack,
        onToggle = viewModel::toggle,
        onPaint = viewModel::paint,
        onListen = viewModel::playTarget,
        onPlayMine = viewModel::playMine,
        onStop = viewModel::stop,
        onSubmit = viewModel::submit,
        onEndRound = viewModel::endRound
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onToggle: (Int, Int) -> Unit,
    onPaint: (Int, Int, Boolean) -> Unit,
    onListen: () -> Unit,
    onPlayMine: () -> Unit,
    onStop: () -> Unit,
    onSubmit: () -> Unit,
    onEndRound: () -> Unit
) {
    ScreenScaffold(
        overlay = listOf(Color(0xF014161F), Color(0xD91A1D28), Color(0xF50C0E15))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.game_back),
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ComparePill(state = state, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.game_tries, state.triesLeft),
                style = MonoLabel,
                color = if (state.triesLeft <= 1) Accent else TextSecondary,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }

        if (state.ready) {
            Text(
                text = stringResource(
                    R.string.loop_title,
                    stringResource(state.styleRes),
                    state.loopNumber
                ),
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.game_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.ready) {
                StepGrid(
                    voices = state.voices,
                    pattern = state.pattern,
                    lockedRows = state.lockedRows,
                    threadProgress = state.threadProgress,
                    playheadStep = state.playheadStep,
                    labelsVisible = state.labelsVisible,
                    highContrast = state.highContrast,
                    interactive = true,
                    onToggle = onToggle,
                    onPaint = onPaint
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton(
                text = stringResource(R.string.listen),
                onClick = onListen,
                enabled = state.ready,
                icon = Icons.Default.PlayArrow,
                height = 48.dp,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.yours),
                onClick = onPlayMine,
                enabled = state.ready,
                icon = Icons.Default.Refresh,
                height = 48.dp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onStop,
                enabled = state.playing,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pause),
                    contentDescription = stringResource(R.string.cd_stop),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryButton(
            text = stringResource(R.string.submit),
            onClick = onSubmit,
            enabled = state.ready,
            icon = Icons.Default.Check,
            height = 56.dp,
            violet = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SecondaryButton(
            text = stringResource(R.string.end_round),
            onClick = onEndRound,
            enabled = state.ready,
            height = 48.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ComparePill(state: GameUiState, modifier: Modifier = Modifier) {
    val targetActive = state.playingTarget
    val mineActive = state.playingMine
    val description = stringResource(
        if (targetActive) R.string.game_target else R.string.game_yours
    )
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.18f)),
        modifier = modifier
            .height(36.dp)
            .semantics {
                stateDescription = description
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PillCell(
                text = stringResource(R.string.game_target),
                active = targetActive,
                activeColor = Accent,
                activeBackground = AccentInk,
                modifier = Modifier.weight(1f)
            )
            PillCell(
                text = stringResource(R.string.game_yours),
                active = mineActive,
                activeColor = Primary,
                activeBackground = PrimaryInk,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PillCell(
    text: String,
    active: Boolean,
    activeColor: Color,
    activeBackground: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(if (active) activeBackground else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (active) activeColor else TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
