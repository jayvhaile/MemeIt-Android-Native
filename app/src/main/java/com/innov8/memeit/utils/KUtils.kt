package com.innov8.memeit.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.adroitandroid.chipcloud.ChipCloud
import com.adroitandroid.chipcloud.ChipListener
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.imagepipeline.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.activities.SettingsActivity
import com.innov8.memeit.activities.TagMemesActivity
import com.innov8.memeit.MemeItApp
import com.innov8.memeit.R
import com.innov8.memeit.workers.ProfileImageUploadWorker
import com.innov8.memeit.workers.ProfileUploadWorker
import com.innov8.memeit.commons.log
import com.innov8.memeit.commons.views.MemeItTextView
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.memeit.backend.models.Meme
import com.memeit.backend.models.Meme.MemeType.GIF
import com.memeit.backend.models.Meme.MemeType.IMAGE
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.Reaction
import com.stfalcon.frescoimageviewer.ImageViewer
import java.io.File
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import com.innov8.memeit.MemeItApp.Companion.instance as it

val SECOND_MILLIS = 1000L
val MINUTE_MILLIS = SECOND_MILLIS * 60
val HOUR_MILLIS = MINUTE_MILLIS * 60
val DAY_MILLIS = HOUR_MILLIS * 24
val WEEK_MILLIS = DAY_MILLIS * 7
val MONTH_MILLIS = DAY_MILLIS * 30
val YEAR_MILLIS = DAY_MILLIS * 365

fun formatDate(date: Long): String {
    val now = System.currentTimeMillis()

    val sdf = SimpleDateFormat("hh:mm a")
    val sdf2 = SimpleDateFormat("MMM dd, yyyy")

    if (date < 0) {
        throw IllegalArgumentException("Illegal Date")
    }
    val diff = if (date > now) 0 else now - date
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
    return if (this.isNullOrEmpty()) "..." else this[0].toString()
}

val screenWidth get() = it.resources.displayMetrics.widthPixels
val screenHeight get() = it.resources.displayMetrics.heightPixels

val screenWidthOriented: Int
    get() {
        val orientation = it.resources.configuration.orientation
        return when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> screenHeight
            else -> screenWidth
        }
    }

val screenHeightOriented: Int
    get() {
        val orientation = it.resources.configuration.orientation
        return when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> screenWidth
            else -> screenHeight
        }
    }


fun getImageMemeUrl(id: String, ratio: Float = 1f, fac: Float, quality: Int): String {
    val w = (screenWidthOriented * fac) step 50
    val h = ((screenWidthOriented / ratio).toInt()
            .trim(200.dp, screenHeightOriented - 200.dp) * fac) step 50

    return "https://res.cloudinary.com/innov8/image/fetch/c_fit,h_$h,q_$quality,w_$w/${id.full}"
}

fun getGifMemeUrl(id: String, ratio: Float = 1f, fac: Float, quality: Int): String {
    val w = (screenWidthOriented * fac) step 50
    val h = ((screenWidthOriented / ratio).toInt()
            .trim(200.dp, screenHeightOriented - 200.dp) * fac) step 50

    return "https://res.cloudinary.com/innov8/image/fetch/c_fit,h_$h,q_$quality,w_$w/${id.full}"
}

fun MemeTemplate.generatePreviewUrl(): String {
    val level = SettingsActivity.getImageQualityLevel(MemeItApp.instance)
    val quality = SettingsActivity.quality[level]
    val fac = SettingsActivity.factor[level]
//    return "http://localhost:8080/img/bg-cta.jpg"
    return getImageMemeUrl(memeTemplateProperty.previewImageUrl, 1f, fac, quality)
}

fun Meme.generateUrl(): String {
    val level = SettingsActivity.getImageQualityLevel(MemeItApp.instance)
    val quality = SettingsActivity.quality[level]
    val fac = SettingsActivity.factor[level]
    return when (getType()) {
        IMAGE -> getImageMemeUrl(imageId!!, imageRatio.toFloat(), fac, quality)
        GIF -> getGifMemeUrl(imageId!!, imageRatio.toFloat(), fac, quality)
    }
}

val String.full get() = if (this.startsWith("http")) this else "${MemeItApp.STORAGE_URL}$this"


fun ProfileDraweeView.loadImage(url: String?, width: Int = R.dimen.profile_image_expanded_size.dimen().toInt(), height: Int = width) {
    if (url == null) {
        setImageURI(null as String?)
        return
    }
    val level = SettingsActivity.getImageQualityLevel(context)
    val quality = SettingsActivity.quality[level]
    val u = "https://res.cloudinary.com/innov8/image/fetch/c_fit,h_$height,q_$quality,w_$width/${url.full}"
    setImageRequest(ImageRequest.fromUri(u))
}

fun Reaction.getDrawable(active: Boolean = true): Drawable {
    return ResourcesCompat.getDrawable(it.resources, getDrawableID(active), null)!!
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

fun Int.formatNumber() = when {
    this < 1000 -> this.toString()
    this < 1000_000 -> String.format("%.2fk", this / 1000.0f)
    this < 1000_000_000 -> String.format("%.2fm", this / 1000_000.0f)
    else -> String.format("%.2fb", this / 1000_000_000.0f)
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
val Float.dp: Float
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
    val b = System.currentTimeMillis()
    val t = block()
    val a = System.currentTimeMillis()
    log("fucck", tag, "before $b after $a duration ${a - b}")
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

fun View.snack(message: String, actionText: String? = null, action: ((View) -> Unit)? = null, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).applyIf(actionText != null && action != null) {
        setAction(actionText!!, action!!)
    }.show()
}

fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    if (condition) block()
    return this
}

