package com.avi.gharkhojo.Model

import android.os.Parcel
import android.os.Parcelable

data class GridItem(val imageResId: Int, val rent: String, val bhk: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageResId)
        parcel.writeString(rent)
        parcel.writeString(bhk)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GridItem> {
        override fun createFromParcel(parcel: Parcel): GridItem {
            return GridItem(parcel)
        }

        override fun newArray(size: Int): Array<GridItem?> {
            return arrayOfNulls(size)
        }
    }
}
