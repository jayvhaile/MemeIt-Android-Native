package com.innov8.memegenerator

import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memegenerator.utils.toByteArray
import com.innov8.memeit.commons.log
import com.innov8.memeit.commons.prefix
import com.innov8.memeit.commons.toast
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Meme
import kotlinx.android.synthetic.main.activity_meme_poster2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.FileOutputStream
import java.util.*

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
        upload(File(gif), rat)
    }

    private fun upload(file: File, ratio: Float = 1f, id: Boolean = true) {
        progressDialog.setTitle("Uploading Image")
        progressDialog.show()
        MemeItClient.uploadFile(file, id, {
            progressDialog.setTitle("Image Uploaded, Posting Meme")
            MemeItMemes.postMeme(prepareRequest(it, ratio)).call({
                progressDialog.hide()
                toast("Meme Posted to MemeIt!")
            }, {
                progressDialog.hide()
                toast("Meme Posting failed: $it")
            })
        }) {
            progressDialog.hide()
            toast("Failed to upload Image")
            log("fcck", it)
        }
    }

    private fun upload(byteArray: ByteArray, ratio: Float = 1f, ext:String) {
        progressDialog.setTitle("Uploading Image")
        progressDialog.show()
        MemeItClient.uploadByteArray(byteArray,ext, {
            progressDialog.setTitle("Image Uploaded, Posting Meme")
            MemeItMemes.postMeme(prepareRequest(it, ratio)).call({
                progressDialog.hide()
                toast("Meme Posted to MemeIt!")
            }, {
                progressDialog.hide()
                toast("Meme Posting failed: $it")
            })
        }) {
            progressDialog.hide()
            toast("Failed to upload Image")
            log("fcck", it)
        }
    }


    private fun handleImageUpload() {
        val bitmap = (meme_image_view.drawable as BitmapDrawable).bitmap
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val byteArray = withContext(Dispatchers.Default) {
                bitmap.toByteArray(Bitmap.CompressFormat.JPEG, 90)
            }
            upload(byteArray, bitmap.width.toFloat() / bitmap.height,"jpeg")
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

var rat = 1f

class MyControllerListener : BaseControllerListener<ImageInfo>() {
    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        super.onFinalImageSet(id, imageInfo, animatable)
        animatable?.let {
            it.start()
            rat = imageInfo!!.width.toFloat() / imageInfo.height.toFloat()
        }

    }
}
