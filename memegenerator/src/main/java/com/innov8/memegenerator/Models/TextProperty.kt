package com.innov8.memegenerator.Models

import android.os.Parcel
import android.os.Parcelable

data class TextProperty(val xP: Float, val yP: Float, val widthP: Float, val heightP: Float, val textStyleProperty: TextStyleProperty) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readParcelable(TextStyleProperty::class.java.classLoader)!!)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(xP)
        parcel.writeFloat(yP)
        parcel.writeFloat(widthP)
        parcel.writeFloat(heightP)
        parcel.writeParcelable(textStyleProperty, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TextProperty> {
        override fun createFromParcel(parcel: Parcel): TextProperty {
            return TextProperty(parcel)
        }

        override fun newArray(size: Int): Array<TextProperty?> {
            return arrayOfNulls(size)
        }
    }
}