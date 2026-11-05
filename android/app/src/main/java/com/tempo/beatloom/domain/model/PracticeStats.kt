package com.tempo.beatloom.domain.model

data class PracticeStats(
    val solvedTotal: Int,
    val totalPuzzles: Int,
    val bestStreak: Int,
    val earScore: Int,
    val voiceAccuracy: Map<Voice, Int>,
    val styleAccuracy: List<Int>,
    val weeklySolves: List<Int>
)
