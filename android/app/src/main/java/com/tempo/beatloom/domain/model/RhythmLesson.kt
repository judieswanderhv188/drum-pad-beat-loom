package com.tempo.beatloom.domain.model

data class RhythmLesson(
    val key: String,
    val titleRes: Int,
    val bodyRes: Int,
    val pattern: List<Boolean>
)
