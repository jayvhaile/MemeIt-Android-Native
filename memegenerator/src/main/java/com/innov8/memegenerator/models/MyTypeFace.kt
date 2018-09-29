package com.innov8.memegenerator.models

import android.content.Context
import android.graphics.Typeface

open class MyTypeFace(val name: String, private val fileName: String = "", loadNow: Boolean = false) {

    companion object {
        var DEFAULT: MyTypeFace = MyTypeFace("Default")
        private fun loadAllFiles(): List<MyTypeFace> {
            val prefix = "fonts/"
            val ttf = ".ttf"
            return listOf<MyTypeFace>(
                    MyTypeFace("Arial", "${prefix}arial$ttf"),
                    MyTypeFace("Avenir", "${prefix}avenir$ttf"),
                    MyTypeFace("Helvetica", "${prefix}helvetica$ttf"),
                    MyTypeFace("Impact", "${prefix}impact$ttf"),
                    MyTypeFace("Lyric", "${prefix}lyric_font$ttf"),
                    MyTypeFace("Pacifico", "${prefix}Pacifico$ttf"),
                    MyTypeFace("Ubuntu", "${prefix}ubuntu$ttf")
            )
        }

        private var typefaceFiles: List<MyTypeFace>? = null
        fun getTypefaceFiles(): List<MyTypeFace> {
            if (typefaceFiles == null)
                typefaceFiles = loadAllFiles()
            return typefaceFiles!!
        }

        fun byName(name: String, context: Context? = null): MyTypeFace? {
            val t = getTypefaceFiles().find { font -> font.name == name }
            if (t != null && context != null)
                t.loadTypeFace(context)
            return t
        }
    }

    private fun loadTypeFace(context: Context) {
        typeface = if (name == "Default")
            Typeface.DEFAULT
        else
            Typeface.createFromAsset(context.assets, fileName)
    }

    @Transient
    private var typeface: Typeface? = null

    open fun getTypeFace(context: Context? = null): Typeface {
        if (typeface == null)
            loadTypeFace(context!!)
        return typeface!!
    }

    override fun toString(): String {
        return name.toString()
    }
}

