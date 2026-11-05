package com.tempo.beatloom.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tempo.beatloom.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameOverUiState(
    val earScore: Int = 0,
    val solvedTotal: Int = 0,
    val bestStreak: Int = 0
)

class GameOverViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val stats = progressRepository.stats()
            _state.update {
                it.copy(
                    earScore = stats.earScore,
                    solvedTotal = stats.solvedTotal,
                    bestStreak = stats.bestStreak
                )
            }
        }
    }
}
