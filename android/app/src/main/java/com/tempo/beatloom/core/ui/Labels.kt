package com.tempo.beatloom.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tempo.beatloom.R
import com.tempo.beatloom.domain.model.Voice

fun voiceLabelRes(voice: Voice): Int = when (voice) {
    Voice.KICK -> R.string.voice_kick
    Voice.SNARE -> R.string.voice_snare
    Voice.HAT -> R.string.voice_hat
    Voice.CLAP -> R.string.voice_clap
    Voice.TOM -> R.string.voice_tom
    Voice.RIM -> R.string.voice_rim
}

fun voiceTint(voice: Voice): Color = Color(voice.tint)

@Composable
fun voiceLabel(voice: Voice): String = stringResource(voiceLabelRes(voice))
