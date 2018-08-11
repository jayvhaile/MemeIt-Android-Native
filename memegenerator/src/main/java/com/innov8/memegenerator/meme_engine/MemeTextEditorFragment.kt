package com.innov8.memegenerator.meme_engine


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner

import com.innov8.memegenerator.R
import com.innov8.memegenerator.custom_views.ToggleImageButton

class MemeTextEditorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_customize_text, container, false)
    }

    lateinit var textSizeV:SeekBar
    lateinit var textColorV:View
    lateinit var textfontV:Spinner
    lateinit var textStyleBoldV:ToggleImageButton
    lateinit var textStyleItalicV:ToggleImageButton
    lateinit var textStyleAllCapV:ToggleImageButton
    lateinit var textStroke:CheckBox
    private fun init(){


    }


}
