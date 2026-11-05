package com.tempo.beatloom.domain.repository

import com.tempo.beatloom.domain.model.AppSettings

interface SettingsRepository {
    fun settings(): AppSettings
    fun save(settings: AppSettings)
}
