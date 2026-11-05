package com.tempo.beatloom.domain.model

enum class Voice(val key: String, val tint: Long) {
    KICK("kick", 0xFF7B5CFF),
    SNARE("snare", 0xFFFFB627),
    HAT("hat", 0xFF00D1C1),
    CLAP("clap", 0xFFFF6B9A),
    TOM("tom", 0xFFFF8A4C),
    RIM("rim", 0xFF8B90A6);

    companion object {
        fun fromKey(key: String): Voice? = entries.firstOrNull { it.key == key }
    }
}
