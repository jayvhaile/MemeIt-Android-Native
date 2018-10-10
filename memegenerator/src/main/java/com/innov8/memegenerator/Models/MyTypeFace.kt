package com.innov8.memegenerator.Models

import android.content.Context
import android.graphics.Typeface

data class MyTypeFace(val name: String, private val fileName: String = "") {

    companion object {
        var DEFAULT: MyTypeFace = MyTypeFace("Default")
        private fun loadAllFiles(): List<MyTypeFace> {
            val prefix = "fonts/"
            val ttf = ".ttf"
            return listOf(
                    MyTypeFace("Arial", "${prefix}arial$ttf"),
                    MyTypeFace("Avenir", "${prefix}avenir$ttf"),
                    MyTypeFace("Helvetica", "${prefix}helvetica$ttf"),
                    MyTypeFace("Impact", "${prefix}impact$ttf"),
                    MyTypeFace("Lyric", "${prefix}lyric_font$ttf"),
                    MyTypeFace("Pacifico", "${prefix}Pacifico$ttf"),
                    MyTypeFace("Ubuntu", "${prefix}ubuntu$ttf")
            )
        }

        val typefaceFiles by lazy { loadAllFiles() }

        fun byName(name: String, context: Context? = null): MyTypeFace? {
            val t = typefaceFiles.find { font -> font.name == name }
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


    fun getTypeFace(context: Context? = null): Typeface {
        if (typeface == null)
            loadTypeFace(context!!)
        return typeface!!
    }

    override fun toString()=name
    override fun equals(other: Any?): Boolean=(other as? MyTypeFace)?.name==name
    override fun hashCode(): Int=name.hashCode()
}

