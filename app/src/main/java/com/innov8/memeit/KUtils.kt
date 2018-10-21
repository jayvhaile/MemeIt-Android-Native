package com.innov8.memeit

import android.content.Context
import android.content.res.Configuration.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.animation.drawable.AnimatedDrawable2
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.material.snackbar.Snackbar
import com.innov8.memegenerator.utils.log
import com.innov8.memeit.Activities.SettingsActivity
import com.innov8.memeit.CustomViews.ProfileDraweeView
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.dataclasses.Meme.MemeType.GIF
import com.memeit.backend.dataclasses.Meme.MemeType.IMAGE
import com.memeit.backend.dataclasses.Reaction
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import com.innov8.memeit.MemeItApp.Companion.instance as it

fun launchTask(block: suspend CoroutineScope.() -> Unit): Job {
    return launch(UI) { block() }
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
    val sdf2 = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
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
    return if (this.isNullOrEmpty()) "..." else this!![0].toString()
}

val screenWidth: Int = it.resources.displayMetrics.widthPixels
val screenHeight: Int = it.resources.displayMetrics.heightPixels

val screenWidthOriented
    get() = it.resources.displayMetrics.widthPixels

val screenHeightOriented
    get() = it.resources.displayMetrics.widthPixels

fun getCloudinaryImageUrlForId(id: String): String = MediaManager.get().url().source(id).generate()

private fun getImageMemeUrl(id: String, ratio: Float = 1f): String {
    val level = SettingsActivity.getImageQualityLevel(MemeItApp.instance)
    val quality = SettingsActivity.quality[level]
    val fac = SettingsActivity.factor[level]

    val w = (screenWidth * fac) step 50
    val h = ((screenWidth / ratio).toInt()
            .trim(200.dp, screenHeight - 200.dp) * fac) step 50

    val thigh = Transformation<Transformation<*>>()
            .quality(quality)
            .crop("fit")
            .width(w)
            .height(h)
    return MediaManager.get().url().transformation(thigh).source(id).generate()
}

private fun getGifMemeUrl(id: String): String {
    val t = Transformation<Transformation<*>>()
            .effect("loop")
    /*.quality(quality)
    .crop("fit")
    .width(w)
    .height(h)*/

    return MediaManager.get().url().resourcType("video")
            .format("gif")
            .transformation(t)
            .generate(id)
}

fun Meme.generateUrl() = when (getType()) {
    IMAGE -> getImageMemeUrl(imageId!!, imageRatio.toFloat())
    GIF -> getGifMemeUrl(imageId!!)
}

private fun SimpleDraweeView.loadImageMeme(id: String, ratio: Float = 1f, resizeWidth: Int, resizeHeight: Int) {
    val tlow = Transformation<Transformation<*>>().quality("5")
            .width(50)
            .height(50)
            .crop("fit")
            .effect("blur", 60)

    val low = MediaManager.get().url().transformation(tlow).source(id).generate()
    val high = getImageMemeUrl(id, ratio)
    val lowReq = ImageRequestBuilder.fromRequest(ImageRequest.fromUri(low))
            .setPostprocessor(IterativeBoxBlurPostProcessor(2))
            .build()
    var req = ImageRequest.fromUri(high)
    if (resizeWidth > 0 && resizeHeight > 0)
        req = ImageRequestBuilder.fromRequest(req)
                .setResizeOptions(ResizeOptions.forDimensions(resizeWidth, resizeHeight))
                .build()
    val c = Fresco.newDraweeControllerBuilder()
            .setLowResImageRequest(lowReq)
            .setImageRequest(req)
            .setOldController(controller)
            .build()
    controller = c
}

private fun SimpleDraweeView.loadGifMeme(id: String, ratio: Float, resizeWidth: Int, resizeHeight: Int) {

    val level = SettingsActivity.getImageQualityLevel(context)
    val quality = SettingsActivity.quality[level]
    val fac = SettingsActivity.factor[level]


    val w = (screenWidth * fac) step 50
    val h = ((screenWidth / ratio).toInt()
            .trim(200.dp, screenHeight - 200.dp) * fac) step 50

    val tb = Transformation<Transformation<*>>()
            .quality(quality)
            .crop("fit")
            .width(w)
            .height(h)
    val thumb = MediaManager.get().url().resourcType("video")
            .format("jpg")
            .transformation(tb)
            .generate(id)


    val url = getGifMemeUrl(id)

    class m : BaseControllerListener<ImageInfo>() {
        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            if (animatable != null) {
                val a = animatable as AnimatedDrawable2
                a.start()
            }
        }
    }

    var req = ImageRequest.fromUri(url)
    if (resizeWidth > 0 && resizeHeight > 0)
        req = ImageRequestBuilder.fromRequest(req)
                .setResizeOptions(ResizeOptions.forDimensions(resizeWidth, resizeHeight))
                .build()

    controller = Fresco.newDraweeControllerBuilder()
            .setLowResImageRequest(ImageRequest.fromUri(thumb))
            .setImageRequest(req)
            .setAutoPlayAnimations(false)
            .setControllerListener(m())
            .build()
}

