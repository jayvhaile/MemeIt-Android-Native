package com.innov8.memeit.CustomClasses

import android.content.Context
import com.facebook.common.util.UriUtil
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.getDrawableIdByName
import com.innov8.memegenerator.utils.log
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import com.memeit.backend.utilis.OnCompleteListener
import java.util.*

interface MemeLoader<T> {
    companion object {
        const val HOME_LOADER: Byte = 1
        const val TRENDING_LOADER: Byte = 2
        const val FAVOURITE_LOADER: Byte = 3
        const val MYMEME_LOADER: Byte = 4
        const val EMPTY_LOADER: Byte = 5
        fun create(type: Byte, context: Context) =
                when (type) {
                    HOME_LOADER -> HomeLoader(context)
                    TRENDING_LOADER -> TrendingLoader()
                    FAVOURITE_LOADER -> FavoritesLoader()
                    MYMEME_LOADER -> MyMemesLoader()
                    else -> EmptyLoader()
                }
    }

    fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<T>>)
    fun reset() {}
}

class HomeLoader(val context: Context) : MemeLoader<HomeElement> {
    var userSuggestions: Queue<User>? = null
    var memeTemplates: List<MemeTemplate>? = null

    var suggestionLoaded = false
    var suggestionFailed = true
    var templateLoaded = false
    var templateFailed = true
    var memeLoaded = false
    var memeFailed = false

    var tempmemeListener: OnCompleteListener<List<HomeElement>>? = null
    var tempMemes: List<Meme>? = null
    var tempError: OnCompleteListener.Error? = null


    val myMemeListener = object : OnCompleteListener<List<Meme>> {
        override fun onSuccess(t: List<Meme>) {
            memeLoaded = true
            tempMemes = t
            bake()
        }

        override fun onFailure(error: OnCompleteListener.Error?) {
            memeFailed = true
            tempError = error
        }

    }

    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<HomeElement>>) {
        log("load called")
        log("checking if suggestion is not loaded or failed")
        log("result", suggestionLoaded, suggestionFailed)
        if (!suggestionLoaded && suggestionFailed) {
            suggestionFailed = false
            MemeItUsers.getInstance().getUserSuggestions(object : OnCompleteListener<List<User>> {
                override fun onSuccess(t: List<User>) {
                    userSuggestions = ArrayDeque(t)
                    suggestionLoaded = true
                    log("suggestion loaded successfully")
                    bake()
                }

                override fun onFailure(error: OnCompleteListener.Error?) {
                    log("suggestion failed", error?.message ?: "")
                    suggestionFailed = true
                }

            })

        }
        if (!templateLoaded && templateFailed) {
            templateFailed = false
            MemeTemplate.loadLocalTemplates(context) {
                memeTemplates = it
                templateLoaded = true
                bake()
            }
        }
        tempmemeListener = listener
        memeFailed = false
        log("loading memes")
        MemeItMemes.getInstance().getHomeMemes(skip, limit, myMemeListener)

    }

    var index = 0
    val offset = 2
    var type = 0

    private fun bake() {
        log("bake called", "checking if something is still loading")
        val memeLoadedOrFailed = memeLoaded || memeFailed
        log("meme status", "failed:" + memeFailed, "loaded: " + memeLoaded)
        val suggestionLoadedOrFailed = suggestionLoaded || suggestionFailed
        log("suggestion status", "failed:" + suggestionFailed, "loaded: " + suggestionLoaded)
        val templateLoadedOrFailed = templateLoaded || templateFailed
        if (memeLoadedOrFailed && suggestionLoadedOrFailed && templateLoadedOrFailed) {
            if (memeFailed) {
                log("meme loading failed. sending error")
                tempmemeListener!!.onFailure(tempError)
            } else {
                log("baking results")
                val homeElements = mutableListOf<HomeElement>()
                //todo make performance consideration here
                tempMemes!!.forEach {
                    homeElements.add(it)
                    index++
                    if (index % offset == 0) {
                        val x = loadShit()
                        if (x != null) {
                            homeElements.add(x)
                        }
                    }
                }
                tempmemeListener!!.onSuccess(homeElements)
            }
        }
    }

    private fun loadShit(triedUser: Boolean = false, triedTemp: Boolean = false, triedAd: Boolean = false): HomeElement? {
        return if (triedAd && triedTemp && triedUser) null
        else {
            val t = type % 3
            type++
            when (t) {
                0 -> {
                    if (suggestionFailed) loadShit(true, triedTemp, triedAd)
                    bakeSuggestion() ?: loadShit(true, triedTemp, triedAd)
                }
                1 -> bakeTemplate() ?: loadShit(triedUser, true, triedAd)
                else -> bakeAD() ?: loadShit(triedUser, triedTemp, true)
            }
        }
    }

    var suggestionlastIndex = 0
    var templatelastIndex = 0
    private fun bakeSuggestion(): UserSuggestion? {
        val users = userSuggestions ?: return null
        return if (users.size < 2) null
        else {
            val suggestions = mutableListOf<User>()
            for (i in 0 until 4) {
                val user: User? = users.poll()
                if (user == null) break
                else
                    suggestions.add(user)
            }
            UserSuggestion(suggestions)
        }
    }

    private fun bakeTemplate(): MemeTemplateSuggestion? {
        val templates = memeTemplates ?: return null
        return if (templates.size >= templatelastIndex + 2) {
            val t = mutableListOf<String>()
            for (i in 0 until 4) {
                if (templates.size > templatelastIndex) {
                    val tt = templates[templatelastIndex++]
                    var uri: String = tt.imageURL
                    if (tt.dataSource == MemeTemplate.LOCAL_DATA_SOURCE)
                        uri = UriUtil.getUriForResourceId(context.getDrawableIdByName(tt.imageURL)).toString()
                    t.add(uri)
                } else {
                    break
                }
            }
            MemeTemplateSuggestion(t)
        } else null
    }

    private fun bakeAD(): AdElement? {
        return null
    }

    override fun reset() {

        userSuggestions = null
        memeTemplates = null
        tempmemeListener = null
        tempMemes = null
        tempError = null
        suggestionLoaded = false
        suggestionFailed = true
        templateLoaded = false
        templateFailed = true
        memeLoaded = false
        memeFailed = false
        index = 0
        type = 0
        templatelastIndex=0
    }


}

class TrendingLoader : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getTrendingMemes(skip, limit, listener)
    }
}

class FavoritesLoader : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getFavouriteMemes(skip, limit, listener)
    }
}

class MyMemesLoader : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        MemeItMemes.getInstance().getMyMemes(skip, limit, listener)
    }
}

class EmptyLoader : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int, listener: OnCompleteListener<List<Meme>>) {
        listener.onSuccess(ArrayList())
    }
}