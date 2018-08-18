package com.innov8.memegenerator.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innov8.memegenerator.R
import com.innov8.memegenerator.customViews.MyToolbarmenu

class MemePaintEditorFragment : MemeEditorFragment() {
    override val menus: List<MyToolbarmenu>
        get() = listOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meme_editor_paint, container, false)
    }


}
