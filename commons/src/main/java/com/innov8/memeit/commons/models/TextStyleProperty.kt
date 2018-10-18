package com.innov8.memeit.commons.models

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

data class TextStyleProperty(val textSize:Float=0f,
                             val textColor:Int= Color.BLACK,
                             val myTypeFace: MyTypeFace,
                             val bold:Boolean=false,
                             val italic:Boolean=false,
                             val allCap:Boolean=false,
                             val stroked:Boolean=false,
                             val strokeColor:Int=Color.BLACK,
                             val strokeWidth:Float=0f) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readParcelable(MyTypeFace::class.java.classLoader)!!,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(textSize)
        parcel.writeInt(textColor)
        parcel.writeParcelable(myTypeFace, flags)
        parcel.writeByte(if (bold) 1 else 0)
        parcel.writeByte(if (italic) 1 else 0)
        parcel.writeByte(if (allCap) 1 else 0)
        parcel.writeByte(if (stroked) 1 else 0)
        parcel.writeInt(strokeColor)
        parcel.writeFloat(strokeWidth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TextStyleProperty> {
        override fun createFromParcel(parcel: Parcel): TextStyleProperty {
            return TextStyleProperty(parcel)
        }

        override fun newArray(size: Int): Array<TextStyleProperty?> {
            return arrayOfNulls(size)
        }
    }
}