package com.myquill.app

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object SoundEffectsManager {
    private var audioManager: AudioManager? = null
    private var toneGen: ToneGenerator? = null
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        initialized = true
    }

    fun playClick() {
        // Prefer system click sound; fall back to a short tone
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK) ?: toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
    }

    fun playSuccess() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
    }

    fun playError() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
    }

    fun playNotification() {
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
    }
}