fun SimpleDraweeView.loadMeme(meme: Meme, resizeWidth: Int = 0, resizeHeight: Int = width) {
    if (meme.getType() == IMAGE) loadImageMeme(meme.imageId!!, meme.imageRatio.toFloat(), resizeWidth, resizeHeight)
    else if (meme.getType() == GIF) loadGifMeme(meme.imageId!!, meme.imageRatio.toFloat(), resizeWidth, resizeHeight)
}


fun ProfileDraweeView.loadImage(url: String?, width: Float = R.dimen.profile_mini_size.dimen(), height: Float = width) {
    if (url == null) {
        setImageURI(url as String?)
        return
    }

    val level = SettingsActivity.getImageQualityLevel(context)
    val quality = SettingsActivity.quality[level]
    val t = Transformation<Transformation<*>>()
            .width(width)
            .height(height)
            .quality(quality)
            .crop("fit")

    val u = MediaManager.get().url().source(url).generate()
    log("awrk", u)

    setImageRequest(ImageRequest.fromUri(u))


}


fun Reaction.getDrawable(active: Boolean = true): Drawable {
    val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    val inactiveRID = intArrayOf(R.drawable.laughing_inactive_light, R.drawable.rofl_inactive, R.drawable.neutral_inactive, R.drawable.angry_inactive)
    return VectorDrawableCompat.create(it.resources, if (active) activeRID[getType().ordinal] else inactiveRID[getType().ordinal], null)!!
}

fun Reaction.getDrawableID(active: Boolean = true): Int {
    val activeRID = intArrayOf(R.drawable.laughing, R.drawable.rofl, R.drawable.neutral, R.drawable.angry)
    val inactiveRID = intArrayOf(R.drawable.laughing_inactive_light, R.drawable.rofl_inactive, R.drawable.neutral_inactive, R.drawable.angry_inactive)

    return if (active) activeRID[getType().ordinal] else inactiveRID[getType().ordinal]
}

fun RecyclerView.makeLinear(orientation: Int = RecyclerView.VERTICAL) {
    this.layoutManager = LinearLayoutManager(context, orientation, false)
}

infix fun Float.step(step: Int): Int {
    val x = Math.max(this / step, 1f).toInt()
    return x * step
}

fun Any.log(vararg m: Any) {
    Log.d(this::class.java.simpleName, m.joinToString(", "))
}

fun Int.formatNumber(): String {
    if (this < 1000) {
        return this.toString()
    } else {
        val d = this / 1000.0f
        return String.format("%.2fk", d)
    }
}

fun Int.formatNumber(suffix: String = "", suffixPlural: String = ""): String {
    if (this >= 1000000) {
        val d = this / 1000000.0f
        return String.format("%.2fm %s", d, suffixPlural)
    } else if (this >= 1000) {
        val d = this / 1000.0f
        return String.format("%.2fk %s", d, suffixPlural)
    } else return if (this > 1) {
        String.format("%d %s", this, suffixPlural)
    } else if (this == 1) {
        String.format("%d %s", this, suffix)
    } else {
        String.format("no %s", this, suffix)
    }

}

fun Int.dimenI(context: Context = it): Int = context.resources.getDimension(this).toInt()
fun Int.dimen(context: Context = it): Float = context.resources.getDimension(this)
fun Int.color(context: Context = it): Int = context.resources.getColor(this)
fun Int.string(context: Context = it): String = context.resources.getString(this)
fun Int.strinArray(context: Context = it): Array<String> = context.resources.getStringArray(this)

infix fun Int.trim(max: Int): Int = if (this < max) this else max
fun Int.trim(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
val Float.DP: Float
    get() {
        return this * it.resources.displayMetrics.density
    }
val Float.SP: Float
    get() {
        return this * it.resources.displayMetrics.scaledDensity
    }
val Int.dp: Int
    get() {
        return (this * it.resources.displayMetrics.density).toInt()
    }
val Int.sp: Int
    get() {
        return (this * it.resources.displayMetrics.scaledDensity).toInt()
    }

fun <T> measure(tag: String = "", block: () -> T): T {
    val x = System.currentTimeMillis()
    val t = block()
    log("fucck", tag, System.currentTimeMillis() - x)
    return t
}


fun String.validateLength(min: Int, max: Int, tag: String): String? {
    return when {
        this.trim().length < min -> "$tag should at least be $min in length"
        this.trim().length > max -> "$tag should be less than $max in length"
        else -> null
    }
}


fun String.validateMatch(s2: String, tag: String): String? {
    return when {
        this != s2 -> "$tag doesn't match"
        else -> null
    }
}

fun String.isEmail(): Boolean {
    if (TextUtils.isEmpty(this)) return false
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String?.isEmailOrEmpty(): Boolean {
    if (isNullOrEmpty()) return true
    return this!!.isEmail()
}

fun String?.validateEmailOrEmpty(): String? = if (!isEmailOrEmpty()) "Invalid Email" else null

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT, root: Boolean = false) {
    Snackbar.make(if (root) rootView else this, message, duration).show()
}
