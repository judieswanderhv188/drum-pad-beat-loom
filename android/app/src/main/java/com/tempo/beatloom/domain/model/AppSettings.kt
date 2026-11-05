package com.tempo.beatloom.domain.model

data class AppSettings(
    val countIn: Boolean = true,
    val haptics: Boolean = true,
    val gridLabels: Boolean = true,
    val highContrastPads: Boolean = false,
    val masterVolume: Float = 0.85f
)
