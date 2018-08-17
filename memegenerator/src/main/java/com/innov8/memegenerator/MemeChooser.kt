package com.innov8.memegenerator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.innov8.memegenerator.adapters.MemeTemplatesListAdapter
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.MyAsyncTask
import com.innov8.memegenerator.utils.goToWithString
import com.innov8.memegenerator.utils.initWithGrid
import java.io.InputStreamReader

class MemeChooser : AppCompatActivity() {
    lateinit var memeTemplateListView:RecyclerView
    lateinit var memeTemplatesListAdapter: MemeTemplatesListAdapter
    lateinit var gson:Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_chooser)
        memeTemplatesListAdapter= MemeTemplatesListAdapter(this)
        memeTemplateListView=findViewById(R.id.meme_template_list)
        memeTemplateListView.initWithGrid(2)
        memeTemplateListView.adapter=memeTemplatesListAdapter
        gson=Gson()
        MyAsyncTask<List<MemeTemplate>>()
                .start {
                    val fr=assets.open("template.json")
                    val bis=InputStreamReader(fr,"UTF-8")
                    val jsonReader= JsonReader(bis)

                    gson.fromJson(jsonReader, object:TypeToken<List<MemeTemplate>>() {}.type)

                }.onFinished {
                    memeTemplatesListAdapter.addAll(it)
                }


        memeTemplatesListAdapter.OnItemClicked={
            val json=gson.toJson(it)
            goToWithString(MemeEditorActivity::class.java,json)
        }
    }
}
