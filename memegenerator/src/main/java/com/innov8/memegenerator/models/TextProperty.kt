package com.innov8.memegenerator.models

import android.graphics.Color
import android.graphics.Typeface

data class TextProperty(val textSize:Float=0f,
                        val textColor:Int= Color.BLACK,
                        val typeface: Typeface= Typeface.DEFAULT,
                        val bold:Boolean=false,
                        val italic:Boolean=false,
                        val allCap:Boolean=false,
                        val stroked:Boolean=false,
                        val strokeColor:Int=Color.BLACK,
                        val strokeWidth:Float=0f)