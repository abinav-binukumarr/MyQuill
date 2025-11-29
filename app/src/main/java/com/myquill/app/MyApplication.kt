package com.myquill.app

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundEffectsManager.init(this)
    }
}
