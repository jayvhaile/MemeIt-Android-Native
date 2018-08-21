package com.innov8.memeit.Adapters

import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.HomePageItemPack

class HomePageItemLoader {

    fun load(){
        MemeItMemes.getInstance().getHomeMemes(0,10,null)

        var n=50
        var noAd=Math.floor(0.05*n).toInt()
        var noUS=3
        var noMS=2

    }
    var i=8
    var n=50
    lateinit var homePageItemPack: HomePageItemPack
   /* fun bake():List<HomeElement>{
        val l= mutableListOf<HomeElement>()
        val ml=homePageItemPack.memes
        val ul=homePageItemPack.memes
        val al=homePageItemPack.memes
        for(x in 0..n step i){
            l.addAll()
        }
    }*/


}