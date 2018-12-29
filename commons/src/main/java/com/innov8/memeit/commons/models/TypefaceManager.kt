package com.innov8.memeit.commons.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface

@SuppressLint("StaticFieldLeak")
object TypefaceManager {
    private lateinit var context: Context

    private data class MyTypeface(val name: String, val filename: String) {
        val typeface: Typeface by lazy {
            Typeface.createFromAsset(context.assets, filename)
        }
    }

    private val myTypefaces by lazy {
        val prefix = "fonts/"
        val ttf = ".ttf"
        listOf(
                MyTypeface("Agane", "${prefix}agane_bold$ttf"),
                MyTypeface("Arial", "${prefix}arial$ttf"),
                MyTypeface("Avenir", "${prefix}avenir$ttf"),
                MyTypeface("Helvetica", "${prefix}helvetica$ttf"),
                MyTypeface("Impact", "${prefix}impact$ttf"),
                MyTypeface("Lyric", "${prefix}lyric_font$ttf"),
                MyTypeface("Pacifico", "${prefix}Pacifico$ttf"),
                MyTypeface("Ubuntu", "${prefix}ubuntu$ttf")
        )
    }
    val fonts by lazy {
        myTypefaces.map { it.name }
    }

    fun init(context: Context) {
        this.context = context
    }

    fun byName(name: String): Typeface {
        return myTypefaces.find {
            it.name == name
        }?.typeface ?: Typeface.DEFAULT
    }
}