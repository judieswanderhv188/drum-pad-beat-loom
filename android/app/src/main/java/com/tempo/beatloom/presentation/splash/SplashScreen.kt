package com.tempo.beatloom.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.beatloom.R
import com.tempo.beatloom.core.di.AppViewModelFactory
import com.tempo.beatloom.core.theme.Accent
import com.tempo.beatloom.core.theme.BgDeep
import com.tempo.beatloom.core.theme.Outline
import com.tempo.beatloom.core.theme.Primary
import com.tempo.beatloom.core.theme.TextMuted
import com.tempo.beatloom.core.theme.TextPrimary
import com.tempo.beatloom.core.theme.TextSecondary

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.finished) {
        if (state.finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val ringScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )
    val ghostStep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2080, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ghost"
    )
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    val heroState = remember { MutableTransitionState(false).apply { targetState = true } }
    val titleState = remember { MutableTransitionState(false).apply { targetState = true } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF05060B), Color(0xFF14161F), Color(0xFF0B0C14))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x5C7B5CFF), Color(0x00000000)),
                        radius = 620f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visibleState = heroState,
                enter = scaleIn(initialScale = 0.82f, animationSpec = tween(620, easing = FastOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(520))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(184.dp)
                            .scale(ringScale)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .border(
                                width = 1.5.dp,
                                color = Primary.copy(alpha = ringAlpha.coerceIn(0f, 1f)),
                                shape = CircleShape
                            )
                    )
                    Surface(
                        modifier = Modifier.size(184.dp),
                        shape = CircleShape,
                        color = Color(0x142C3044),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.18f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.logo),
                                contentDescription = stringResource(R.string.cd_logo),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(148.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            AnimatedVisibility(
                visibleState = titleState,
                enter = fadeIn(animationSpec = tween(520, delayMillis = 180)) +
                    slideInVertically(animationSpec = tween(520, delayMillis = 180)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.brand_top),
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .width(260.dp)
                                .height(44.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Accent.copy(alpha = 0.35f * shimmer),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Text(
                            text = stringResource(R.string.brand_main),
                            style = MaterialTheme.typography.displaySmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.splash_subtitle),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            GhostRow(activeIndex = ghostStep.toInt().coerceIn(0, 7))

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                color = Accent,
                trackColor = Outline.copy(alpha = 0.4f),
                modifier = Modifier
                    .width(168.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun GhostRow(activeIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(8) { index ->
            val active = index == activeIndex
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (active) Accent else Outline.copy(alpha = 0.5f)
                    )
            )
        }
    }
}
