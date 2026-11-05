package com.tempo.beatloom.data.local

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

class BeatLoomPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun solvedIds(): Set<Int> =
        prefs.getStringSet(KEY_SOLVED, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun addSolved(id: Int) {
        val updated = solvedIds().map { it.toString() }.toMutableSet()
        updated.add(id.toString())
        prefs.edit().putStringSet(KEY_SOLVED, updated).apply()
    }

    fun bestStreak(): Int = prefs.getInt(KEY_BEST_STREAK, 0)

    fun currentStreak(): Int = prefs.getInt(KEY_CURRENT_STREAK, 0)

    fun setStreak(current: Int, best: Int) {
        prefs.edit()
            .putInt(KEY_CURRENT_STREAK, current)
            .putInt(KEY_BEST_STREAK, best)
            .apply()
    }

    fun earScore(): Int = prefs.getInt(KEY_EAR_SCORE, 0)

    fun setEarScore(value: Int) {
        prefs.edit().putInt(KEY_EAR_SCORE, value).apply()
    }

    fun voiceHits(key: String): Pair<Int, Int> = Pair(
        prefs.getInt(PREFIX_VOICE_HIT + key, 0),
        prefs.getInt(PREFIX_VOICE_TOTAL + key, 0)
    )

    fun addVoiceHits(key: String, hits: Int, total: Int) {
        val current = voiceHits(key)
        prefs.edit()
            .putInt(PREFIX_VOICE_HIT + key, current.first + hits)
            .putInt(PREFIX_VOICE_TOTAL + key, current.second + total)
            .apply()
    }

    fun styleHits(index: Int): Pair<Int, Int> = Pair(
        prefs.getInt(PREFIX_STYLE_HIT + index, 0),
        prefs.getInt(PREFIX_STYLE_TOTAL + index, 0)
    )

    fun addStyleHits(index: Int, hits: Int, total: Int) {
        val current = styleHits(index)
        prefs.edit()
            .putInt(PREFIX_STYLE_HIT + index, current.first + hits)
            .putInt(PREFIX_STYLE_TOTAL + index, current.second + total)
            .apply()
    }

    fun weeklySolves(): List<Int> {
        val stored = prefs.getString(KEY_WEEK_COUNTS, null)
            ?.split(SEPARATOR_VALUE)
            ?.mapNotNull { it.toIntOrNull() }
            .orEmpty()
        val counts = MutableList(WEEK_DAYS) { stored.getOrElse(it) { 0 } }
        val base = prefs.getLong(KEY_WEEK_BASE, 0L)
        val today = LocalDate.now().toEpochDay()
        val shift = (today - base).toInt()
        if (base == 0L || shift >= WEEK_DAYS || shift < 0) {
            return List(WEEK_DAYS) { 0 }
        }
        if (shift == 0) return counts
        val shifted = MutableList(WEEK_DAYS) { 0 }
        for (index in shift until WEEK_DAYS) {
            shifted[index - shift] = counts[index]
        }
        return shifted
    }

    fun addSolveToday() {
        val counts = weeklySolves().toMutableList()
        counts[WEEK_DAYS - 1] = counts[WEEK_DAYS - 1] + 1
        prefs.edit()
            .putLong(KEY_WEEK_BASE, LocalDate.now().toEpochDay())
            .putString(KEY_WEEK_COUNTS, counts.joinToString(SEPARATOR_VALUE))
            .apply()
    }

    fun countIn(): Boolean = prefs.getBoolean(KEY_COUNT_IN, true)

    fun haptics(): Boolean = prefs.getBoolean(KEY_HAPTICS, true)

    fun gridLabels(): Boolean = prefs.getBoolean(KEY_GRID_LABELS, true)

    fun highContrastPads(): Boolean = prefs.getBoolean(KEY_HIGH_CONTRAST, false)

    fun masterVolume(): Float = prefs.getFloat(KEY_VOLUME, 0.85f)

    fun saveSettings(
        countIn: Boolean,
        haptics: Boolean,
        gridLabels: Boolean,
        highContrast: Boolean,
        volume: Float
    ) {
        prefs.edit()
            .putBoolean(KEY_COUNT_IN, countIn)
            .putBoolean(KEY_HAPTICS, haptics)
            .putBoolean(KEY_GRID_LABELS, gridLabels)
            .putBoolean(KEY_HIGH_CONTRAST, highContrast)
            .putFloat(KEY_VOLUME, volume)
            .apply()
    }

    fun beatRecords(): List<String> =
        prefs.getStringSet(KEY_BEATS, emptySet()).orEmpty().toList()

    fun writeBeatRecords(records: Collection<String>) {
        prefs.edit().putStringSet(KEY_BEATS, records.toSet()).apply()
    }

    fun resetProgress() {
        val editor = prefs.edit()
        editor.remove(KEY_SOLVED)
        editor.remove(KEY_BEST_STREAK)
        editor.remove(KEY_CURRENT_STREAK)
        editor.remove(KEY_EAR_SCORE)
        editor.remove(KEY_WEEK_BASE)
        editor.remove(KEY_WEEK_COUNTS)
        for (key in prefs.all.keys) {
            if (key.startsWith(PREFIX_VOICE_HIT) || key.startsWith(PREFIX_VOICE_TOTAL) ||
                key.startsWith(PREFIX_STYLE_HIT) || key.startsWith(PREFIX_STYLE_TOTAL)
            ) {
                editor.remove(key)
            }
        }
        editor.apply()
    }

    private companion object {
        const val FILE_NAME = "beat_loom_prefs"
        const val KEY_SOLVED = "solved_ids"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_CURRENT_STREAK = "current_streak"
        const val KEY_EAR_SCORE = "ear_score"
        const val KEY_WEEK_BASE = "week_base"
        const val KEY_WEEK_COUNTS = "week_counts"
        const val KEY_COUNT_IN = "count_in"
        const val KEY_HAPTICS = "haptics"
        const val KEY_GRID_LABELS = "grid_labels"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_VOLUME = "master_volume"
        const val KEY_BEATS = "saved_beats"
        const val PREFIX_VOICE_HIT = "voice_hit_"
        const val PREFIX_VOICE_TOTAL = "voice_total_"
        const val PREFIX_STYLE_HIT = "style_hit_"
        const val PREFIX_STYLE_TOTAL = "style_total_"
        const val SEPARATOR_VALUE = ","
        const val WEEK_DAYS = 7
    }
}
