package com.tempo.beatloom.presentation.game

import com.tempo.beatloom.domain.model.Voice

data class GameUiState(
    val ready: Boolean = false,
    val styleRes: Int = 0,
    val loopNumber: Int = 1,
    val bpm: Int = 0,
    val voices: List<Voice> = emptyList(),
    val pattern: List<List<Boolean>> = emptyList(),
    val lockedRows: List<Boolean> = emptyList(),
    val threadProgress: List<Float> = emptyList(),
    val playheadStep: Int = -1,
    val playingTarget: Boolean = false,
    val playingMine: Boolean = false,
    val triesLeft: Int = 0,
    val labelsVisible: Boolean = true,
    val highContrast: Boolean = false
) {
    val playing: Boolean get() = playingTarget || playingMine
}
