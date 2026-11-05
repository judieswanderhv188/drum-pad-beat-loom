package com.tempo.beatloom.presentation.app

import androidx.lifecycle.ViewModel
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.core.navigation.Screen
import com.tempo.beatloom.domain.model.SubmissionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) {
            backStack.addLast(current)
        }
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun finishRound(result: SubmissionResult) {
        _screen.value = Screen.GameOver(result)
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replay(result: SubmissionResult) {
        val next = if (result.solved) {
            (result.puzzleId + 1) % GameConfig.TOTAL_PUZZLES
        } else {
            result.puzzleId
        }
        _screen.value = Screen.Game(next)
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }
}
