package com.innov8.memegenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.innov8.memegenerator.adapters.MemeTemplatesListAdapter
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.goToWithString
import com.innov8.memegenerator.utils.initWithGrid

class MemeChooser : AppCompatActivity() {
    lateinit var memeTemplateListView: androidx.recyclerview.widget.RecyclerView
    lateinit var memeTemplatesListAdapter: MemeTemplatesListAdapter
    lateinit var gson: Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_chooser)
        memeTemplatesListAdapter = MemeTemplatesListAdapter(this)
        memeTemplateListView = findViewById(R.id.meme_template_list)
        memeTemplateListView.initWithGrid(2)
        memeTemplateListView.adapter = memeTemplatesListAdapter
        gson = Gson()
        MemeTemplate.loadLocalTemplates(this) {
            memeTemplatesListAdapter.addAll(it)
        }

        memeTemplatesListAdapter.OnItemClicked = {
            val json = gson.toJson(it)
            goToWithString(MemeEditorActivity::class.java, json)
        }
    }
}
