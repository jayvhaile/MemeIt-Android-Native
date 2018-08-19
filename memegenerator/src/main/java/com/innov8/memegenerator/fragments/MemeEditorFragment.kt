package com.innov8.memegenerator.fragments

import androidx.fragment.app.Fragment
import com.innov8.memegenerator.customViews.MyToolbarmenu

abstract class MemeEditorFragment: androidx.fragment.app.Fragment() {
    abstract val menus:List<MyToolbarmenu>

}