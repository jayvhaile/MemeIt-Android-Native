package com.innov8.memegenerator.memeEngine

import com.innov8.memegenerator.models.MyTypeFace
import com.innov8.memegenerator.models.TextStyleProperty

interface TextEditListener {
    fun onTextSizeChanged(size:Float)
    fun onTextColorChanged(color:Int)
    fun onTextFontChanged(typeface: MyTypeFace)
    fun onTextSetBold(bold:Boolean)
    fun onTextSetItalic(italic:Boolean)
    fun onTextSetAllCap(allCap:Boolean)
    fun onTextSetStroked(stroked:Boolean)
    fun onTextStrokeChanged(strokeSize:Float)
    fun onTextStrokrColorChanged(strokeColor:Int)
    fun onApplyAll(textStyleProperty: TextStyleProperty, applySize:Boolean=true)
}