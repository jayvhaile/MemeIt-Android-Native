package com.innov8.memeit.commons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun log(vararg messages: Any) {
    Log.d("#MemeIt", messages.joinToString(" , "))
}

fun FragmentManager.replace(id: Int, fragment: androidx.fragment.app.Fragment) {
    beginTransaction().replace(id, fragment).commit()
}

fun calcSampleSize(option: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val width = option.outWidth
    val height = option.outHeight
    var size = 1
    if (width > reqWidth || height > reqHeight) {
        val hw = width / 2
        val hh = height / 2
        while (hw / size >= reqWidth && hh / size >= reqHeight) {
            size *= 2
        }
    }
    return size
}

fun calcSampleSize(option: BitmapFactory.Options, quality: Float): Int {
    val size = Math.max(option.outWidth, option.outHeight)
    val reqSize = size * if (quality > 1f) 1f else quality
    var sampleSize = 1
    if (size > reqSize) {
        val halfSize = size / 2
        while (halfSize / sampleSize > reqSize) sampleSize *= 2
    }
    return sampleSize
}

fun Context.loadBitmap(id: Int, quality: Float): Bitmap {
    val opt = BitmapFactory.Options()
    opt.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources, id, opt)
    opt.inSampleSize = calcSampleSize(opt, quality)
    opt.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(resources, id, opt)
}

fun Context.loadBitmap(id: Int, reqWidth: Int, reqHeight: Int = reqWidth): Bitmap {
    val opt = BitmapFactory.Options()
    opt.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources, id, opt)
    opt.inSampleSize = calcSampleSize(opt, reqWidth.dp(this), reqHeight.dp(this))
    opt.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(resources, id, opt)
}

fun Context.getDrawableIdByName(name: String): Int {
    return resources.getIdentifier(name, "drawable", packageName)
}

fun Float.toSP(context: Context): Float {
    return this / context.resources.displayMetrics.scaledDensity
}

fun Float.sp(context: Context): Float {
    return this * context.resources.displayMetrics.scaledDensity
}

fun Float.toDP(context: Context): Float {
    return this / context.resources.displayMetrics.density
}

fun Float.dp(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Int.dp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

fun Context.goTo(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}

fun Activity.goTo(clazz: Class<out Activity>, finish: Boolean = false) {
    startActivity(Intent(this, clazz))
    if (finish)
        finish()
}

fun Activity.goToWithString(clazz: Class<out Activity>, data: String, finish: Boolean = false) {
    val intent = Intent(this, clazz)
    intent.putExtra("string", data)
    startActivity(intent)
    if (finish)
        finish()
}

fun Activity.makeFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    } else {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
fun String?.prefix(): String {
    return if (this.isNullOrEmpty()) "..." else this!![0].toString()
}