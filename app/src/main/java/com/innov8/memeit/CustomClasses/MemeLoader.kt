package com.innov8.memeit.CustomClasses

import android.content.Context
import android.os.Parcelable
import com.facebook.common.util.UriUtil
import com.innov8.memegenerator.models.MemeTemplate
import com.innov8.memegenerator.utils.AsyncLoader
import com.innov8.memegenerator.utils.getDrawableIdByName
import com.innov8.memeit.trim
import com.memeit.backend.MemeItMemes
import com.memeit.backend.MemeItUsers
import com.memeit.backend.dataclasses.*
import com.memeit.backend.utilis.Listener
import com.memeit.backend.utilis.OnCompleteListener
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.util.*

interface MemeLoader<T>:Parcelable {
    companion object {
        const val HOME_LOADER: Byte = 1
        const val TRENDING_LOADER: Byte = 2
        const val FAVOURITE_LOADER: Byte = 3
        const val USER_POST_MEME_LOADER: Byte = 4
        const val EMPTY_LOADER: Byte = 5
        fun create(type: Byte, context: Context, userID: String? = null): MemeLoader<out HomeElement> =
                when (type) {
                    HOME_LOADER -> HomeLoader(context)
                    TRENDING_LOADER -> TrendingLoader()
                    FAVOURITE_LOADER -> FavoritesLoader()
                    USER_POST_MEME_LOADER -> MyMemesLoader(userID = userID)
                    else -> EmptyLoader()
                }
    }

    var listener: OnCompleteListener<List<T>>?

    fun load(skip: Int, limit: Int)
    fun reset() {}

}

class HomeLoader(val context: Context, override var listener: OnCompleteListener<List<HomeElement>>? = null) : MemeLoader<HomeElement> {
    private var userSuggestions: List<User>? = null
    private var tagsSuggestions: List<Tag>? = null
    private var memeTemplates: List<MemeTemplate>? = null

    private var usersLoaded = false
    private var usersFailed = true
    private var tagsLoaded = false
    private var tagsFailed = true
    private var templateLoaded = false
    private var templateFailed = true
    var memeLoaded = false
    var memeFailed = false

    var tempMemes: List<Meme>? = null
    var tempError: OnCompleteListener.Error? = null


    val myMemeListener = Listener<List<Meme>>({
        memeLoaded = true
        tempMemes = it
        bake()
    }, {
        memeFailed = true
        tempError = it
        bake()
    })
    fun loada(skip: Int, limit: Int) {
        launch(UI){

        }
    }
    override fun load(skip: Int, limit: Int) {
        if (!usersLoaded && usersFailed) {
            usersFailed = false
            MemeItUsers.getInstance().getUserSuggestions(Listener<List<User>>({
                userSuggestions = it
                usersLoaded = true
                bake()
            },{
                usersFailed = true
                bake()
            }))

        }
        if (!tagsLoaded && tagsFailed) {
            tagsFailed = false
            MemeItMemes.getInstance().getSuggestedTags(0,300,Listener<List<Tag>>({
                tagsSuggestions = it
                tagsLoaded = true
                bake()
            },{
                tagsFailed = true
                bake()
            }))

        }
        if (!templateLoaded && templateFailed) {
            templateFailed = false
            MemeTemplate.loadLocalTemplates(context) {
                memeTemplates = it
                templateLoaded = true
                bake()
            }
        }
        memeFailed = false
        MemeItMemes.getInstance().getHomeMemes(skip, limit, myMemeListener)

    }

    var index = 0
    val offset = 2
    var type = 0

    private fun bake() {
        val usersLoadedOrFailed = usersLoaded || usersFailed
        val tagsLoadedOrFailed = tagsLoaded || tagsFailed
        val templateLoadedOrFailed = templateLoaded || templateFailed
        if (memeFailed) {
            listener?.onFailure(tempError)
        } else if (memeLoaded && usersLoadedOrFailed && tagsLoadedOrFailed&&templateLoadedOrFailed) {
            AsyncLoader<List<HomeElement>> {
                val homeElements = mutableListOf<HomeElement>()
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
                homeElements
            }.load {
                listener?.onSuccess(it)
            }


        }
    }

