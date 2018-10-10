package com.innov8.memegenerator

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.innov8.memegenerator.MemeEngine.MemeEditorView
import com.innov8.memegenerator.MemeEngine.MemeTextView
import com.innov8.memegenerator.Models.MemeTemplate
import com.innov8.memegenerator.Models.MemeTemplate.Companion.LOCAL_DATA_SOURCE
import com.innov8.memegenerator.utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStreamReader

class MemeTemplateMaker : AppCompatActivity() {
    val meme_template_count = 97
    var meme_template_index = 1
    var gson = Gson()
    var memeTemplates = mutableListOf<MemeTemplate>()
    lateinit var memeEditorView: MemeEditorView
//    lateinit var memeTextEditorFragment: MemeTextEditorFragment
    val view: View? = null
    lateinit var file: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_template_maker)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 55)
            return
        }
        memeEditorView = findViewById(R.id.memeEditorView)
//        memeTextEditorFragment = MemeTextEditorFragment()
//        memeTextEditorFragment.textEditListener = memeEditorView.textEditListener
//        supportFragmentManager.replace(R.id.holder, memeTextEditorFragment)

        findViewById<Button>(R.id.addtext).setOnClickListener {
            val memeTextView = MemeTextView(this, 400, 100)
            memeTextView.text = "Text"
            memeEditorView.addMemeItemView(memeTextView)
        }
        findViewById<Button>(R.id.removetext).setOnClickListener {
            memeEditorView.removeSelectedItem()
        }
        findViewById<Button>(R.id.next).setOnClickListener {
            if(!load()){
                loadNext()
            }
        }
        findViewById<Button>(R.id.savebtn).setOnClickListener {
            if (file.exists()) file.delete()
            file.createNewFile()
            val json = gson.toJson(memeTemplates)
            var x = FileWriter(file)
            x.write(json)
            x.flush()
            x.close()
        }

        if (isExternalStorageWritable()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs()
            file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "meme_template.json")
            if (file.exists()) {
                MyAsyncTask<List<MemeTemplate>>()
                        .start {
                            val x = FileInputStream(file)
                            val bis = InputStreamReader(x, "UTF-8")
                            val jsonReader = JsonReader(bis)
                            gson.fromJson(jsonReader, object : TypeToken<List<MemeTemplate>>() {}.type)

                        }.onFinished {
                            memeTemplates = it.toMutableList()
                            meme_template_index
                            if(!load()){
                                var bitmap = loadBitmap(getDrawableIdByName(String.format("meme_%02d", meme_template_index)), .3f)
                                memeEditorView.loadBitmab(bitmap)
                                meme_template_index++
                            }
                        }
            }
        }

    }
    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
    fun loadNext() {
        if (meme_template_index <= meme_template_count) {
            val imgname = String.format("meme_%02d", meme_template_index - 1)
            var bitmap = loadBitmap(getDrawableIdByName(String.format("meme_%02d", meme_template_index)), .3f)
            val tp = memeEditorView.generateAllTextProperty()
            val newItem = MemeTemplate("sample", imgname, LOCAL_DATA_SOURCE, tp)
            val item = memeTemplates.find { it.imageURL == imgname }
            if (item != null) {
                val index = memeTemplates.indexOf(item)
                memeTemplates[index] = newItem
            } else
                memeTemplates.add(newItem)
            memeEditorView.clearMemeItems()
            memeEditorView.loadBitmab(bitmap)
            meme_template_index++

        } else {
            this.toast("Finished")
        }
    }
    fun load():Boolean {
        if (memeTemplates.size > meme_template_index - 1) {
            memeEditorView.loadMemeTemplate(memeTemplates[meme_template_index - 1])
            meme_template_index++
            return true
        }
        return false
    }
}
