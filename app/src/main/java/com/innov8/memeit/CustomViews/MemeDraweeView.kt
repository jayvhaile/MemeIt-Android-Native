package com.innov8.memeit.CustomViews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.innov8.memeit.CustomClasses.LoadingDrawable
import com.innov8.memeit.sp
private const val STATE_NONE=-1
private const val STATE_LOADING=0
private const val STATE_FAILED=1
private const val STATE_LOADED=2
class MemeDraweeView:SimpleDraweeView,ControllerListener<ImageInfo>{

    override fun onFailure(id: String?, throwable: Throwable?) {
        state= STATE_FAILED
    }

    override fun onRelease(id: String?) {
        state= STATE_NONE
    }

    override fun onSubmit(id: String?, callerContext: Any?) {
        state= STATE_LOADING
    }

    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
    }

    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
    }

    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        state= STATE_LOADED
    }

    constructor(context: Context):super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int):super(context, attrs, defStyle){
        init()
    }
    private var state:Int= STATE_LOADING
    var onClick:(()->Unit)?=null
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
                .setControllerListener(this)
                .build()
        hierarchy.setRetryImage(retry)
        hierarchy.setFailureImage(failed)
        hierarchy.setProgressBarImage(LoadingDrawable(context))
        setOnClickListener {
            when(state){
                STATE_LOADED->onClick?.invoke()
//                STATE_FAILED->
            }
        }



    }
}