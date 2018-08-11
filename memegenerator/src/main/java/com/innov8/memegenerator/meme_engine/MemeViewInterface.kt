package com.innov8.memegenerator.meme_engine

interface MemeViewInterface {


    fun onItemSelected(memeItemView: MemeItemView)
    fun onItemMoved(memeItemView: MemeItemView)
    fun onItemResized(memeItemView: MemeItemView)

}