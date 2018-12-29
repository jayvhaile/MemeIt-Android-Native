package com.innov8.memeit.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.innov8.memeit.fragments.TagSearchFragment
import com.innov8.memeit.R
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_tag_search.*

class SearchTagActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_SELECTED = 101
        const val RESULT_CODE_CANCELLED = 102
        const val PARAM_SELECTED_TAG = "selected tag"

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        setResult(SearchUserActivity.RESULT_CODE_CANCELLED, null)
        super.onBackPressed()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_search)

        val tagSearchFragment = TagSearchFragment().apply {
            onItemClicked= {
                finishWithResult(it.tag)
            }
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.holder, tagSearchFragment)
                .commit()

        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search_view.addOnTextChanged {
            tagSearchFragment.setFilter(it)
            search_clear.visibility = if (it.isBlank()) View.INVISIBLE else View.VISIBLE

        }
        search_view.setOnEditorActionListener { _, _, _ ->
            val text = search_view.text.toString()
            if (text.isBlank()) {
                false
            } else {
                finishWithResult(text)
                true
            }
        }
        search_clear.setOnClickListener {
            search_view.text.clear()
        }
    }

    private fun finishWithResult(tag: String) {
        setResult(RESULT_CODE_SELECTED, Intent().apply { putExtra(PARAM_SELECTED_TAG, tag) })
        finish()
    }
}