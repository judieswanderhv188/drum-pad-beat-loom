package com.tempo.beatloom.data.repository

import com.tempo.beatloom.data.local.BeatLoomPreferences
import com.tempo.beatloom.domain.model.AppSettings
import com.tempo.beatloom.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: BeatLoomPreferences
) : SettingsRepository {

    override fun settings(): AppSettings = AppSettings(
        countIn = preferences.countIn(),
        haptics = preferences.haptics(),
        gridLabels = preferences.gridLabels(),
        highContrastPads = preferences.highContrastPads(),
        masterVolume = preferences.masterVolume()
    )

    override fun save(settings: AppSettings) {
        preferences.saveSettings(
            countIn = settings.countIn,
            haptics = settings.haptics,
            gridLabels = settings.gridLabels,
            highContrast = settings.highContrastPads,
            volume = settings.masterVolume
        )
    }
}
