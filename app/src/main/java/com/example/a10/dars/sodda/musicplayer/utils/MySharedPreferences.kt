package com.example.a10.dars.sodda.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {
    private const val KEY = "key"
    private const val NAME = "isMusicPlay"
    private const val MODE = Context.MODE_PRIVATE
    lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor: SharedPreferences.Editor = edit()
        operation(editor)
        editor.apply()

    }

    var user: String?
        get() = sharedPreferences.getString(KEY, "")
        set(value) = MySharedPreferences.sharedPreferences.edit {
            if (value != null) {
                it.putString(KEY, value)
            }

        }
}