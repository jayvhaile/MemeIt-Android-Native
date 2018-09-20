package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.sp

class MemeDraweeView:SimpleDraweeView{
    constructor(context: Context):super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int):super(context, attrs, defStyle){
        init()
    }




    fun init (){
        val textDrawable=TextDrawable
                .builder()
                .beginConfig()
                .bold()
                .textColor(Color.GRAY)
                .fontSize(14.sp)
                .endConfig()
        val r="Loading Meme Failed. Tap to Retry"
        val f="Loading Meme Failed"
        val retry=textDrawable.buildRect(r,Color.TRANSPARENT)
        val failed=textDrawable.buildRect(f,Color.TRANSPARENT)


        controller=Fresco.newDraweeControllerBuilder()
                .setTapToRetryEnabled(true)
                .build()
        hierarchy.setRetryImage(retry)
        hierarchy.setFailureImage(failed)
        hierarchy.setProgressBarImage(LoadingDrawable(context))


    }
}