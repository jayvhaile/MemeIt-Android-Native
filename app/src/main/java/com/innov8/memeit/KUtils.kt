package com.innov8.memeit

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.animation.drawable.AnimatedDrawable2
import com.facebook.fresco.animation.drawable.AnimationListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Meme.MemeType.IMAGE
import com.memeit.backend.dataclasses.Reaction
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import java.util.*

fun launchTask(block: suspend CoroutineScope.() -> Unit): Job {
    return launch { block() }
}

suspend fun <T> runAsync(block: suspend CoroutineScope.() -> T): T = async(CommonPool) { block() }.await()

val SECOND_MILLIS = 1000L
val MINUTE_MILLIS = SECOND_MILLIS * 60
val HOUR_MILLIS = MINUTE_MILLIS * 60
val DAY_MILLIS = HOUR_MILLIS * 24
val WEEK_MILLIS = DAY_MILLIS * 7
val MONTH_MILLIS = DAY_MILLIS * 30
val YEAR_MILLIS = DAY_MILLIS * 365

fun formatDate(date: Long): String {
    val now = System.currentTimeMillis()

    //todo locale based on selected lang
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val sdf2 = SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH)
    if (date < 0 || date > now) {
        throw IllegalArgumentException("Illegal Date")
    }

    val diff = now - date
    return when {
        diff < MINUTE_MILLIS -> "just now"
        diff < 2 * MINUTE_MILLIS -> "a minute ago"
        diff < HOUR_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS -> "an hour ago"
        diff < DAY_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 2 * DAY_MILLIS -> "yesterday at ${sdf.format(date)}"
        diff < WEEK_MILLIS -> "${diff / DAY_MILLIS} days ago"
        diff < 2 * WEEK_MILLIS -> "a week ago"
        else -> sdf2.format(date)
    }
}

fun Long.formateAsDate(): String = formatDate(this)

fun String?.prefix(): String {
    return if (this === null) "..." else this[0].toString()
}

fun SimpleDraweeView.load(id: String,width: Int= 100, height: Int = 100,memeType:Meme.MemeType) {
     if(memeType== IMAGE){
         val tlow = Transformation<Transformation<*>>().quality("5")
                 .width(50)
                 .height(50)
                 .crop("fit")
                 .effect("blur", 100)

         val level = SettingsActivity.getImageQualityLevel(context)
         val quality=SettingsActivity.quality[level]
         val fac=SettingsActivity.factor[level]
         val thigh = Transformation<Transformation<*>>()
                 .quality(quality)
                 .crop("fit")


         if (width > 0 && height > 0) {
             val w:Int=(width*fac).step(50)
             val h:Int=(height*fac).step(50)
             thigh.width(w).height(h)
         }else
             throw IllegalArgumentException("width or height cant be 0")

         val low = MediaManager.get().url().transformation(tlow).source(id).generate()
         val high = MediaManager.get().url().transformation(thigh).source(id).generate()


         val lowReq = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(low))
                 .setPostprocessor(IterativeBoxBlurPostProcessor(3))
                 .build()
         val c = Fresco.newDraweeControllerBuilder()
                 .setLowResImageRequest(lowReq)
                 .setImageRequest(ImageRequest.fromUri(high))
                 .setOldController(controller)
                 .build()
         controller = c
     }else{

         val url = MediaManager.get().url().resourcType("video")
                 .format("gif")
                 .generate(id)

         class m: BaseControllerListener<ImageInfo>() {
             override fun onFailure(id: String?, throwable: Throwable?) {
                 super.onFailure(id, throwable)
             }

             override fun onRelease(id: String?) {
                 super.onRelease(id)
             }

             override fun onSubmit(id: String?, callerContext: Any?) {
                 super.onSubmit(id, callerContext)
             }

             override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                 super.onIntermediateImageSet(id, imageInfo)
             }

             override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                 super.onIntermediateImageFailed(id, throwable)
             }

             override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                 super.onFinalImageSet(id, imageInfo, animatable)
                 if(animatable!=null){
                     val a=animatable as AnimatedDrawable2

                     a.setAnimationListener(object :AnimationListener{
                         override fun onAnimationRepeat(p0: AnimatedDrawable2?) {
                         }

                         override fun onAnimationStart(p0: AnimatedDrawable2?) {
                         }

                         override fun onAnimationFrame(p0: AnimatedDrawable2?, p1: Int) {
                         }

                         override fun onAnimationStop(p0: AnimatedDrawable2?) {
                             p0?.start()
                         }

                         override fun onAnimationReset(p0: AnimatedDrawable2?) {
                             p0?.start()
                         }
                     })
                 }
             }
         }
        controller = Fresco.newDraweeControllerBuilder()
                 .setUri(url)
                 .setAutoPlayAnimations(true)
                 .setControllerListener(m())
                 .build()
     }

}

fun ProfileDraweeView.loadImage(url:String?,width:Float,height: Float){

}


fun Reaction.getDrawable(context: Context,active:Boolean=true):Drawable{
    //todo include the inactive ones too
    val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    return VectorDrawableCompat.create(context.resources,activeRID[type.ordinal],null)!!
}
fun Reaction.getDrawableID(active:Boolean=true):Int{
    //todo include the inactive ones too
    val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    return activeRID[type.ordinal]
}

fun RecyclerView.makeLinear(orientation: Int=RecyclerView.VERTICAL){
    this.layoutManager=LinearLayoutManager(context,orientation,false)
}
fun Float.step(step:Int):Int{
    val x=Math.max(this/step,1f).toInt()
    return x*step
}

fun Any.log(vararg m:Any){
    Log.d(this::class.java.simpleName, m.joinToString(", "))
}

