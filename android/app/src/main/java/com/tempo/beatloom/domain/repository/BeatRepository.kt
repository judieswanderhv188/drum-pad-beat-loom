package com.tempo.beatloom.domain.repository

import com.tempo.beatloom.domain.model.SavedBeat

interface BeatRepository {
    fun beats(): List<SavedBeat>
    fun save(beat: SavedBeat)
    fun rename(id: Long, name: String)
    fun delete(id: Long)
}
