package com.tempo.beatloom.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tempo.beatloom.R
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.domain.repository.ProgressRepository
import com.tempo.beatloom.domain.repository.PuzzleRepository
import com.tempo.beatloom.domain.usecase.UnlockChapterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val puzzleRepository: PuzzleRepository,
    private val progressRepository: ProgressRepository,
    private val unlockChapter: UnlockChapterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val stats = progressRepository.stats()
            val styles = puzzleRepository.styles()
            val nextId = progressRepository.nextUnsolvedPuzzleId()
            val next = puzzleRepository.puzzle(nextId)
            val chapters = styles.map { style ->
                ChapterUi(
                    index = style.index,
                    nameRes = style.nameRes,
                    solved = progressRepository.solvedInChapter(style.index),
                    total = style.loopCount,
                    unlocked = unlockChapter(style.index),
                    firstPuzzleId = style.index * GameConfig.LOOPS_PER_CHAPTER
                )
            }
            val tiles = buildList {
                if (stats.earScore > 0) {
                    add(MenuStat(R.string.stat_ear_score, stats.earScore.toString(), 0))
                }
                if (stats.bestStreak > 0) {
                    add(MenuStat(R.string.stat_best_streak, stats.bestStreak.toString(), 1))
                }
                if (stats.solvedTotal > 0) {
                    add(MenuStat(R.string.stat_solved, stats.solvedTotal.toString(), 2))
                }
                if (size < 2) {
                    add(MenuStat(R.string.stat_total_loops, stats.totalPuzzles.toString(), 0))
                    add(MenuStat(R.string.stat_chapters, GameConfig.CHAPTERS.toString(), 1))
                }
            }.take(3)
            _state.update {
                it.copy(
                    ready = true,
                    solvedTotal = stats.solvedTotal,
                    totalPuzzles = stats.totalPuzzles,
                    nextPuzzleId = nextId,
                    nextStyleRes = styles[next.chapterIndex].nameRes,
                    nextLoopNumber = next.indexInChapter + 1,
                    stats = tiles,
                    chapters = chapters
                )
            }
        }
    }
}
