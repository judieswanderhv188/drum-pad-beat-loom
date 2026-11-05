package com.tempo.beatloom.data.repository

import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.data.sample.SampleData
import com.tempo.beatloom.domain.model.Puzzle
import com.tempo.beatloom.domain.model.RhythmLesson
import com.tempo.beatloom.domain.model.RhythmStyle
import com.tempo.beatloom.domain.repository.PuzzleRepository

class PuzzleRepositoryImpl : PuzzleRepository {

    private val cache = HashMap<Int, Puzzle>()

    override fun styles(): List<RhythmStyle> = SampleData.styles

    override fun puzzle(id: Int): Puzzle =
        cache.getOrPut(id.coerceIn(0, GameConfig.TOTAL_PUZZLES - 1)) { SampleData.puzzle(id) }

    override fun nextPuzzleId(currentId: Int): Int =
        (currentId + 1) % GameConfig.TOTAL_PUZZLES

    override fun lessons(): List<RhythmLesson> = SampleData.lessons

    override fun totalPuzzles(): Int = GameConfig.TOTAL_PUZZLES
}
