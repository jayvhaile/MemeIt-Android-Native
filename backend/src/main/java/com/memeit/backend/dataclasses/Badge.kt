package com.memeit.backend.dataclasses

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.innov8.memeit.commons.getDrawableIdByName
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.io.InputStreamReader

data class Badge(val id: String,
                 val label: String = "label",
                 @SerializedName("desc") val description: String,
                 val level: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(label)
        parcel.writeString(description)
        parcel.writeInt(level)
    }

    override fun describeContents(): Int {
        return 0
    }



    fun getDrawableId(context: Context)=context.getDrawableIdByName(id)


    companion object CREATOR : Parcelable.Creator<Badge> {
        override fun createFromParcel(parcel: Parcel): Badge {
            return Badge(parcel)
        }

        override fun newArray(size: Int): Array<Badge?> {
            return arrayOfNulls(size)
        }

        fun loadBadges(context: Context, onFinished: (List<Badge>) -> Unit) {
            launch(UI) {
                onFinished(withContext(CommonPool) {
                    val gson = Gson()
                    val fr = context.assets.open("badges.json")
                    val bis = InputStreamReader(fr, "UTF-8")
                    val jsonReader = JsonReader(bis)

                    val l: List<Badge> = gson.fromJson(jsonReader, object : TypeToken<List<Badge>>() {}.type)
                    l
                })
            }
        }


    }
}
