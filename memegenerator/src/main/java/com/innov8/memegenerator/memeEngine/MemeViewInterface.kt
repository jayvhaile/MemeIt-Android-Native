package com.innov8.memegenerator.memeEngine

interface MemeViewInterface {


    fun onItemSelected(memeItemView: MemeItemView)
    fun onItemMoved(memeItemView: MemeItemView)
    fun onItemResized(memeItemView: MemeItemView)

}