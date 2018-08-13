package com.innov8.memegenerator.memeEngine

import android.graphics.Typeface

interface TextEditInterface {
    fun onTextSizeChanged(size:Float)
    fun onTextColorChanged(color:Int)
    fun onTextFontChanged(typeface: Typeface)
    fun onTextSetBold(bold:Boolean)
    fun onTextSetItalic(italic:Boolean)
    fun onTextSetAllCap(allCap:Boolean)
    fun onTextSetStroked(stroked:Boolean)
    fun onTextStrokeChanged(strokeSize:Float)
    fun onTextStrokrColorChanged(strokeColor:Int)

}