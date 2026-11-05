package com.tempo.beatloom.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.beatloom.R
import com.tempo.beatloom.core.di.AppViewModelFactory
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.Outline
import com.tempo.beatloom.core.theme.Primary
import com.tempo.beatloom.core.theme.SurfaceCard
import com.tempo.beatloom.core.theme.Success
import com.tempo.beatloom.core.theme.TextMuted
import com.tempo.beatloom.core.theme.TextPrimary
import com.tempo.beatloom.core.theme.TextSecondary
import com.tempo.beatloom.core.ui.PrimaryButton
import com.tempo.beatloom.core.ui.ScreenScaffold
import com.tempo.beatloom.core.ui.SecondaryButton
import com.tempo.beatloom.core.ui.StatCardRow
import com.tempo.beatloom.core.ui.StatEntry
import com.tempo.beatloom.core.ui.ThreadLine
import com.tempo.beatloom.core.ui.voiceLabel
import com.tempo.beatloom.core.ui.voiceTint
import com.tempo.beatloom.domain.model.SubmissionResult

@Composable
fun GameOverScreen(
    result: SubmissionResult,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    GameOverContent(
        result = result,
        state = state,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    result: SubmissionResult,
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val heroState = remember { MutableTransitionState(false).apply { targetState = true } }
    val triesLabel = stringResource(R.string.result_tries)
    val rowsLabel = stringResource(R.string.stat_solved)
    val earLabel = stringResource(R.string.stat_ear_score)
    val entries = listOfNotNull(
        StatEntry(triesLabel, result.triesUsed.toString(), Primary),
        if (result.matchedCount > 0) {
            StatEntry(
                rowsLabel,
                result.matchedCount.toString() + "/" + result.rowCount,
                Accent
            )
        } else {
            null
        },
        if (state.earScore > 0) {
            StatEntry(earLabel, state.earScore.toString(), Success)
        } else {
            null
        }
    )

    ScreenScaffold(
        overlay = listOf(Color(0xF30B0D14), Color(0xE51A1D28), Color(0xF5090A10))
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visibleState = heroState,
            enter = scaleIn(initialScale = 0.88f, animationSpec = tween(520)) +
                fadeIn(animationSpec = tween(420))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(
                            if (result.solved) R.string.result_solved else R.string.result_missed
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        color = if (result.solved) Accent else TextPrimary,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.result_rows,
                            result.matchedCount,
                            result.rowCount
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        StatCardRow(entries = entries)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.72f)),
            border = BorderStroke(1.dp, Outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                result.voices.forEachIndexed { index, voice ->
                    val matched = result.matchedRows.getOrElse(index) { false }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.width(74.dp)
                        ) {
                            Text(
                                text = voiceLabel(voice),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (matched) Accent else voiceTint(voice)
                            )
                        }
                        ThreadLine(
                            progress = if (matched) 1f else 0f,
                            matched = matched,
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.result_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = stringResource(R.string.play_again),
            onClick = onPlayAgain,
            icon = Icons.Default.PlayArrow,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryButton(
            text = stringResource(R.string.menu),
            onClick = onMenu,
            icon = Icons.Default.Home,
            height = 48.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
