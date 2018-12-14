package com.innov8.memegenerator.interfaces

import com.innov8.memegenerator.utils.CloseableFragment

interface EditorStateChangedListener {
    fun onEditorOpened(tag:String,cf: CloseableFragment)
    fun onEditorClosed()
}