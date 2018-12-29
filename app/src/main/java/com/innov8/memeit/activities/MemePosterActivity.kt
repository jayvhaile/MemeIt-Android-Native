package com.innov8.memeit.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory as externalStorage
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.innov8.memeit.R
import com.innov8.memeit.workers.MemeUploadWorker
import com.memeit.backend.models.Meme
import java.io.File
import java.util.*
import com.innov8.memeit.utils.addFileToMediaStore
import com.innov8.memeit.workers.MemeImageUploadWorker
import com.innov8.memeit.commons.toast
import kotlinx.android.synthetic.main.meme_poster_3.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel


class MemePosterActivity : AppCompatActivity() {
    private var texts: Array<String>? = null
    private lateinit var memeType: Meme.MemeType
    private var gif: String? = null
    private var image: String? = null
    private var templateID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_poster_3)
        setSupportActionBar(toolbar)


        btn_add_tag.setOnClickListener {
            startActivityForResult(Intent(this, SearchTagActivity::class.java), SearchTagActivity.REQUEST_CODE)
        }
        btn_mention_user.setOnClickListener {
            startActivityForResult(Intent(this, SearchUserActivity::class.java), SearchUserActivity.REQUEST_CODE)
        }

        save_to_gallery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !hasWritePermission) {
                requestWritePermission()
            }

        }
        handleIntent()
    }

    private fun handleIntent() {
        gif = intent?.getStringExtra("gif")
        image = intent?.getStringExtra("image")
        texts = intent?.getStringArrayExtra("texts")
        templateID = intent?.getStringExtra("tid")
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

    }

    private val hasWritePermission: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private val permRequest = 256
    private fun requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permRequest)
        }
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


    private fun upload() {
        when (memeType) {
            Meme.MemeType.IMAGE -> {
                if (save_to_gallery.isChecked && hasWritePermission) {
                    copyFile(image!!, "${externalStorage(DIRECTORY_PICTURES)}${File.separator}MemeIt")
                }
                handleImageUpload()
            }
            Meme.MemeType.GIF -> {
                if (save_to_gallery.isChecked && hasWritePermission) {
                    copyFile(gif!!, "${externalStorage(DIRECTORY_PICTURES)}${File.separator}MemeIt", "image/gif")
                }
                handleGifUpload()
            }
        }
        MaterialDialog.Builder(this).title("Your meme is getting uploaded")
                .content("You will be notified when its done.")
                .positiveText("Browse Memes")
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

    private fun copyFile(src: String, dest: String, mimeType: String = "image/jpeg") {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val saved = withContext(Dispatchers.Default) {
                var channelIn: FileChannel? = null
                var channelOut: FileChannel? = null
                var status: Boolean
                try {
                    val destDir = File(dest).apply {
                        mkdirs()
                    }
                    val destFile = File(destDir, "${UUID.randomUUID()}.jpg")
                    channelIn = FileInputStream(src).channel
                    channelOut = FileOutputStream(destFile).channel
                    channelIn.transferTo(0, channelIn.size(), channelOut)

                    addFileToMediaStore(destFile, mimeType)
                    status = true
                } catch (e: Exception) {
                    status = false
                } finally {
                    channelIn?.close()
                    channelOut?.close()
                }
                status
            }

            toast(if (saved) "Saved To Gallery" else "Failed to save to gallery")
        }
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
                .putString(MemeUploadWorker.TEMPLATE_ID, templateID)
                .putStringArray(MemeUploadWorker.TEXTS, texts ?: arrayOf())
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
                if (resultCode == SearchUserActivity.RESULT_CODE_SELECTED) {
                    description.append("@")
                    description.append(data?.getStringExtra(SearchUserActivity.PARAM_SELECTED_USERNAME))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permRequest -> {
                if (!hasWritePermission) {
                    toast("Sorry,We couldn't save the image to gallery without the permission")
                    save_to_gallery.isChecked = false
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
