package com.tempo.beatloom.data.repository

import com.tempo.beatloom.data.local.BeatLoomPreferences
import com.tempo.beatloom.domain.model.SavedBeat
import com.tempo.beatloom.domain.model.Voice
import com.tempo.beatloom.domain.repository.BeatRepository

class BeatRepositoryImpl(
    private val preferences: BeatLoomPreferences
) : BeatRepository {

    override fun beats(): List<SavedBeat> =
        preferences.beatRecords().mapNotNull(::decode).sortedByDescending { it.id }

    override fun save(beat: SavedBeat) {
        val records = preferences.beatRecords()
            .filterNot { it.substringBefore(FIELD) == beat.id.toString() }
            .toMutableList()
        records.add(encode(beat))
        preferences.writeBeatRecords(records)
    }

    override fun rename(id: Long, name: String) {
        val existing = beats().firstOrNull { it.id == id } ?: return
        save(existing.copy(name = sanitize(name)))
    }

    override fun delete(id: Long) {
        val records = preferences.beatRecords()
            .filterNot { it.substringBefore(FIELD) == id.toString() }
        preferences.writeBeatRecords(records)
    }

    private fun encode(beat: SavedBeat): String = listOf(
        beat.id.toString(),
        sanitize(beat.name),
        beat.bpm.toString(),
        beat.bars.toString(),
        beat.voices.joinToString(VOICE) { it.key },
        beat.pattern.joinToString(ROW) { row -> row.joinToString("") { if (it) "1" else "0" } }
    ).joinToString(FIELD)

    private fun decode(record: String): SavedBeat? {
        val parts = record.split(FIELD)
        if (parts.size < 6) return null
        val id = parts[0].toLongOrNull() ?: return null
        val voices = parts[4].split(VOICE).mapNotNull { Voice.fromKey(it) }
        if (voices.isEmpty()) return null
        val pattern = parts[5].split(ROW).map { row -> row.map { it == '1' } }
        if (pattern.size != voices.size) return null
        return SavedBeat(
            id = id,
            name = parts[1],
            bpm = parts[2].toIntOrNull() ?: 92,
            bars = parts[3].toIntOrNull() ?: 1,
            voices = voices,
            pattern = pattern
        )
    }

    private fun sanitize(raw: String): String =
        raw.replace(FIELD, " ").replace(ROW, " ").replace(VOICE, " ").trim()

    private companion object {
        const val FIELD = "|"
        const val ROW = ";"
        const val VOICE = ","
    }
}
