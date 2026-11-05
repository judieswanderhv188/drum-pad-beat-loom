package com.tempo.beatloom.domain.model

data class Puzzle(
    val id: Int,
    val chapterIndex: Int,
    val indexInChapter: Int,
    val bars: Int,
    val bpm: Int,
    val voices: List<Voice>,
    val target: List<List<Boolean>>
)
