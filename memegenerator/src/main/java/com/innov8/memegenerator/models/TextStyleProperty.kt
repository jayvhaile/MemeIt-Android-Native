package com.innov8.memegenerator.models

import android.graphics.Color

data class TextStyleProperty(val textSize:Float=0f,
                             val textColor:Int= Color.BLACK,
                             val myTypeFace: MyTypeFace,
                             val bold:Boolean=false,
                             val italic:Boolean=false,
                             val allCap:Boolean=false,
                             val stroked:Boolean=false,
                             val strokeColor:Int=Color.BLACK,
                             val strokeWidth:Float=0f)