package com.innov8.memeit.commons.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface

@SuppressLint("StaticFieldLeak")
object TypefaceManager {
    private lateinit var context: Context

    private data class TypefaceFile(val name: String, val filename: String) {
        val typeface: Typeface by lazy {
            Typeface.createFromAsset(context.assets, filename)
        }
    }

    private val typefaceFiles by lazy {
        val prefix = "fonts/"
        context.assets.list("fonts")!!.map {
            TypefaceFile(it.split(".")[0], "$prefix$it")
        }
    }
    val fonts by lazy {
        typefaceFiles.map { it.name }
    }

    fun init(context: Context) {
        this.context = context
    }

    fun byName(name: String): Typeface {
        return typefaceFiles.find {
            it.name == name
        }?.typeface ?: Typeface.DEFAULT
    }
}