package com.tempo.beatloom.domain.repository

import com.tempo.beatloom.domain.model.PracticeStats
import com.tempo.beatloom.domain.model.SubmissionResult

interface ProgressRepository {
    fun stats(): PracticeStats
    fun solvedInChapter(chapterIndex: Int): Int
    fun isSolved(puzzleId: Int): Boolean
    fun nextUnsolvedPuzzleId(): Int
    fun record(result: SubmissionResult): PracticeStats
    fun reset()
}
