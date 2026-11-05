package com.tempo.beatloom.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.beatloom.R
import com.tempo.beatloom.core.di.AppViewModelFactory
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.MonoLabel
import com.tempo.beatloom.core.theme.Primary
import com.tempo.beatloom.core.theme.PrimaryDeep
import com.tempo.beatloom.core.theme.Success
import com.tempo.beatloom.core.theme.TextMuted
import com.tempo.beatloom.core.theme.TextPrimary
import com.tempo.beatloom.core.ui.BentoTile
import com.tempo.beatloom.core.ui.PrimaryButton
import com.tempo.beatloom.core.ui.ScreenScaffold
import com.tempo.beatloom.core.ui.StatCardRow
import com.tempo.beatloom.core.ui.StatEntry

@Composable
fun MenuScreen(onPlay: (Int) -> Unit) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    MenuContent(state = state, onPlay = onPlay)
}

@Composable
private fun MenuContent(state: MenuUiState, onPlay: (Int) -> Unit) {
    val transition = rememberInfiniteTransition(label = "menu")
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    ScreenScaffold(
        overlay = listOf(Color(0xE616181F), Color(0xCC14161F), Color(0xF20E1017))
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.brand_main),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = stringResource(R.string.cd_studio_mark),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0x992C3044),
            border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.loops_solved,
                        state.solvedTotal,
                        state.totalPuzzles
                    ),
                    style = MonoLabel,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.menu_hint),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color(0xFFA08CFF).copy(alpha = 0.34f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Primary.copy(alpha = glow),
                    spotColor = Primary.copy(alpha = glow)
                )
                .background(
                    Brush.linearGradient(listOf(Primary, PrimaryDeep)),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = stringResource(R.string.cd_next_sprite),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.next_loop),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD9CFFF)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (state.ready) {
                            Text(
                                text = stringResource(
                                    R.string.loop_title,
                                    stringResource(state.nextStyleRes),
                                    state.nextLoopNumber
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                PrimaryButton(
                    text = stringResource(R.string.play),
                    onClick = { onPlay(state.nextPuzzleId) },
                    enabled = state.ready,
                    icon = Icons.Default.PlayArrow,
                    height = 56.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatCardRow(
            entries = state.stats.map { stat ->
                StatEntry(
                    label = stringResource(stat.labelRes),
                    value = stat.value,
                    accent = statAccent(stat.accentIndex)
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            state.chapters.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { chapter ->
                        BentoTile(
                            title = stringResource(chapter.nameRes),
                            subtitle = if (chapter.unlocked) {
                                stringResource(
                                    R.string.chapter_progress,
                                    chapter.solved,
                                    chapter.total
                                )
                            } else {
                                stringResource(R.string.chapter_locked)
                            },
                            progress = chapter.progress,
                            sprite = painterResource(chapterSprite(chapter.index)),
                            spriteDescription = stringResource(R.string.cd_chapter_sprite),
                            enabled = chapter.unlocked,
                            onClick = { onPlay(chapter.firstPuzzleId) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun statAccent(index: Int): Color = when (index) {
    0 -> Accent
    1 -> Primary
    else -> Success
}

private fun chapterSprite(index: Int): Int = when (index) {
    0 -> R.drawable.sprite_2
    1 -> R.drawable.sprite_3
    2 -> R.drawable.sprite_4
    3 -> R.drawable.sprite_5
    4 -> R.drawable.icon_home
    else -> R.drawable.logo
}
