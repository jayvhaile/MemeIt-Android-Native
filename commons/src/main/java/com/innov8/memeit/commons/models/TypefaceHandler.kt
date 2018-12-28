package com.innov8.memeit.commons.models

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable

data class TypefaceHandler(val name: String, private val fileName: String = "") : Parcelable {
    private fun loadTypeFace(context: Context) {
        typeface = if (name == "Default")
            Typeface.DEFAULT
        else
            Typeface.createFromAsset(context.assets, fileName)
    }

    @Transient
    private var typeface: Typeface? = null

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!)


    fun getTypeFace(context: Context? = null): Typeface {
        if (typeface == null)
            loadTypeFace(context!!)
        return typeface!!
    }

    override fun toString() = name
    override fun equals(other: Any?): Boolean = (other as? TypefaceHandler)?.name == name
    override fun hashCode(): Int = name.hashCode()
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(fileName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TypefaceHandler> {
        override fun createFromParcel(parcel: Parcel): TypefaceHandler {
            return TypefaceHandler(parcel)
        }

        override fun newArray(size: Int): Array<TypefaceHandler?> {
            return arrayOfNulls(size)
        }


        val DEFAULT by lazy { TypefaceHandler("Default") }
        private fun loadAllFiles(): List<TypefaceHandler> {
            val prefix = "fonts/"
            val ttf = ".ttf"
            return listOf(
                    TypefaceHandler("Arial", "${prefix}arial$ttf"),
                    TypefaceHandler("Avenir", "${prefix}avenir$ttf"),
                    TypefaceHandler("Helvetica", "${prefix}helvetica$ttf"),
                    TypefaceHandler("Impact", "${prefix}impact$ttf"),
                    TypefaceHandler("Lyric", "${prefix}lyric_font$ttf"),
                    TypefaceHandler("Pacifico", "${prefix}Pacifico$ttf"),
                    TypefaceHandler("Ubuntu", "${prefix}ubuntu$ttf")
            )
        }

        val typefaceFiles by lazy { loadAllFiles() }

        fun byName(name: String, context: Context? = null): TypefaceHandler {
            val t = typefaceFiles.find { font -> font.name == name }
            return t?.apply {
                context?.let { loadTypeFace(it) }
            } ?: DEFAULT.apply {
                context?.let { loadTypeFace(it) }
            }
        }
    }
}

