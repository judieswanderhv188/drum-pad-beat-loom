package com.tempo.beatloom.domain.usecase

import com.tempo.beatloom.domain.repository.ProgressRepository

class BuildWeeklyStatsUseCase(
    private val progressRepository: ProgressRepository
) {

    operator fun invoke(): List<Int> {
        val raw = progressRepository.stats().weeklySolves
        return List(DAYS) { raw.getOrElse(it) { 0 } }
    }

    private companion object {
        const val DAYS = 7
    }
}
