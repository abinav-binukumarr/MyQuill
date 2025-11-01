package com.myquill.app

import android.content.Context

object Auth {
    private const val PREF = "auth"
    private const val KEY = "uid"
    fun setUser(context: Context, id: Long) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putLong(KEY, id).apply()
    }
    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().remove(KEY).apply()
    }
    fun userId(context: Context): Long? {
        val v = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getLong(KEY, -1L)
        return if (v <= 0) null else v
    }
}