package com.innov8.memeit.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.innov8.memeit.commons.SuperActivity
import com.innov8.memeit.fragments.UserSearchFragment
import com.innov8.memeit.R
import com.innov8.memeit.commons.addOnTextChanged
import kotlinx.android.synthetic.main.activity_search_user.*

class SearchUserActivity : SuperActivity() {
    companion object {
        const val REQUEST_CODE = 200
        const val RESULT_CODE_SELECTED = 201
        const val RESULT_CODE_CANCELLED = 202
        const val PARAM_SELECTED_USERNAME = "selected username"

    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(RESULT_CODE_CANCELLED, null)
        super.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userSearchFragment = UserSearchFragment().apply {
            onItemClicked= { finishWithResult(it.username!!) }
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.holder, userSearchFragment)
                .commit()

        search_view.addOnTextChanged {
            userSearchFragment.setFilter(it)
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

    private fun finishWithResult(username: String) {
        setResult(RESULT_CODE_SELECTED, Intent().apply { putExtra(PARAM_SELECTED_USERNAME, username) })
        finish()
    }
}