    private fun loadShit(triedUser: Boolean = false, triedTagg:Boolean=false,triedTemp: Boolean = false, triedAd: Boolean = false): HomeElement? {
        return if (triedAd && triedTemp && triedUser) null
        else {
            val t = type % 4
            type++
            when (t) {
                0 -> {
                    bakeSuggestion() ?: loadShit(true,triedTagg, triedTemp, triedAd)
                }
                1->bakeTags()?:loadShit(triedUser,true,triedTemp,triedAd)
                2 -> bakeTemplate() ?: loadShit(triedUser, triedTagg,true, triedAd)
                else -> bakeAD() ?: loadShit(triedUser,triedTagg, triedTemp, true)
            }
        }
    }

    private var usersIndex = 0
    private var tagsIndex = 0
    private var templateIndex = 0
    private fun bakeSuggestion(): UserSuggestion? {
        val users = userSuggestions ?: return null
        val rem=(users.size-usersIndex) trim 10
        return if(rem>2){
            val u=UserSuggestion(users.subList(usersIndex,usersIndex+rem))
            usersIndex+=rem
            u
        } else null
    }
    private fun bakeTags():TagSuggestion?{
        val tags=tagsSuggestions?:return null
        val rem=(tags.size-tagsIndex) trim 10
        return if(rem>2){
            val t=TagSuggestion(tags.subList(tagsIndex,tagsIndex+rem))
            tagsIndex+=rem
            t
        } else null
    }

    private fun bakeTemplate(): MemeTemplateSuggestion? {
        val templates = memeTemplates ?: return null
        val rem=(templates.size-templateIndex) trim 10
        return if(rem>2){
            val m= MemeTemplateSuggestion(templates.subList(templateIndex,templateIndex+rem).map {
                if(it.dataSource==MemeTemplate.LOCAL_DATA_SOURCE){
                    UriUtil.getUriForResourceId(context.getDrawableIdByName(it.imageURL)).toString()
                }else{
                    ""      //todo handle
                }
            })
            templateIndex+=rem
            m
        }else null
    }

    private fun bakeAD(): AdElement? {
        return AdElement()
    }

    override fun reset() {
        userSuggestions = null
        tagsSuggestions = null
        memeTemplates = null
        tempMemes = null
        tempError = null
        usersLoaded = false
        usersFailed = true
        tagsLoaded = false
        tagsFailed = true
        templateLoaded = false
        templateFailed = true
        memeLoaded = false
        memeFailed = false
        index = 0
        type = 0
        templateIndex = 0
        usersIndex = 0
        tagsIndex=0
    }


}

class SearchLoader(override var listener: OnCompleteListener<List<Meme>>? = null) : MemeLoader<Meme> {

    fun search(search: String?, tags: Array<String>?, skip: Int, limit: Int) {
        MemeItMemes.getInstance().getFileterdMemes(search, tags, skip, limit, listener)
    }

    override fun load(skip: Int, limit: Int) {
}

class EmptyLoader(override var listener: OnCompleteListener<List<Meme>>? = null) : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int) {
        listener?.onSuccess(ArrayList())
    }
}
search(null, null, skip, limit)
}

}

class TrendingLoader(override var listener: OnCompleteListener<List<Meme>>? = null) : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int) {
        MemeItMemes.getInstance().getTrendingMemes(skip, limit, listener)
    }
}

class FavoritesLoader(override var listener: OnCompleteListener<List<Meme>>? = null) : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int) {
        MemeItMemes.getInstance().getFavouriteMemes(skip, limit, listener)
    }
}

class MyMemesLoader(override var listener: OnCompleteListener<List<Meme>>? = null, val userID: String?) : MemeLoader<Meme> {
    override fun load(skip: Int, limit: Int) {
        if (userID == null)
            MemeItMemes.getInstance().getMyMemes(skip, limit, listener)
        else
            MemeItMemes.getInstance().getMemesOf(userID, skip, limit, listener)
    }