package com.innov8.memegenerator

import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memegenerator.utils.toByteArray
import com.innov8.memeit.commons.prefix
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.dataclasses.Meme
import kotlinx.android.synthetic.main.activity_meme_poster2.*
import java.io.File

class MemePosterActivity : AppCompatActivity() {
    companion object {
        var bitmap: Bitmap? = null
    }

    var texts: Array<String>? = null
    private lateinit var memeType: Meme.MemeType
    private var gif: String? = null


    private lateinit var progressDialog: MaterialDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_poster2)
        setSupportActionBar(toolbar)
        val muser = MemeItClient.myUser!!
        poster_pp.setText(muser.name?.prefix() ?: "")
        handleIntent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meme_poster, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_post -> upload()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleIntent() {
        gif = intent?.getStringExtra("gif")
        when {
            gif != null -> {
                memeType = Meme.MemeType.GIF
                val file = File(gif)
                val controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromFile(file))
                        .setAutoPlayAnimations(true)
                        .setControllerListener(MyControllerListener())
                        .build()
                meme_image_view.controller = controller
            }
            bitmap != null -> {
                memeType = Meme.MemeType.IMAGE
                meme_image_view.setImageBitmap(bitmap)
                bitmap = null
            }
            else -> {
                finish()
                return
            }
        }
        texts = intent?.getStringArrayExtra("texts")
    }


    private fun upload() {
        progressDialog = MaterialDialog.Builder(this)
                .title("Preparing to Upload")
                .progress(true, 100)
                .build()
        progressDialog.show()
        when (memeType) {
            Meme.MemeType.IMAGE -> handleImageUpload()
            Meme.MemeType.GIF -> handleGifUpload()
        }

    }

    private fun handleGifUpload() {
        MemeItClient.uploadImage(File(gif))
    }


    private fun handleImageUpload() {
        val bitmap = (meme_image_view.drawable as BitmapDrawable).bitmap
        val byteArray = bitmap.toByteArray(Bitmap.CompressFormat.JPEG, 90)
        MediaManager.get()
                .upload(byteArray)
                .callback(MyUploadCallback())
                .dispatch()
    }


    inner class MyUploadCallback : UploadCallback {
        override fun onStart(s: String) {
            progressDialog.setProgress(0)
            progressDialog.setTitle("Uploading Image Started")
        }

        override fun onProgress(s: String, l: Long, l1: Long) {
            val p = (l * 100 / l1)
            progressDialog.setProgress(p.toInt())
            progressDialog.setTitle("Uploading $p%")
        }

        override fun onSuccess(s: String, map: Map<*, *>) {
            val public_id = map["public_id"].toString()
            val width = map["width"].toString().toFloat()
            val height = map["height"].toString().toFloat()
            val ratio = width / height
            progressDialog.setProgress(-1)
            progressDialog.setTitle("Image Uploaded! Posting Meme")

            MemeItMemes.postMeme(prepareRequest(public_id, ratio)).call({
                progressDialog.hide()
                toast("Meme Posted to MemeIt!")
            }, {
                progressDialog.hide()
                toast("Meme Posting failed: $it")
            })
        }

        override fun onError(s: String, errorInfo: ErrorInfo) {
            progressDialog.hide()
            toast("Meme Image Uploading failed: " + errorInfo.description)
        }

        override fun onReschedule(s: String, errorInfo: ErrorInfo) {
            progressDialog.hide()
            toast("Meme Image Uploading rescheduled: " + errorInfo.description)
        }
    }

    private fun prepareRequest(uri: String, ratio: Float): Meme {
        return Meme(imageId = uri,
                imageRatio = ratio.toDouble(),
                type = memeType.name,
                description = caption_field.text?.toString(),
                texts = texts?.toList() ?: listOf(),
                tags = tags.toMutableList())

    }

    private val tags
        get() = tags_field.text.split(" ")
                .asSequence()
                .filter { it.startsWith("#") && it.length > 1 }
                .map { it.substring(1) }
                .toList()
}

class MyControllerListener : BaseControllerListener<ImageInfo>() {
    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        super.onFinalImageSet(id, imageInfo, animatable)
        animatable?.start()
    }
}
