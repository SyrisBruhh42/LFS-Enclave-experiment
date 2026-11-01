package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.Biome3D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Zero-asset Procedural Sci-Fi Audio Synthesizer for Enclave 16.
 *
 * Uses Android's native [AudioTrack] in PCM_16BIT static/oneshot buffers on a background thread
 * to synthesize:
 * - Pneumatic bulkhead door unsealing sweeps
 * - 432Hz/528Hz/648Hz Cymatic Breakthrough chords
 * - Cryptographic Cognitive Vault unlock chimes
 * - Ticking Crisis Bulkhead klaxon pulses
 * - 7.83 Hz Schumann Sanctuary singing-bowl harmonics
 *
 * Safely no-ops in headless/unit-test environments or when muted by the player.
 */
object EnclaveSoundAndHapticsManager {

    private const val SAMPLE_RATE = 22050
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var isAudioEnabled: Boolean = true

    fun playDoorOpenSfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 260) { t, norm ->
                // Pneumatic hiss + warm brass resonant latch (180Hz -> 320Hz)
                val sweepFreq = 180.0 + 140.0 * norm
                val envelope = sin(PI * norm) * exp(-2.2 * norm)
                val tone = 0.65 * sin(2.0 * PI * sweepFreq * t) +
                    0.35 * sin(2.0 * PI * (sweepFreq * 1.5) * t)
                tone * envelope * 0.45
            }
        }
    }

    fun playBreakthroughSfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 520) { t, norm ->
                // Cymatic harmonic triad (432 Hz, 540 Hz, 648 Hz) with bell shimmer
                val env = (1.0 - norm) * exp(-1.6 * norm)
                val f1 = sin(2.0 * PI * 432.0 * t)
                val f2 = sin(2.0 * PI * 540.0 * t)
                val f3 = sin(2.0 * PI * 648.0 * t)
                val shimmer = sin(2.0 * PI * 864.0 * t) * (0.5 + 0.5 * sin(2.0 * PI * 7.83 * t))
                ((f1 * 0.35) + (f2 * 0.30) + (f3 * 0.25) + (shimmer * 0.10)) * env * 0.55
            }
        }
    }

    fun playResonantSfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 280) { t, norm ->
                val freq = if (norm < 0.48) 384.0 else 480.0
                val env = (1.0 - norm) * exp(-2.0 * norm)
                (0.75 * sin(2.0 * PI * freq * t) + 0.25 * sin(2.0 * PI * freq * 2.0 * t)) * env * 0.42
            }
        }
    }

    fun playVaultUnlockSfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 390) { t, norm ->
                val freq = when {
                    norm < 0.33 -> 330.0
                    norm < 0.66 -> 440.0
                    else -> 660.0
                }
                val env = exp(-1.8 * (norm % 0.34))
                sin(2.0 * PI * freq * t) * env * 0.45
            }
        }
    }

    fun playCrisisAlertSfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 360) { t, norm ->
                val pulse = if (norm < 0.45 || norm in 0.55..0.95) 1.0 else 0.0
                val freq = if (norm < 0.5) 290.0 else 245.0
                val wave = 0.7 * sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 2.0) * t)
                wave * pulse * 0.42
            }
        }
    }

    fun playSanctuarySfx() {
        if (!isAudioEnabled) return
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 580) { t, norm ->
                // Warm 256 Hz singing bowl modulated by Earth's 7.83 Hz Schumann heartbeat
                val schumannMod = 0.75 + 0.25 * sin(2.0 * PI * 7.83 * t)
                val env = sin(PI * norm) * exp(-1.2 * norm)
                val bowl = 0.7 * sin(2.0 * PI * 256.0 * t) + 0.3 * sin(2.0 * PI * 384.0 * t)
                bowl * schumannMod * env * 0.48
            }
        }
    }

    fun playBiomeTransitionHum(biome: Biome3D) {
        if (!isAudioEnabled) return
        val baseFreq = when (biome) {
            Biome3D.FORGE_HALL -> 144.0
            Biome3D.ARBORETUM -> 216.0
            Biome3D.ATLAS_CHAMBER -> 256.0
            Biome3D.STORMBREAKER -> 192.0
            Biome3D.DEEP_AQUIFER -> 172.0
        }
        audioScope.launch {
            playSynthesizedBuffer(durationMs = 420) { t, norm ->
                val schumann = 0.8 + 0.2 * sin(2.0 * PI * 7.83 * t)
                val env = sin(PI * norm) * exp(-1.5 * norm)
                (0.7 * sin(2.0 * PI * baseFreq * t) + 0.3 * sin(2.0 * PI * baseFreq * 1.5 * t)) * schumann * env * 0.35
            }
        }
    }

    private fun playSynthesizedBuffer(
        durationMs: Int,
        generator: (timeSec: Double, normalizedProgress: Double) -> Double
    ) {
        try {
            val totalSamples = (SAMPLE_RATE * durationMs) / 1000
            if (totalSamples <= 0) return
            val pcm = ShortArray(totalSamples)
            for (i in 0 until totalSamples) {
                val t = i.toDouble() / SAMPLE_RATE.toDouble()
                val norm = i.toDouble() / totalSamples.toDouble()
                val sample = generator(t, norm).coerceIn(-1.0, 1.0)
                pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            val byteSize = totalSamples * 2
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
                .setBufferSizeInBytes(byteSize.coerceAtLeast(2048))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, totalSamples)
            track.play()
            Thread.sleep(durationMs.toLong() + 40L)
            track.stop()
            track.release()
        } catch (_: Throwable) {
            // Safely ignore in headless JVM / emulator environments without audio output
        }
    }
}
