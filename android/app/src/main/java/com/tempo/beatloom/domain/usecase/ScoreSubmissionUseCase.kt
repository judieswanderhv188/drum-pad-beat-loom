package com.tempo.beatloom.domain.usecase

import com.tempo.beatloom.domain.model.Puzzle
import com.tempo.beatloom.domain.model.SubmissionResult

class ScoreSubmissionUseCase {

    operator fun invoke(
        puzzle: Puzzle,
        pattern: List<List<Boolean>>,
        lockedRows: List<Boolean>,
        triesUsed: Int,
        maxTries: Int
    ): SubmissionResult {
        val matched = puzzle.target.mapIndexed { row, target ->
            lockedRows.getOrElse(row) { false } || pattern.getOrNull(row) == target
        }
        val solved = matched.all { it }
        val triesLeft = (maxTries - triesUsed).coerceAtLeast(0)
        val reward = when {
            !solved -> 0
            triesUsed <= 1 -> 100
            triesUsed == 2 -> 70
            else -> 40
        }
        val partial = matched.count { it } * 4
        return SubmissionResult(
            puzzleId = puzzle.id,
            chapterIndex = puzzle.chapterIndex,
            voices = puzzle.voices,
            matchedRows = matched,
            solved = solved,
            triesUsed = triesUsed,
            triesLeft = triesLeft,
            earScoreDelta = (reward / 5) + partial
        )
    }
}
