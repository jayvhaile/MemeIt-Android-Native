package com.innov8.memeit.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.Adapters.ELEAdapter
import com.innov8.memeit.Adapters.TagSearchAdapter
import com.innov8.memeit.Loaders.PopularTagLoader
import com.innov8.memeit.R
import com.innov8.memeit.Utils.makeLinear
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_tag_search.*

class SearchTagActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CODE_SELECTED = 101
        const val RESULT_CODE_CANCELLED = 102
        const val PARAM_SELECTED_TAG = "selected tag"

    }

    val adapter by lazy {
        TagSearchAdapter(this)
    }
    val loader by lazy {
        PopularTagLoader()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_search)

        search_tags_list.makeLinear()
        search_tags_list.adapter = adapter


        search_view.addOnTextChanged {
            adapter.filterWord = it
            load(if (it.isBlank()) null else it)
        }
        search_view.setOnEditorActionListener { _, _, _ ->
            val text = search_view.text.toString()
            if (text.isBlank()) {
                false
            } else {
                setResult(RESULT_CODE_SELECTED, Intent().apply { putExtra(PARAM_SELECTED_TAG, text) })
                finish()
                true
            }
        }
    }

    fun load(search: String? = null) {
        if (adapter.getCount() == 0) adapter.loading = true
        loader.search = search
        loader.load(30, {
            adapter.loading = false
            adapter.addAll(it)
        }) {
            adapter.loading = false
            if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_ERROR
        }
    }
}