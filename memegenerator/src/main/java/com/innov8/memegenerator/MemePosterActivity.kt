package com.innov8.memegenerator

import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memegenerator.utils.log
import com.innov8.memegenerator.utils.toByteArray
import com.innov8.memegenerator.utils.toast
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.utilis.OnCompleteListener
import kotlinx.android.synthetic.main.activity_meme_poster.*
import java.io.File

class MemePosterActivity : AppCompatActivity() {
    companion object {
        var bitmap: Bitmap? = null
    }

    var texts: Array<String>? = null
    private lateinit var memeType: Meme.MemeType
    private var gif: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_poster)
        gif = intent?.getStringExtra("gif")
        log("fiki", gif ?: "")
        when {
            gif != null -> {
                memeType = Meme.MemeType.GIF
                val file=File(gif)
                log("fiki",file.exists())
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
        post_btn.setOnClickListener { upload() }
    }

    private fun upload() {
        post_btn.startAnimation()
        post_status.visibility = View.VISIBLE
        post_status.text = "Preparing to upload"

        when (memeType) {
            Meme.MemeType.IMAGE -> {
                val bitmap = (meme_image_view.drawable as BitmapDrawable).bitmap
                val byteArray = bitmap.toByteArray(Bitmap.CompressFormat.JPEG, 90)
                MediaManager.get().upload(byteArray).callback(MyUploadCallback()).dispatch()
            }
            Meme.MemeType.GIF -> {
                MediaManager.get()
                        .upload(gif)
                        .option("resource_type","video")
                        .callback(MyUploadCallback()).dispatch(this)

            }
        }
    }

    inner class MyUploadCallback : UploadCallback {
        override fun onStart(s: String) {
            post_btn.setProgress(0)
            post_status.text = "Uploading Image Started"
        }

        override fun onProgress(s: String, l: Long, l1: Long) {
            val p=(l * 100 / l1)
            post_btn.setProgress(p.toInt())
            post_status.text = "Uploading $p%"
        }

        override fun onSuccess(s: String, map: Map<*, *>) {
            val public_id = map["public_id"].toString()
            val width = map["width"].toString().toFloat()
            val height = map["height"].toString().toFloat()
            val ratio = width / height
            post_btn.resetProgress()
            post_status.text = "Image Uploaded! Posting Meme"
            MemeItMemes.getInstance().postMeme(prepareRequest(public_id, ratio), object : OnCompleteListener<Meme> {
                override fun onSuccess(memeResponse: Meme) {
                    post_btn.revertAnimation()
                    toast("Meme Posted to MemeIt!")
                    post_status.text = "Meme Posted"
                    //todo goto the post
                }

                override fun onFailure(error: OnCompleteListener.Error) {
                    post_btn.revertAnimation()
                    toast("Meme Posting failed: " + error.message)
                    post_status.text = "Meme Posting Failed"
                }
            })
        }

        override fun onError(s: String, errorInfo: ErrorInfo) {
            post_btn.revertAnimation()
            toast("Meme Image Uploading failed: " + errorInfo.description)
            post_status.text = "Image Uploading Failed"
        }

        override fun onReschedule(s: String, errorInfo: ErrorInfo) {
            post_btn.revertAnimation()
            toast("Meme Image Uploading rescheduled: " + errorInfo.description)
            post_status.text = "Image Uploading Rescheduled"
        }
    }

    private fun prepareRequest(uri: String, ratio: Float): Meme {
        val tags: List<String>? = getTags()
        //todo retrive description
        return Meme.createMeme(uri, ratio.toDouble(), memeType,"", texts?.toList(), tags)

    }

    fun getTags() =
            tags.text.split(" ")
                    .filter { it.startsWith("#") && it.length > 1 }
                    .map { it.substring(1) }
                    .toList()
}
class MyControllerListener : BaseControllerListener<ImageInfo>() {

    override fun onFailure(id: String?, throwable: Throwable?) {
        super.onFailure(id, throwable)
        log("fiki failure",throwable?.message?:"   xx")
    }

    override fun onRelease(id: String?) {
        super.onRelease(id)
    }

    override fun onSubmit(id: String?, callerContext: Any?) {
        super.onSubmit(id, callerContext)
        log("fiki submit")
    }

    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
        super.onIntermediateImageSet(id, imageInfo)
        log("fiki intermediate")

    }

    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
        super.onIntermediateImageFailed(id, throwable)
        log("fiki ifail",throwable?.message?:" xx")

    }

    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        super.onFinalImageSet(id, imageInfo, animatable)
        log("fiki final ",animatable!=null)
        animatable?.start()

    }
}
