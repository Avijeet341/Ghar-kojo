package com.avi.gharkhojo.Model

import android.os.Parcel
import android.os.Parcelable

data class Upload(
    val key: String,
    val imageResId: String,
    val title: String,
    val location: String,
    val priceAmount: String,
    val pricePeriod: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val area: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Upload> {
        override fun createFromParcel(parcel: Parcel): Upload {
            return Upload(parcel)
        }

        override fun newArray(size: Int): Array<Upload?> {
            return arrayOfNulls(size)
        }
    }
}
