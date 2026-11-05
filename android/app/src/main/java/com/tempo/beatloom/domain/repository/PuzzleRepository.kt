package com.tempo.beatloom.domain.repository

import com.tempo.beatloom.domain.model.Puzzle
import com.tempo.beatloom.domain.model.RhythmLesson
import com.tempo.beatloom.domain.model.RhythmStyle

interface PuzzleRepository {
    fun styles(): List<RhythmStyle>
    fun puzzle(id: Int): Puzzle
    fun nextPuzzleId(currentId: Int): Int
    fun lessons(): List<RhythmLesson>
    fun totalPuzzles(): Int
}
