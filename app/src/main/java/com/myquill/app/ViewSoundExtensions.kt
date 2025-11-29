package com.myquill.app

import android.view.View

fun View.setOnClickListenerWithSound(listener: (v: View) -> Unit) {
    setOnClickListener {
        SoundEffectsManager.playClick()
        listener(it)
    }
}
