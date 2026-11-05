package com.tempo.beatloom.data.repository

import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.data.local.BeatLoomPreferences
import com.tempo.beatloom.domain.model.PracticeStats
import com.tempo.beatloom.domain.model.SubmissionResult
import com.tempo.beatloom.domain.model.Voice
import com.tempo.beatloom.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val preferences: BeatLoomPreferences
) : ProgressRepository {

    override fun stats(): PracticeStats {
        val voiceAccuracy = Voice.entries.associateWith { voice ->
            val (hits, total) = preferences.voiceHits(voice.key)
            if (total == 0) 0 else (hits * 100) / total
        }
        val styleAccuracy = List(GameConfig.CHAPTERS) { index ->
            val (hits, total) = preferences.styleHits(index)
            if (total == 0) 0 else (hits * 100) / total
        }
        return PracticeStats(
            solvedTotal = preferences.solvedIds().size,
            totalPuzzles = GameConfig.TOTAL_PUZZLES,
            bestStreak = preferences.bestStreak(),
            earScore = preferences.earScore(),
            voiceAccuracy = voiceAccuracy,
            styleAccuracy = styleAccuracy,
            weeklySolves = preferences.weeklySolves()
        )
    }

    override fun solvedInChapter(chapterIndex: Int): Int {
        val first = chapterIndex * GameConfig.LOOPS_PER_CHAPTER
        val last = first + GameConfig.LOOPS_PER_CHAPTER
        return preferences.solvedIds().count { it in first until last }
    }

    override fun isSolved(puzzleId: Int): Boolean = preferences.solvedIds().contains(puzzleId)

    override fun nextUnsolvedPuzzleId(): Int {
        val solved = preferences.solvedIds()
        for (id in 0 until GameConfig.TOTAL_PUZZLES) {
            if (!solved.contains(id)) return id
        }
        return 0
    }

    override fun record(result: SubmissionResult): PracticeStats {
        result.voices.forEachIndexed { index, voice ->
            val matched = if (result.matchedRows.getOrElse(index) { false }) 1 else 0
            preferences.addVoiceHits(voice.key, matched, 1)
        }
        preferences.addStyleHits(result.chapterIndex, result.matchedCount, result.rowCount)
        preferences.setEarScore(preferences.earScore() + result.earScoreDelta)
        if (result.solved) {
            if (!preferences.solvedIds().contains(result.puzzleId)) {
                preferences.addSolved(result.puzzleId)
                preferences.addSolveToday()
            }
            val current = preferences.currentStreak() + 1
            preferences.setStreak(current, maxOf(current, preferences.bestStreak()))
        } else if (result.triesLeft == 0) {
            preferences.setStreak(0, preferences.bestStreak())
        }
        return stats()
    }

    override fun reset() {
        preferences.resetProgress()
    }
}
