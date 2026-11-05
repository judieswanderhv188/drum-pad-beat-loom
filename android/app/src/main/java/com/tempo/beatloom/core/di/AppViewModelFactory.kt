package com.tempo.beatloom.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tempo.beatloom.presentation.app.AppViewModel
import com.tempo.beatloom.presentation.game.GameViewModel
import com.tempo.beatloom.presentation.gameover.GameOverViewModel
import com.tempo.beatloom.presentation.menu.MenuViewModel
import com.tempo.beatloom.presentation.splash.SplashViewModel

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel() as T
        modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel() as T
        modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
            ServiceLocator.puzzleRepository,
            ServiceLocator.progressRepository,
            ServiceLocator.unlockChapter
        ) as T
        modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
            ServiceLocator.puzzleRepository,
            ServiceLocator.progressRepository,
            ServiceLocator.settingsRepository,
            ServiceLocator.scoreSubmission,
            ServiceLocator.percussionSynth
        ) as T
        modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
            ServiceLocator.progressRepository
        ) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
    }
}
