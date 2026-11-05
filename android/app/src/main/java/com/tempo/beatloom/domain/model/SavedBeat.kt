package com.tempo.beatloom.domain.model

data class SavedBeat(
    val id: Long,
    val name: String,
    val bpm: Int,
    val bars: Int,
    val voices: List<Voice>,
    val pattern: List<List<Boolean>>
)
