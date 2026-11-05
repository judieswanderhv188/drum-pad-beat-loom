package com.tempo.beatloom.core.navigation

import com.tempo.beatloom.domain.model.SubmissionResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val puzzleId: Int) : Screen
    data class GameOver(val result: SubmissionResult) : Screen
}
