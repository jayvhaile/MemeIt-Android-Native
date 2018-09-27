package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memegenerator.utils.dp

class ProfileDraweeView:SimpleDraweeView{
    constructor(context: Context,text:String="",color:Int=Color.RED):super(context){
        this.text=text
        this.color=color
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int):super(context, attrs, defStyle){
        init()
    }


    lateinit var textDrawable:TextDrawable
    var text:String=""
        set(value) {
            field = value
            init()
        }
    var color:Int= Color.RED
        set(value) {
            field = value
            init()
        }

    fun init (){
        textDrawable=TextDrawable
                .builder()
                .beginConfig()
                .bold()
                .endConfig()
                .buildRound(text,color)

        val rp=RoundingParams.asCircle()
        rp.setBorder(Color.WHITE,2f.dp(context))
        hierarchy.roundingParams=rp
        hierarchy.setPlaceholderImage(textDrawable)
    }
}