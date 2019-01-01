package com.innov8.memeit.commons

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import java.io.ByteArrayInputStream
import java.io.InputStream

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

fun Context.loadBitmapfromStream(stream: InputStream, quality: Float = 1f): Bitmap? {
    val opt = BitmapFactory.Options()
    val ba = stream.readBytes()
    opt.inJustDecodeBounds = true
    BitmapFactory.decodeStream(ByteArrayInputStream(ba), null, opt)
    opt.inSampleSize = calcSampleSize(opt, quality)
    opt.inJustDecodeBounds = false
    return BitmapFactory.decodeStream(ByteArrayInputStream(ba), null, opt)
}

fun Context.loadBitmapfromStream(stream: InputStream, reqWidth: Int, reqHeight: Int = reqWidth): Bitmap? {
    val opt = BitmapFactory.Options()
    val ba = stream.readBytes()
    opt.inJustDecodeBounds = true
    BitmapFactory.decodeStream(ByteArrayInputStream(ba), null, opt)
    opt.inSampleSize = calcSampleSize(opt, reqWidth.dp(this), reqHeight.dp(this))
    opt.inJustDecodeBounds = false
    return BitmapFactory.decodeStream(ByteArrayInputStream(ba), null, opt)
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

fun EditText.addOnTextChanged(listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            listener(s.toString())
        }
    })
}

infix fun Long.min(min: Long) = if (this < min) min else this
infix fun Long.max(max: Long) = if (this > max) max else this
const val notidChannelName = "MemeIt Events Notification"
fun Context.sendUserNotification(data: NotifData, notifyID: Int) {

    val pendingIntent = PendingIntent.getActivity(this, 0, data.intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        android.app.Notification.Builder(this, notidChannelName)
    } else {
        android.app.Notification.Builder(this)
    }
    builder.setContentTitle(data.title)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(android.app.Notification.BigTextStyle().bigText(data.message))
            .setContentText(data.message)
            .setSmallIcon(data.icon)
    val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    manager.notify(notifyID, builder.build())
}

data class NotifData(val title: String,
                     val message: String,
                     val icon: Int,
                     val intent: Intent)