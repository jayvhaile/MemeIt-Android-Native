package com.innov8.memegenerator.interfaces

import com.innov8.memegenerator.memeEngine.MemeItemView

interface MemeViewInterface {


    fun onItemSelected(memeItemView: MemeItemView)
    fun onItemMoved(memeItemView: MemeItemView)
    fun onItemResized(memeItemView: MemeItemView)

}