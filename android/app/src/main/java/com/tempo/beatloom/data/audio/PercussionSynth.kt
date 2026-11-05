package com.tempo.beatloom.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.tempo.beatloom.domain.model.Voice
import java.util.Random
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class PercussionSynth {

    private val pools = HashMap<Voice, List<AudioTrack>>()
    private val cursors = HashMap<Voice, Int>()
    private var prepared = false

    @Synchronized
    fun prepare() {
        if (prepared) return
        prepared = true
        for (voice in Voice.entries) {
            val data = render(voice)
            val tracks = ArrayList<AudioTrack>(POOL_SIZE)
            repeat(POOL_SIZE) {
                buildTrack(data)?.let(tracks::add)
            }
            if (tracks.isNotEmpty()) {
                pools[voice] = tracks
                cursors[voice] = 0
            }
        }
    }

    fun play(voice: Voice, volume: Float) {
        val pool = pools[voice] ?: return
        val index = ((cursors[voice] ?: 0) + 1) % pool.size
        cursors[voice] = index
        val track = pool[index]
        runCatching {
            if (track.playState != AudioTrack.PLAYSTATE_STOPPED) {
                track.stop()
            }
            track.reloadStaticData()
            track.setVolume(volume.coerceIn(0f, 1f))
            track.play()
        }
    }

    fun estimatedLatencyMs(): Int {
        val minBytes = runCatching {
            AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
        }.getOrDefault(0)
        if (minBytes <= 0) return DEFAULT_LATENCY_MS
        val frames = minBytes / 2
        return ((frames * 1000L) / SAMPLE_RATE).toInt().coerceIn(4, 400)
    }

    @Synchronized
    fun release() {
        for (pool in pools.values) {
            for (track in pool) {
                runCatching {
                    track.stop()
                    track.release()
                }
            }
        }
        pools.clear()
        cursors.clear()
        prepared = false
    }

    private fun buildTrack(data: ShortArray): AudioTrack? = runCatching {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(data.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(data, 0, data.size)
        track
    }.getOrNull()

    private fun render(voice: Voice): ShortArray = when (voice) {
        Voice.KICK -> renderKick()
        Voice.SNARE -> renderSnare()
        Voice.HAT -> renderHat()
        Voice.CLAP -> renderClap()
        Voice.TOM -> renderTom()
        Voice.RIM -> renderRim()
    }

    private fun renderKick(): ShortArray = sweep(0.14, 110.0, 45.0, 26.0, 0.95)

    private fun renderTom(): ShortArray = sweep(0.18, 150.0, 105.0, 18.0, 0.8)

    private fun renderRim(): ShortArray = sweep(0.025, 1600.0, 1400.0, 140.0, 0.6)

    private fun renderSnare(): ShortArray {
        val count = frames(0.12)
        val random = Random(SEED_SNARE)
        val out = ShortArray(count)
        var phase = 0.0
        val increment = 2.0 * PI * 190.0 / SAMPLE_RATE
        for (index in 0 until count) {
            val time = index.toDouble() / SAMPLE_RATE
            val noiseEnv = exp(-time * 34.0)
            val bodyEnv = exp(-time * 46.0)
            val noise = (random.nextDouble() * 2.0 - 1.0) * noiseEnv * 0.55
            val body = sin(phase) * bodyEnv * 0.45
            phase += increment
            out[index] = clip((noise + body) * 0.9)
        }
        return fadeOut(out)
    }

    private fun renderHat(): ShortArray {
        val count = frames(0.045)
        val random = Random(SEED_HAT)
        val out = ShortArray(count)
        var previous = 0.0
        for (index in 0 until count) {
            val time = index.toDouble() / SAMPLE_RATE
            val env = exp(-time * 150.0)
            val raw = random.nextDouble() * 2.0 - 1.0
            val highPassed = raw - previous
            previous = raw
            out[index] = clip(highPassed * env * 0.5)
        }
        return fadeOut(out)
    }

    private fun renderClap(): ShortArray {
        val count = frames(0.09)
        val random = Random(SEED_CLAP)
        val out = ShortArray(count)
        val burstGap = frames(0.012)
        val burstLength = frames(0.008)
        for (index in 0 until count) {
            val time = index.toDouble() / SAMPLE_RATE
            var gain = exp(-time * 26.0) * 0.35
            for (burst in 0 until 3) {
                val start = burst * burstGap
                if (index >= start && index < start + burstLength) {
                    gain += 0.55
                }
            }
            val raw = random.nextDouble() * 2.0 - 1.0
            out[index] = clip(raw * gain)
        }
        return fadeOut(out)
    }

    private fun sweep(
        seconds: Double,
        startHz: Double,
        endHz: Double,
        decay: Double,
        amplitude: Double
    ): ShortArray {
        val count = frames(seconds)
        val out = ShortArray(count)
        var phase = 0.0
        for (index in 0 until count) {
            val position = index.toDouble() / count
            val time = index.toDouble() / SAMPLE_RATE
            val frequency = startHz + (endHz - startHz) * position
            phase += 2.0 * PI * frequency / SAMPLE_RATE
            out[index] = clip(sin(phase) * exp(-time * decay) * amplitude)
        }
        return fadeOut(out)
    }

    private fun fadeOut(data: ShortArray): ShortArray {
        val tail = minOf(FADE_FRAMES, data.size)
        for (index in 0 until tail) {
            val position = data.size - tail + index
            val gain = 1.0 - index.toDouble() / tail
            data[position] = (data[position] * gain).toInt().toShort()
        }
        return data
    }

    private fun frames(seconds: Double): Int = (SAMPLE_RATE * seconds).toInt().coerceAtLeast(64)

    private fun clip(value: Double): Short =
        (value.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()

    private companion object {
        const val SAMPLE_RATE = 44100
        const val POOL_SIZE = 2
        const val FADE_FRAMES = 160
        const val DEFAULT_LATENCY_MS = 40
        const val SEED_SNARE = 7211L
        const val SEED_HAT = 1933L
        const val SEED_CLAP = 4517L
    }
}
