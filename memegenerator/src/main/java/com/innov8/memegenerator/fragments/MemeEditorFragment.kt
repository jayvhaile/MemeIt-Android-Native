package com.innov8.memegenerator.fragments

import android.support.v4.app.Fragment
import com.innov8.memegenerator.customViews.MyToolbarmenu

abstract class MemeEditorFragment:Fragment() {
    abstract val menus:List<MyToolbarmenu>

}