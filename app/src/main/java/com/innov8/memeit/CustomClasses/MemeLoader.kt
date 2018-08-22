package com.innov8.memeit.CustomClasses

import com.memeit.backend.MemeItMemes
import com.memeit.backend.dataclasses.Meme
import com.memeit.backend.utilis.OnCompleteListener
import java.util.*

interface MemeLoader {
    companion object {
        const val HOME_LOADER:Byte=1
        const val TRENDING_LOADER:Byte=2
        const val FAVOURITE_LOADER:Byte=3
        const val MYMEME_LOADER:Byte=4
        const val EMPTY_LOADER:Byte=5
        fun create(type:Byte)=
                when(type){
                    HOME_LOADER->HomeLoader()
                    TRENDING_LOADER->TrendingLoader()
                    FAVOURITE_LOADER->FavoritesLoader()
                    MYMEME_LOADER->MyMemesLoader()
                    else ->EmptyLoader()
                }
    }
    fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>)

}

class HomeLoader : MemeLoader {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        //todo getHomeMemesforguest if user is not signed in
        //MemeItMemes.getInstance().getHomeMemes(skip,limit,listener);
    }

}

class TrendingLoader : MemeLoader {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getTrendingMemes(skip, limit, listener)
    }
}

class FavoritesLoader : MemeLoader {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getFavouriteMemes(skip, limit, listener)
    }
}

class MyMemesLoader : MemeLoader {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getMyMemes(skip, limit, listener)
    }
}

class EmptyLoader : MemeLoader {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        listener.onSuccess(ArrayList())
    }
}