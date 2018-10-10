package com.innov8.memegenerator.MemeEngine

interface MemeViewInterface {


    fun onItemSelected(memeItemView: MemeItemView)
    fun onItemMoved(memeItemView: MemeItemView)
    fun onItemResized(memeItemView: MemeItemView)

}