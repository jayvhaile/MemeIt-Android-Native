package com.memeit.backend.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.memeit.backend.utils.generateFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class MemeTemplate(
        val _id: String? = null,
        val pid: String? = null,
        val label: String,
        val category: String,
        val memeType: String,
        val usageCount: Long? = null,
        val createdDate: Long? = null,
        val tags: List<String>,
        val examples: List<Array<String>> = listOf(),
        val memeTemplateProperty: SavedMemeTemplateProperty
) : HomeElement {
    override val itemType: Int = HomeElement.MEME_TEMPLATE_SUGGESTION_TYPE

    override fun equals(other: Any?): Boolean {
        return (other as? MemeTemplate)?._id?.let {
            it == _id
        } ?: false
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }

    fun saveToFile(file: File) {
        val jw = JsonWriter(FileWriter(file))
        buildGson()
                .toJson(
                        this,
                        MemeTemplate::class.java,
                        jw
                )
        jw.close()
    }

    fun saveToString(): String =
            buildGson().toJson(this, MemeTemplate::class.java)


    companion object {
        fun readFromString(json: String): MemeTemplate {
            return buildGson()
                    .fromJson<MemeTemplate>(
                            json,
                            MemeTemplate::class.java
                    )
        }

        fun readFromFile(file: File): MemeTemplate? {
            val jw = JsonReader(FileReader(file))
            return try {
                buildGson()
                        .fromJson<MemeTemplate>(
                                jw,
                                MemeTemplate::class.java
                        )
            } catch (e: Exception) {
                null
            } finally {
                jw.close()
            }
        }

        fun getTemplatesDir(context: Context): File {
            return File(context.filesDir, "templates/").apply { this.mkdirs() }
        }

        fun getDraftsDir(context: Context): File {
            return File(context.filesDir, "templates/drafts/").apply { this.mkdirs() }
        }

        fun getDraftsJsonDir(context: Context): File {
            return File(context.filesDir, "templates/drafts/json/").apply { this.mkdirs() }
        }

        fun getSavedDir(context: Context): File {
            return File(context.filesDir, "templates/saved/").apply { this.mkdirs() }
        }

        fun getSavedJsonDir(context: Context): File {
            return File(context.filesDir, "templates/saved/json/").apply { this.mkdirs() }
        }

        fun getTempUploadDir(context: Context): File {
            return File(context.filesDir, "templates/temp/upload/").apply { this.mkdirs() }
        }

        fun getTempUploadJsonDir(context: Context): File {
            return File(context.filesDir, "templates/temp/upload/json/").apply { this.mkdirs() }
        }
    }
}


sealed class MemeTemplateProperty(
        val layoutProperty: LayoutProperty,
        val memeItemsProperty: List<MemeItemProperty>
)

sealed class LoadedMemeTemplateProperty(
        layoutProperty: LayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        val images: List<Bitmap> = listOf(),
        val previewImageBitmap: Bitmap? = null
) : MemeTemplateProperty(layoutProperty, memeItemsProperty)

enum class SavedState {
    LOCAL,
    REMOTE
}

sealed class SavedMemeTemplateProperty(
        layoutProperty: LayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        val images: MutableList<String> = mutableListOf(),
        var previewImageUrl: String
) : MemeTemplateProperty(layoutProperty, memeItemsProperty) {

    fun getState() = images.find { it.startsWith("http") }?.let {
        SavedState.REMOTE
    } ?: SavedState.LOCAL

    fun saveToFile(file: File) {
        val jw = JsonWriter(FileWriter(file))
        buildGson().toJson(
                this,
                SavedMemeTemplateProperty::class.java,
                jw
        )
        jw.close()
    }

    fun saveToString(): String =
            buildGson().toJson(this, SavedMemeTemplateProperty::class.java)

    abstract fun getType(): Meme.MemeType

    companion object {
        fun getRuntimeTypeAdapterFactory() =
                generateFactory(SavedMemeTemplateProperty::class.java,
                        listOf(
                                SavedImageMemeTemplateProperty::class.java,
                                SavedGifMemeTemplateProperty::class.java
                        ))

        fun readFromString(json: String): SavedMemeTemplateProperty {
            return buildGson()
                    .fromJson<SavedMemeTemplateProperty>(
                            json,
                            SavedMemeTemplateProperty::class.java
                    )
        }

        fun readFromFile(file: File): SavedMemeTemplateProperty? {
            val jw = JsonReader(FileReader(file))
            return try {
                buildGson()
                        .fromJson<SavedMemeTemplateProperty>(
                                jw,
                                SavedMemeTemplateProperty::class.java
                        )
            } catch (e: Exception) {
                null
            } finally {
                jw.close()
            }
        }
    }
}


class LoadedImageMemeTemplateProperty(
        layoutProperty: LayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        images: List<Bitmap> = listOf(),
        previewImageBitmap: Bitmap? = null
) : LoadedMemeTemplateProperty(layoutProperty, memeItemsProperty, images, previewImageBitmap)

class LoadedGifMemeTemplateProperty(
        layoutProperty: SingleImageLayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        image: Bitmap,
        val originalPath: String,
        previewImageBitmap: Bitmap? = null
) : LoadedMemeTemplateProperty(layoutProperty, memeItemsProperty, listOf(image), previewImageBitmap)


