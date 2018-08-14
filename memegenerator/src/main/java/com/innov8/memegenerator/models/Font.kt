package com.innov8.memegenerator.models

import android.content.Context
import android.graphics.Typeface

data class Font(val name: String, val fileName: String) {
    companion object {
        fun loadAllFonts(context: Context): List<Font> {
            val prefix = "fonts/"
            val ttf = ".ttf"
            return listOf<Font>(
                    Font("Arial", "${prefix}arial$ttf"),
                    Font("Avenir", "${prefix}avenir$ttf"),
                    Font("Helvetica", "${prefix}helvetica$ttf"),
                    Font("Impact", "${prefix}impact$ttf"),
                    Font("Lyric", "${prefix}lyric_font$ttf"),
                    Font("Pacifico", "${prefix}Pacifico$ttf"),
                    Font("Ubuntu", "${prefix}ubuntu$ttf")
            )
        }

        fun byName(context: Context, name: String): Typeface {
            return loadAllFonts(context)
                    .find { font -> font.name == name }?.loadTypeFace(context)
                    ?: Typeface.DEFAULT
        }
    }

    fun loadTypeFace(context: Context): Typeface {
        return Typeface.createFromAsset(context.assets, fileName)
    }
}