package com.innov8.memeit.loaders

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.innov8.memeit.MemeItApp
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.LayoutProperty
import com.memeit.backend.models.MemeItemProperty
import com.memeit.backend.models.MemeTemplate
import com.memeit.backend.models.SavedMemeTemplateProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.FileReader

enum class Sorter {
    POPULAR, RECENT;
}

sealed class TemplateLoader : Loader<MemeTemplate>, Parcelable {
    override var skip: Int = 0
    var type: String? = null
    var category: String? = null
    var search: String? = null
    var mine = false
    var sortBy: Sorter = Sorter.POPULAR

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(category)
        parcel.writeString(search)
        parcel.writeByte(if (mine) 1 else 0)
        parcel.writeString(sortBy.toString())
    }

    override fun describeContents(): Int {
        return 0
    }
}

class ServerTemplateLoader() : TemplateLoader() {
    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        category = parcel.readString()
        search = parcel.readString()
        mine = parcel.readByte() == 1.toByte()
        sortBy = Sorter.valueOf(parcel.readString()!!)
    }

    override fun load(limit: Int, onSuccess: (List<MemeTemplate>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getTemplates(skip, limit, type, category, search, mine, sortBy.toString()).call(onSuccess, onError)
    }


    companion object CREATOR : Parcelable.Creator<ServerTemplateLoader> {
        override fun createFromParcel(parcel: Parcel): ServerTemplateLoader {
            return ServerTemplateLoader(parcel)
        }

        override fun newArray(size: Int): Array<ServerTemplateLoader?> {
            return arrayOfNulls(size)
        }
    }
}

class UnapprovedTemplateLoader() : TemplateLoader() {
    constructor(parcel: Parcel) : this()

    override fun load(limit: Int, onSuccess: (List<MemeTemplate>) -> Unit, onError: (String) -> Unit) {
        MemeItMemes.getUnapprovedTemplates(skip, limit).call(onSuccess, onError)
    }


    companion object CREATOR : Parcelable.Creator<UnapprovedTemplateLoader> {
        override fun createFromParcel(parcel: Parcel): UnapprovedTemplateLoader {
            return UnapprovedTemplateLoader(parcel)
        }

        override fun newArray(size: Int): Array<UnapprovedTemplateLoader?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }
}

class SavedTemplatesLoader() : TemplateLoader() {
    override var skip: Int = 0

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
        category = parcel.readString()
        search = parcel.readString()
        mine = parcel.readByte() == 1.toByte()
        sortBy = Sorter.valueOf(parcel.readString()!!)
    }

    private val context: Context
        get() = MemeItApp.instance

    override fun load(limit: Int, onSuccess: (List<MemeTemplate>) -> Unit, onError: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val gson = GsonBuilder().registerTypeAdapterFactory(SavedMemeTemplateProperty.getRuntimeTypeAdapterFactory())
                    .registerTypeAdapterFactory(LayoutProperty.getRuntimeTypeAdapterFactory())
                    .registerTypeAdapterFactory(MemeItemProperty.getRuntimeTypeAdapterFactory())
                    .create()
            val result = withContext(Dispatchers.Default) {
                MemeTemplate.getSavedJsonDir(context)//todo skip limit
                        .takeIf { it.exists() }
                        ?.listFiles()
//                        ?.slice(skip..skip + limit)
                        ?.map {
                            val jr = JsonReader(FileReader(it))
                            val sp = gson.fromJson<MemeTemplate>(jr, MemeTemplate::class.java)
                            jr.close()
                            sp
                        }?.filter {
                            (if (mine) it.pid!! == MemeItClient.myUser!!.id else true) &&
                                    (type?.let { t -> it.memeType == t } ?: true) &&
                                    (category?.let { c -> it.category == c } ?: true) &&
                                    (search?.let { s -> it.label.contains(s) || it.tags.any { t -> t.contains(s) } }
                                            ?: true)

                        }?.sortedByDescending {
                            when (sortBy) {
                                Sorter.POPULAR -> it.usageCount
                                Sorter.RECENT -> it.createdDate
                            }
                        } ?: listOf()
            }
            onSuccess(result)
        }
    }

    companion object CREATOR : Parcelable.Creator<SavedTemplatesLoader> {
        override fun createFromParcel(parcel: Parcel): SavedTemplatesLoader {
            return SavedTemplatesLoader(parcel)
        }

        override fun newArray(size: Int): Array<SavedTemplatesLoader?> {
            return arrayOfNulls(size)
        }
    }
}