class SavedImageMemeTemplateProperty(
        layoutProperty: LayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        images: MutableList<String> = mutableListOf(),
        previewImageUrl: String
) : SavedMemeTemplateProperty(layoutProperty, memeItemsProperty, images, previewImageUrl) {
    override fun getType(): Meme.MemeType = Meme.MemeType.IMAGE
}

class SavedGifMemeTemplateProperty(
        layoutProperty: SingleImageLayoutProperty,
        memeItemsProperty: List<MemeItemProperty>,
        path: String,
        previewImageUrl: String
) : SavedMemeTemplateProperty(layoutProperty, memeItemsProperty, mutableListOf(path), previewImageUrl) {
    override fun getType(): Meme.MemeType = Meme.MemeType.GIF
}

sealed class LayoutProperty(
        val leftMargin: Int,
        val rightMargin: Int,
        val topMargin: Int,
        val bottomMargin: Int,
        val bgColor: Int
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(leftMargin)
        parcel.writeInt(rightMargin)
        parcel.writeInt(topMargin)
        parcel.writeInt(bottomMargin)
        parcel.writeInt(bgColor)
    }

    override fun describeContents(): Int = 0


    companion object {
        fun getRuntimeTypeAdapterFactory() =
                generateFactory(LayoutProperty::class.java,
                        listOf(
                                SingleImageLayoutProperty::class.java,
                                LinearImageLayoutProperty::class.java,
                                GridImageLayoutProperty::class.java
                        ))


    }

}

class SingleImageLayoutProperty(
        leftMargin: Int,
        rightMargin: Int,
        topMargin: Int,
        bottomMargin: Int,
        bgColor: Int
) : LayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin, bgColor) {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    companion object CREATOR : Parcelable.Creator<SingleImageLayoutProperty> {
        override fun createFromParcel(parcel: Parcel): SingleImageLayoutProperty {
            return SingleImageLayoutProperty(parcel)
        }

        override fun newArray(size: Int): Array<SingleImageLayoutProperty?> {
            return arrayOfNulls(size)
        }
    }
}

class LinearImageLayoutProperty(
        leftMargin: Int,
        rightMargin: Int,
        topMargin: Int,
        bottomMargin: Int,
        bgColor: Int,
        val orientation: Int,
        val spacing: Int
) : LayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin, bgColor) {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(orientation)
        parcel.writeInt(spacing)
    }

    companion object CREATOR : Parcelable.Creator<LinearImageLayoutProperty> {
        override fun createFromParcel(parcel: Parcel): LinearImageLayoutProperty {
            return LinearImageLayoutProperty(parcel)
        }

        override fun newArray(size: Int): Array<LinearImageLayoutProperty?> {
            return arrayOfNulls(size)
        }
    }
}

class GridImageLayoutProperty(
        leftMargin: Int,
        rightMargin: Int,
        topMargin: Int,
        bottomMargin: Int,
        bgColor: Int,
        val orientation: Int,
        val span: Int,
        val hSpacing: Int,
        val vSpacing: Int
) : LayoutProperty(leftMargin, rightMargin, topMargin, bottomMargin, bgColor) {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(orientation)
        parcel.writeInt(span)
        parcel.writeInt(hSpacing)
        parcel.writeInt(vSpacing)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GridImageLayoutProperty> {
        override fun createFromParcel(parcel: Parcel): GridImageLayoutProperty {
            return GridImageLayoutProperty(parcel)
        }

        override fun newArray(size: Int): Array<GridImageLayoutProperty?> {
            return arrayOfNulls(size)
        }
    }
}


sealed class MemeItemProperty(
        val x: Float,     //the x position of the item
        val y: Float,     //the y position of the item
        val w: Float,     //the width of the item
        val h: Float,     //the height of the item
        val r: Float    //rotation in degrees of the item
) {
    companion object {
        fun getRuntimeTypeAdapterFactory() =
                generateFactory(MemeItemProperty::class.java,
                        listOf(
                                MemeStickerItemProperty::class.java,
                                MemeTextItemProperty::class.java
                        ))
    }
}

class MemeStickerItemProperty(
        val sticker: Sticker,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        r: Float
) : MemeItemProperty(x, y, w, h, r)

class MemeTextItemProperty(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        r: Float,
        val text: String,
        val tsp: MemeTextStyleProperty? = null
) : MemeItemProperty(x, y, w, h, r)

data class MemeTextStyleProperty(val textSize: Float = 0f,
                                 val textColor: Int = Color.BLACK,
                                 val font: String = "default",
                                 val bold: Boolean = false,
                                 val italic: Boolean = false,
                                 val allCap: Boolean = false,
                                 val stroked: Boolean = false,
                                 val strokeColor: Int = Color.BLACK,
                                 val strokeWidth: Float = 0f,
                                 val bgColor: Int = Color.TRANSPARENT,
                                 val align: Layout.Alignment = Layout.Alignment.ALIGN_CENTER) {

    var alignment: Layout.Alignment = align ?: Layout.Alignment.ALIGN_CENTER
}


fun buildGson(): Gson {
    return GsonBuilder().registerTypeAdapterFactory(SavedMemeTemplateProperty.getRuntimeTypeAdapterFactory())
            .registerTypeAdapterFactory(LayoutProperty.getRuntimeTypeAdapterFactory())
            .registerTypeAdapterFactory(MemeItemProperty.getRuntimeTypeAdapterFactory())
            .registerTypeAdapterFactory(Sticker.getRuntimeTypeAdapterFactory())
            .create()
}