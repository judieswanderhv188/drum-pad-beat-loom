package com.tempo.beatloom.domain.usecase

import com.tempo.beatloom.domain.repository.ProgressRepository
import com.tempo.beatloom.domain.repository.PuzzleRepository

class UnlockChapterUseCase(
    private val puzzleRepository: PuzzleRepository,
    private val progressRepository: ProgressRepository
) {

    operator fun invoke(chapterIndex: Int): Boolean {
        if (chapterIndex <= 0) return true
        val previous = puzzleRepository.styles().getOrNull(chapterIndex - 1) ?: return false
        val solved = progressRepository.solvedInChapter(chapterIndex - 1)
        return solved * 100 >= previous.loopCount * UNLOCK_PERCENT
    }

    private companion object {
        const val UNLOCK_PERCENT = 60
    }
}
