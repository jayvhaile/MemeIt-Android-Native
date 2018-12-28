package com.innov8.memeit.commons.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface

@SuppressLint("StaticFieldLeak")
object TypefaceManager {
    private lateinit var context: Context

    private val files by lazy {
        val prefix = "fonts/"
        val ttf = ".ttf"
        mapOf(
                "Arial" to "${prefix}arial$ttf",
                "Avenir" to "${prefix}avenir$ttf",
                "Helvetica" to "${prefix}helvetica$ttf",
                "Impact" to "${prefix}impact$ttf",
                "Lyric" to "${prefix}lyric_font$ttf",
                "Pacifico" to "${prefix}Pacifico$ttf",
                "Ubuntu" to "${prefix}ubuntu$ttf"
        )
    }

    fun init(context: Context) {
        this.context = context
    }

    fun byName(name: String): Typeface {
        return files[name]?.run {
            Typeface.createFromAsset(context.assets, this)
        } ?: Typeface.DEFAULT
    }
}