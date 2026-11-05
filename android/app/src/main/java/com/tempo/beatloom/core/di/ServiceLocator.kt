package com.tempo.beatloom.core.di

import android.content.Context
import com.tempo.beatloom.data.audio.PercussionSynth
import com.tempo.beatloom.data.local.BeatLoomPreferences
import com.tempo.beatloom.data.repository.BeatRepositoryImpl
import com.tempo.beatloom.data.repository.ProgressRepositoryImpl
import com.tempo.beatloom.data.repository.PuzzleRepositoryImpl
import com.tempo.beatloom.data.repository.SettingsRepositoryImpl
import com.tempo.beatloom.domain.repository.BeatRepository
import com.tempo.beatloom.domain.repository.ProgressRepository
import com.tempo.beatloom.domain.repository.PuzzleRepository
import com.tempo.beatloom.domain.repository.SettingsRepository
import com.tempo.beatloom.domain.usecase.BuildWeeklyStatsUseCase
import com.tempo.beatloom.domain.usecase.ScoreSubmissionUseCase
import com.tempo.beatloom.domain.usecase.UnlockChapterUseCase

object ServiceLocator {

    private var puzzles: PuzzleRepository? = null
    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var beats: BeatRepository? = null
    private var synth: PercussionSynth? = null

    fun init(context: Context) {
        if (puzzles != null) return
        val preferences = BeatLoomPreferences(context.applicationContext)
        puzzles = PuzzleRepositoryImpl()
        progress = ProgressRepositoryImpl(preferences)
        settings = SettingsRepositoryImpl(preferences)
        beats = BeatRepositoryImpl(preferences)
        synth = PercussionSynth()
    }

    val puzzleRepository: PuzzleRepository get() = checkNotNull(puzzles)

    val progressRepository: ProgressRepository get() = checkNotNull(progress)

    val settingsRepository: SettingsRepository get() = checkNotNull(settings)

    val beatRepository: BeatRepository get() = checkNotNull(beats)

    val percussionSynth: PercussionSynth get() = checkNotNull(synth)

    val scoreSubmission: ScoreSubmissionUseCase = ScoreSubmissionUseCase()

    val unlockChapter: UnlockChapterUseCase
        get() = UnlockChapterUseCase(puzzleRepository, progressRepository)

    val buildWeeklyStats: BuildWeeklyStatsUseCase
        get() = BuildWeeklyStatsUseCase(progressRepository)
}
