package com.tempo.beatloom.data.sample

import com.tempo.beatloom.R
import com.tempo.beatloom.core.config.GameConfig
import com.tempo.beatloom.domain.model.Puzzle
import com.tempo.beatloom.domain.model.RhythmLesson
import com.tempo.beatloom.domain.model.RhythmStyle
import com.tempo.beatloom.domain.model.Voice

object SampleData {

    val styles: List<RhythmStyle> = listOf(
        RhythmStyle(
            index = 0,
            key = "boom_bap",
            nameRes = R.string.style_boom_bap,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 88,
            voices = listOf(Voice.KICK, Voice.SNARE, Voice.HAT)
        ),
        RhythmStyle(
            index = 1,
            key = "house_four",
            nameRes = R.string.style_house_four,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 124,
            voices = listOf(Voice.KICK, Voice.CLAP, Voice.HAT, Voice.RIM)
        ),
        RhythmStyle(
            index = 2,
            key = "afro_swing",
            nameRes = R.string.style_afro_swing,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 104,
            voices = listOf(Voice.KICK, Voice.RIM, Voice.TOM, Voice.CLAP)
        ),
        RhythmStyle(
            index = 3,
            key = "trap_hats",
            nameRes = R.string.style_trap_hats,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 72,
            voices = listOf(Voice.KICK, Voice.SNARE, Voice.HAT)
        ),
        RhythmStyle(
            index = 4,
            key = "latin_clave",
            nameRes = R.string.style_latin_clave,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 96,
            voices = listOf(Voice.RIM, Voice.TOM, Voice.CLAP, Voice.HAT)
        ),
        RhythmStyle(
            index = 5,
            key = "drum_and_bass",
            nameRes = R.string.style_drum_and_bass,
            loopCount = GameConfig.LOOPS_PER_CHAPTER,
            baseBpm = 168,
            voices = listOf(Voice.KICK, Voice.SNARE, Voice.HAT, Voice.TOM)
        )
    )

    val lessons: List<RhythmLesson> = listOf(
        RhythmLesson(
            key = "subdivisions",
            titleRes = R.string.lesson_subdivisions_title,
            bodyRes = R.string.lesson_subdivisions_body,
            pattern = stepsOf(0, 2, 4, 6)
        ),
        RhythmLesson(
            key = "swing",
            titleRes = R.string.lesson_swing_title,
            bodyRes = R.string.lesson_swing_body,
            pattern = stepsOf(0, 3, 4, 7)
        ),
        RhythmLesson(
            key = "syncopation",
            titleRes = R.string.lesson_syncopation_title,
            bodyRes = R.string.lesson_syncopation_body,
            pattern = stepsOf(1, 3, 6, 7)
        )
    )

    fun puzzle(id: Int): Puzzle {
        val safeId = id.coerceIn(0, GameConfig.TOTAL_PUZZLES - 1)
        val chapter = safeId / GameConfig.LOOPS_PER_CHAPTER
        val indexInChapter = safeId % GameConfig.LOOPS_PER_CHAPTER
        val style = styles[chapter]
        val voices = voicesFor(style, indexInChapter)
        val bpm = (style.baseBpm + (indexInChapter % 5) * 4)
            .coerceIn(GameConfig.MIN_BPM, GameConfig.MAX_BPM)
        val target = voices.mapIndexed { row, voice ->
            buildRow(voice, chapter, safeId, row, indexInChapter)
        }
        return Puzzle(
            id = safeId,
            chapterIndex = chapter,
            indexInChapter = indexInChapter,
            bars = 1,
            bpm = bpm,
            voices = voices,
            target = target
        )
    }

    private fun voicesFor(style: RhythmStyle, indexInChapter: Int): List<Voice> = when {
        indexInChapter < 3 -> style.voices.take(1)
        indexInChapter < 8 -> style.voices.take(2)
        indexInChapter < 16 -> style.voices.take(minOf(3, style.voices.size))
        else -> style.voices
    }

    private fun buildRow(
        voice: Voice,
        chapter: Int,
        puzzleId: Int,
        row: Int,
        indexInChapter: Int
    ): List<Boolean> {
        val base = baseTemplate(voice, chapter).toMutableList()
        val flips = 1 + indexInChapter / 9
        var seed = (puzzleId * 1103515245L + 12345L + row * 7919L) and 0x7FFFFFFFL
        repeat(flips) {
            seed = (seed * 1103515245L + 12345L) and 0x7FFFFFFFL
            val position = ((seed shr 7) % GameConfig.STEPS_PER_BAR).toInt()
            if (position != 0 || voice != Voice.KICK) {
                base[position] = !base[position]
            }
        }
        if (base.none { it }) {
            base[0] = true
        }
        return base.toList()
    }

    private fun baseTemplate(voice: Voice, chapter: Int): List<Boolean> = when (voice) {
        Voice.KICK -> when (chapter) {
            1 -> stepsOf(0, 4, 8, 12)
            3 -> stepsOf(0, 6, 10)
            5 -> stepsOf(0, 10)
            else -> stepsOf(0, 8)
        }
        Voice.SNARE -> when (chapter) {
            5 -> stepsOf(4, 11, 14)
            else -> stepsOf(4, 12)
        }
        Voice.HAT -> when (chapter) {
            1 -> stepsOf(2, 6, 10, 14)
            3 -> stepsOf(0, 2, 3, 4, 6, 8, 10, 12, 14)
            else -> stepsOf(0, 2, 4, 6, 8, 10, 12, 14)
        }
        Voice.CLAP -> when (chapter) {
            2 -> stepsOf(3, 10)
            else -> stepsOf(4, 12)
        }
        Voice.TOM -> when (chapter) {
            4 -> stepsOf(6, 13)
            else -> stepsOf(7, 14)
        }
        Voice.RIM -> when (chapter) {
            4 -> stepsOf(0, 3, 6, 10, 12)
            else -> stepsOf(3, 10)
        }
    }

    private fun stepsOf(vararg on: Int): List<Boolean> {
        val set = on.toHashSet()
        return List(GameConfig.STEPS_PER_BAR) { set.contains(it) }
    }
}
