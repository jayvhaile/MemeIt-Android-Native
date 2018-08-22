package com.innov8.memegenerator

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton
import com.innov8.memegenerator.utils.toByteArray
import com.innov8.memegenerator.utils.toast
import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.utilis.OnCompleteListener
import java.util.*

class MemePosterActivity : AppCompatActivity() {
    companion object {
        var bitmap:Bitmap?=null
    }
    lateinit var imageV:ImageView
    lateinit var statusV:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_poster)
        imageV=findViewById(R.id.meme_image_view)
        statusV=findViewById(R.id.post_status)
        if(bitmap!=null){
            imageV.setImageBitmap(bitmap)
            bitmap=null
        }
        val postBtn:CircularProgressButton=findViewById(R.id.post_btn)
        postBtn.setOnClickListener {
            postBtn.startAnimation()
            statusV.visibility= View.VISIBLE
            statusV.text="Preparing to upload"
            val bitmap=(imageV.drawable as BitmapDrawable).bitmap

            val byteArray=bitmap.toByteArray(Bitmap.CompressFormat.JPEG,90)

            MediaManager.get().upload(byteArray).callback(object : UploadCallback {
                override fun onStart(s: String) {
                      postBtn.setProgress(0)
                    statusV.text="Uploading Image Started"
                        }

                override fun onProgress(s: String, l: Long, l1: Long) {
                    postBtn.setProgress((l*100/byteArray.size).toInt())
                    statusV.text="Uploading $l out of $l1"
                }

                override fun onSuccess(s: String, map: Map<*,*>) {
                    val public_id = map["public_id"].toString()
                    val width= map["width"].toString().toFloat()
                    val height = map["height"].toString().toFloat()
                    val ratio = width / height
                    postBtn.resetProgress()
                    statusV.text="Image Uploaded! Posting Meme"
                    MemeItMemes.getInstance().postMeme(prepareRequest(public_id, ratio), object : OnCompleteListener<Meme> {
                        override fun onSuccess(memeResponse: Meme) {
                            postBtn.revertAnimation()
                            toast("Meme Posted to MemeIt!")
                            statusV.text="Meme Posted"
                        }

                        override fun onFailure(error: OnCompleteListener.Error) {
                            postBtn.revertAnimation()
                            toast("Meme Posting failed: "+error.message)
                            statusV.text="Meme Posting Failed"
                        }
                    })
                }

                override fun onError(s: String, errorInfo: ErrorInfo) {
                    postBtn.revertAnimation()
                    toast("Meme Image Uploading failed: "+errorInfo.description)
                    statusV.text="Image Uploading Failed"
                }

                override fun onReschedule(s: String, errorInfo: ErrorInfo) {
                    postBtn.revertAnimation()
                    toast("Meme Image Uploading rescheduled: "+errorInfo.description)
                    statusV.text="Image Uploading Rescheduled"
                }
            }).dispatch()
        }
    }

    private fun prepareRequest(uri: String, ratio: Float): Meme {
        val txt = ""
        val tag = ""
        var texts: List<String>? = null
        var tags: List<String>? = null
        if (!TextUtils.isEmpty(txt)) {
            texts = Arrays.asList(*txt.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
        }
        if (!TextUtils.isEmpty(tag)) {
            tags = Arrays.asList(*tag.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
        }
        return Meme.createMeme(uri, ratio.toDouble(), Meme.MemeType.IMAGE, texts, tags)

    }
}
