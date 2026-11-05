package com.tempo.beatloom.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.core.di.AppViewModelFactory
import com.tempo.beatloom.core.navigation.Screen
import com.tempo.beatloom.presentation.game.GameScreen
import com.tempo.beatloom.presentation.gameover.GameOverScreen
import com.tempo.beatloom.presentation.menu.MenuScreen
import com.tempo.beatloom.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(animationSpec = tween(GameConfig.SCREEN_FADE_IN_MS)) togetherWith
                fadeOut(animationSpec = tween(GameConfig.SCREEN_FADE_OUT_MS))
        },
        label = "screen"
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(onFinished = { appViewModel.openMenu() })
            Screen.Menu -> MenuScreen(onPlay = { puzzleId -> appViewModel.navigate(Screen.Game(puzzleId)) })
            is Screen.Game -> GameScreen(
                puzzleId = target.puzzleId,
                onBack = { appViewModel.back() },
                onFinished = { result -> appViewModel.finishRound(result) }
            )
            is Screen.GameOver -> GameOverScreen(
                result = target.result,
                onPlayAgain = { appViewModel.replay(target.result) },
                onMenu = { appViewModel.openMenu() }
            )
        }
    }
}
