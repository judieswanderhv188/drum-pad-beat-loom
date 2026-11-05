package com.tempo.beatloom.domain.model

data class SubmissionResult(
    val puzzleId: Int,
    val chapterIndex: Int,
    val voices: List<Voice>,
    val matchedRows: List<Boolean>,
    val solved: Boolean,
    val triesUsed: Int,
    val triesLeft: Int,
    val earScoreDelta: Int
) {
    val matchedCount: Int get() = matchedRows.count { it }
    val rowCount: Int get() = matchedRows.size
}
