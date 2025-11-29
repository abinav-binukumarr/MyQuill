package com.myquill.app

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class SoundButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {
    override fun performClick(): Boolean {
        SoundEffectsManager.playClick()
        return super.performClick()
    }
}
