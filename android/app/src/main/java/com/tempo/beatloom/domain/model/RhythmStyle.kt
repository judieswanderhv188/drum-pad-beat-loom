package com.tempo.beatloom.domain.model

data class RhythmStyle(
    val index: Int,
    val key: String,
    val nameRes: Int,
    val loopCount: Int,
    val baseBpm: Int,
    val voices: List<Voice>
)
