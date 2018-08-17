package com.innov8.memegenerator

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.innov8.memegenerator.fragments.MemeTextEditorFragment
import com.innov8.memegenerator.memeEngine.MemeEditorView
import com.innov8.memegenerator.memeEngine.MemeTextView
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.*
import java.io.File
import java.io.FileWriter

class MemeTemplateMaker : AppCompatActivity() {
    val meme_template_count = 97
    var meme_template_index = 1
    var gson = Gson()
    var createmode = true
    var memeTemplates = mutableListOf<MemeTemplate>()
    var json2 = "[{\"imageURL\":\"meme_01\",\"textProperties\":[{\"heightP\":0.19633508,\"textStyleProperty\":{\"allCap\":false,\"bold\":false,\"italic\":false,\"myTypeFace\":{\"fileName\":\"\",\"name\":\"Default\"},\"strokeColor\":-16777216,\"strokeWidth\":0.0,\"stroked\":true,\"textColor\":-1,\"textSize\":20.0},\"widthP\":0.35625,\"xP\":0.3201322,\"yP\":0.13875112},{\"heightP\":0.19633508,\"textStyleProperty\":{\"allCap\":false,\"bold\":false,\"italic\":false,\"myTypeFace\":{\"fileName\":\"\",\"name\":\"Default\"},\"strokeColor\":-16777216,\"strokeWidth\":0.0,\"stroked\":true,\"textColor\":-1,\"textSize\":20.0},\"widthP\":0.35625,\"xP\":0.3217254,\"yP\":0.47377342},{\"heightP\":0.19633508,\"textStyleProperty\":{\"allCap\":false,\"bold\":false,\"italic\":false,\"myTypeFace\":{\"fileName\":\"\",\"name\":\"Default\"},\"strokeColor\":-16777216,\"strokeWidth\":0.0,\"stroked\":true,\"textColor\":-1,\"textSize\":20.0},\"widthP\":0.35,\"xP\":0.32499185,\"yP\":0.8036649}]}]"
    lateinit var memeEditorView: MemeEditorView
    lateinit var memeTextEditorFragment: MemeTextEditorFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_template_maker)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 55)
            return
        }
        memeEditorView = findViewById(R.id.memeEditorView)
        memeTextEditorFragment = MemeTextEditorFragment()
        memeTextEditorFragment.textEditListener = memeEditorView.textEditListener
        supportFragmentManager.replace(R.id.holder, memeTextEditorFragment)
        findViewById<Button>(R.id.addtext).setOnClickListener {
            val memeTextView = MemeTextView(this, 400, 100)
            memeTextView.text = "Text"
            memeEditorView.addMemeItemView(memeTextView)
        }
        findViewById<Button>(R.id.removetext).setOnClickListener {
            memeEditorView.removeSelectedItem()
        }
        findViewById<Button>(R.id.next).setOnClickListener {
            if (createmode)
                loadNext()
            else
                loadTemplate()
            //Pix.start(this, 56,5)

        }
        findViewById<Button>(R.id.savebtn).setOnClickListener {
            val json = gson.toJson(memeTemplates)
            if(!isExternalStorageWritable())
                log("fucccccccccccccccccck")
            else {
                log("yihaaa")
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs()
                var f = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "meme_template.json")
                f.createNewFile()

                var x = FileWriter(f)
                x.write(json)
                x.flush()
                x.close()
            }

        }
        if (createmode) {
            var bitmap = loadBitmap(getDrawableIdByName(String.format("meme_%02d", meme_template_index)), .3f)
            memeEditorView.image = bitmap
            meme_template_index++
        } else {
            memeTemplates = gson.fromJson(json2, object : TypeToken<List<MemeTemplate>>() {}.type)
        }

    }

    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }


    fun loadNext() {
        if (meme_template_index <= meme_template_count) {
            var bitmap = loadBitmap(getDrawableIdByName(String.format("meme_%02d", meme_template_index)), .3f)
            val tp = memeEditorView.generateAllTextProperty()
            memeTemplates.add(
                    MemeTemplate(
                            "sample",
                            String.format("meme_%02d", meme_template_index - 1),
                            MemeTemplate.LOCAL_DATA_SOURCE,
                            tp
                    )
            )
            memeEditorView.clearMemeItems()
            memeEditorView.image = bitmap
            meme_template_index++


        } else {
            this.toast("Finished")
        }
    }

    var index = 0
    fun loadTemplate() {
        if (index < memeTemplates.size)
            memeEditorView.loadMemeTemplate(memeTemplates[index++])
    }
}