fun View.animateVisibility() {
    val isVisible = visibility == View.VISIBLE
    val from = if (isVisible) 1.0f else 0.0f
    val to = if (isVisible) 0.0f else 1.0f

    val animation = ObjectAnimator.ofFloat(this, "alpha", from, to)
    animation.duration = ViewConfiguration.getDoubleTapTimeout().toLong()

    if (isVisible) {
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
        })
    } else
        visibility = View.VISIBLE

    animation.start()
}

fun Context.showMemeZoomView(list: List<Meme>, startingMemeIt: String = list[0].id!!) {
    val hierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
            .setProgressBarImage(LoadingDrawable(this))
    val overlayView = LayoutInflater.from(this).inflate(R.layout.overlay, null, false)
    /*val overlayName = overlayView.findViewById<TextView>(R.id.overlay_name)
    val overlayDesc = overlayView.findViewById<TextView>(R.id.overlay_description)
    val overlayTags = overlayView.findViewById<TextView>(R.id.overlay_tags)
    */ImageViewer.Builder<Meme>(this, list)
            .setFormatter { it.generateUrl() }
            .setOverlayView(overlayView)
            .setImageChangeListener {
                /*overlayName.text = list[it].poster?.name
                overlayDesc.text = list[it].description
                overlayTags.text = list[it].tags.joinToString(", ") { tag -> "#$tag" }*/
            }
            .setCustomDraweeHierarchyBuilder(hierarchy)
            .setBackgroundColor(Color.parseColor("#f6000000"))
            .setStartPosition(list.map { it.id }.indexOf(startingMemeIt))
            .hideStatusBar(false)
            .show()
}

fun ConstraintSet.makeGone(vararg ids: Int) = ids.forEach { this.setVisibility(it, View.GONE) }
fun ConstraintSet.makeVisible(vararg ids: Int) = ids.forEach { this.setVisibility(it, View.VISIBLE) }
val TextInputLayout.text get() = this.editText!!.text.toString()
fun TextInputLayout.clear() {
    this.editText!!.text.clear()
    this.editText!!.error = null
}

fun TextInputLayout.validateLength(min: Int, max: Int, tag: String): Boolean {
    this.text.validateLength(min, max, tag)?.let {
        editText!!.error = it
        return false
    } ?: return true
}

fun TextInputLayout.validateEmailorEmpty(): Boolean {
    this.text.validateEmailOrEmpty()?.let {
        editText!!.error = it
        return false
    } ?: return true
}

fun Pair<TextInputLayout, TextInputLayout>.validateMatch(tag: String): Boolean {
    first.text.validateMatch(second.text, tag)?.let {
        second.editText!!.error = it
        return false
    } ?: return true
}

fun enqueueProfileImageUpload(imageUrl: String) {
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    val uploadWork = OneTimeWorkRequest.Builder(ProfileImageUploadWorker::class.java)
            .setConstraints(constraints)
            .addTag("upload")
            .addTag("pp_upload")
            .setInputData(Data.Builder().putString(ProfileImageUploadWorker.IMAGE_URL, imageUrl).build())
            .build()
    val postWork = OneTimeWorkRequest.Builder(ProfileUploadWorker::class.java)
            .setConstraints(constraints)
            .addTag("upload")
            .addTag("pp_post")
            .build()
    WorkManager.getInstance()
            .beginUniqueWork("pp_upload_post", ExistingWorkPolicy.REPLACE, uploadWork)
            .then(postWork)
            .enqueue()

}

fun TabLayout.addOnTabSelected(listener: (TabLayout.Tab) -> Unit) {
    this.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab?> {
        override fun onTabReselected(p0: TabLayout.Tab?) {

        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            listener(tab!!)
        }
    })
}

fun Context.addFileToMediaStore(file: File, mimeType: String = "image/jpeg") {
    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            })
}

fun generateTextLinkActions(context: Context): (MemeItTextView.LinkMode, String) -> Unit = { mode, text ->
    when (mode) {
        MemeItTextView.LinkMode.PHONE -> context.startActivity(Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${text.trim()}")
        })
        MemeItTextView.LinkMode.EMAIL -> context.startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, text.trim())
            putExtra(Intent.EXTRA_SUBJECT, "From MemeIt")
        })
        MemeItTextView.LinkMode.HASHTAG -> TagMemesActivity.startWithTag(context, text.trim().substring(1))
        MemeItTextView.LinkMode.MENTION -> ProfileActivity.startWithUsername(context, text.trim().substring(1))
        MemeItTextView.LinkMode.URL -> context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(text.trim())
        })
    }
}

fun ChipCloud.onChipSelected(onChipSelected: (Int) -> Unit) {
    setChipListener(object : ChipListener {
        override fun chipDeselected(p0: Int) {

        }

        override fun chipSelected(p0: Int) {
            onChipSelected(p0)
        }
    })
}