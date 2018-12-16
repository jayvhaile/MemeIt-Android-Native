package com.innov8.memeit.Activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.Workers.MemeUploadWorker
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.Meme
import java.io.File
import java.util.*
import com.innov8.memeit.R
import com.innov8.memeit.Workers.MemeImageUploadWorker
import kotlinx.android.synthetic.main.meme_poster_3.*


class MemePosterActivity : AppCompatActivity() {
    var texts: Array<String>? = null
    private lateinit var memeType: Meme.MemeType
    private var gif: String? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_poster_3)
        setSupportActionBar(toolbar)
        val muser = MemeItClient.myUser!!
//        poster_pp.setText(muser.name?.prefix() ?: "")


        btn_add_tag.setOnClickListener {
            startActivityForResult(Intent(this, SearchTagActivity::class.java), SearchTagActivity.REQUEST_CODE)
        }
        btn_mention_user.setOnClickListener {
            startActivityForResult(Intent(this, SearchUserActivity::class.java), SearchUserActivity.REQUEST_CODE)
        }


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
        image = intent?.getStringExtra("image")
        when {
            gif != null -> {
                memeType = Meme.MemeType.GIF
                val file = File(gif)
                meme_image_view.controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromFile(file))
                        .setAutoPlayAnimations(true)
                        .setControllerListener(MyControllerListener())
                        .build()
            }
            image != null -> {
                memeType = Meme.MemeType.IMAGE
                meme_image_view.controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromFile(File(image)))
                        .setAutoPlayAnimations(true)
                        .setControllerListener(MyControllerListener())
                        .build()
            }
            else -> {
                finish()
                return
            }
        }
        texts = intent?.getStringArrayExtra("texts")
    }


    private fun upload() {
        val id = when (memeType) {
            Meme.MemeType.IMAGE -> handleImageUpload()
            Meme.MemeType.GIF -> handleGifUpload()
        }
        MaterialDialog.Builder(this).title("Your meme is getting uploaded")
                .content("We will let you know when the upload is completed")
                .positiveText("Browse Meme")
                .negativeText("Make Another Meme")
                .onPositive { _, _ ->
                    finish()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                }
                .onNegative { _, _ ->
                    finish()
                }
                .show()


    }

    private fun handleImageUpload(): UUID {
        return enqueueMemeUpload(File(image), rat)
    }

    private fun handleGifUpload() = enqueueMemeUpload(File(gif), rat)

    private fun enqueueMemeUpload(file: File, ratio: Float = 1f): UUID {
        val inputData = Data.Builder()
                .putFloat(MemeUploadWorker.IMAGE_RATIO, ratio)
                .putString(MemeUploadWorker.DESCRIPTION, description.text?.toString())
                .putString(MemeUploadWorker.TYPE, memeType.name)
                .putStringArray(MemeUploadWorker.TEXTS, texts ?: arrayOf())
                .putStringArray(MemeUploadWorker.TAGS, tags.toTypedArray())
                .build()
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val imageUploadWork = OneTimeWorkRequest.Builder(MemeImageUploadWorker::class.java)
                .setConstraints(constraints)
                .addTag("upload")
                .addTag("meme_upload")
                .setInputData(Data.Builder().putString(MemeImageUploadWorker.FILE_PATH, file.path).build())
                .build()
        val postWork = OneTimeWorkRequest.Builder(MemeUploadWorker::class.java)
                .setConstraints(constraints)
                .addTag("upload")
                .addTag("meme_upload")
                .setInputData(inputData)
                .build()
        WorkManager.getInstance()
                .beginWith(imageUploadWork)
                .then(postWork)
                .enqueue()
        return postWork.id
    }

    private val tags
        get() = listOf<String>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SearchTagActivity.REQUEST_CODE -> {
                if (resultCode == SearchTagActivity.RESULT_CODE_SELECTED) {
                    description.append(" #")
                    description.append(data?.getStringExtra(SearchTagActivity.PARAM_SELECTED_TAG))
                }
            }
            SearchUserActivity.REQUEST_CODE -> {
                if (resultCode == SearchUserActivity.RESULT_CODE_SELECTED){
                    description.append(" @")
                    description.append(data?.getStringExtra(SearchUserActivity.PARAM_SELECTED_USERNAME))
                }
            }
        }
    }
}

var rat = 1f

class MyControllerListener : BaseControllerListener<ImageInfo>() {
    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        super.onFinalImageSet(id, imageInfo, animatable)
        animatable?.start()
        rat = imageInfo!!.width.toFloat() / imageInfo.height

    }
}
