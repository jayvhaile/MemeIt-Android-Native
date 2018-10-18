package com.innov8.memeit.commons.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.io.InputStreamReader

data class MemeTemplate(val label: String, val imageURL: String, val dataSource: Byte = LOCAL_DATA_SOURCE, val textProperties: List<TextProperty>) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readByte(),
            parcel.createTypedArrayList(TextProperty)!!)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(imageURL)
        parcel.writeByte(dataSource)
        parcel.writeTypedList(textProperties)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemeTemplate> {
        override fun createFromParcel(parcel: Parcel): MemeTemplate {
            return MemeTemplate(parcel)
        }

        override fun newArray(size: Int): Array<MemeTemplate?> {
            return arrayOfNulls(size)
        }

        val LOCAL_DATA_SOURCE: Byte = 0
        val SERVER_DATA_SOURCE: Byte = 1

        fun loadLocalTemplates(context: Context, onFinished: (List<MemeTemplate>) -> Unit) {
            launch(UI) {
                onFinished(withContext(CommonPool) {
                    val gson = Gson()
                    val fr = context.assets.open("template.json")
                    val bis = InputStreamReader(fr, "UTF-8")
                    val jsonReader = JsonReader(bis)

                    val l: List<MemeTemplate> = gson.fromJson(jsonReader, object : TypeToken<List<MemeTemplate>>() {}.type)
                    l
                })
            }
        }

        fun loadLocalTemplates(context: Context): List<MemeTemplate> {
            val gson = Gson()
            val fr = context.assets.open("template.json")
            val bis = InputStreamReader(fr, "UTF-8")
            val jsonReader = JsonReader(bis)
            return gson.fromJson(jsonReader, object : TypeToken<List<MemeTemplate>>() {}.type)
        }
    }

}