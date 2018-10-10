package com.innov8.memegenerator.Models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.innov8.memegenerator.utils.MyAsyncTask
import java.io.InputStreamReader

data class MemeTemplate(val label: String, val imageURL: String, val dataSource: Byte = LOCAL_DATA_SOURCE, val textProperties: List<TextProperty>) {
    companion object {
        val LOCAL_DATA_SOURCE: Byte = 0
        val SERVER_DATA_SOURCE: Byte = 1

        fun loadLocalTemplates(context: Context, onFinished: (List<MemeTemplate>) -> Unit) {
            MyAsyncTask<List<MemeTemplate>>()
                    .start {
                        val gson = Gson()
                        val fr = context.assets.open("template.json")
                        val bis = InputStreamReader(fr, "UTF-8")
                        val jsonReader = JsonReader(bis)

                        gson.fromJson(jsonReader, object : TypeToken<List<MemeTemplate>>() {}.type)

                    }.onFinished(onFinished)
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