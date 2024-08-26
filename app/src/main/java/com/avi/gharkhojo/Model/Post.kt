package com.avi.gharkhojo.Model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    var ownerName: String? = null,
    var propertyType: String? = null,
    var preferredTenants: String? = null,
    var email: String? = null,
    var tenantServed: String? = null,
    var phoneNumber: String? = null,
    var builtUpArea: String? = null,
    var floorPosition: String? = null,
    var lockInPeriod: Int? = null,
    var furnished: String? = null,
    var noOfBedRoom: Int? = null,
    var noOfBathroom: Int? = null,
    var noOfBalcony: Int? = null,
    var isAvailableDiningSpace: Boolean? = null,
    var hasLift: Boolean? = null,
    var hasSecurityGuard: Boolean? = null,
    var hasParking: Boolean? = null,
    var hasGenerator: Boolean? = null,
    var hasGasService: Boolean? = null,
    var parkingCharge: String? = null,
    var rent: String? = null,
    var deposit: String? = null,
    var pincode: Int? = null,
    var landMark: String? = null,
    var houseNumber: Int? = null,
    var area: String? = null,
    var colony: String? = null,
    var city: String? = null,
    var state: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null,
    var description: String? = null,
    var isParkingChargeIncluded: Boolean? = null,
    var postTime: String? = null,
    var coverImage: String? = null,
    var maintenanceCharge: String? = null,
    var road_lane: String? = null,
    var userId: String? = null,
    var noOfKitchen: Int? = null,
    var imageList: Map<String, ArrayList<String>> = hashMapOf(
        "BedRoom" to ArrayList(),
        "Kitchen" to ArrayList(),
        "WashRoom" to ArrayList(),
        "Toilet" to ArrayList(),
        "Balcony" to ArrayList(),
        "Hall" to ArrayList(),
        "Parking" to ArrayList(),
        "Extra" to ArrayList()
    )
) : Parcelable {

    constructor(parcel: Parcel) : this(
        ownerName = parcel.readString(),
        propertyType = parcel.readString(),
        preferredTenants = parcel.readString(),
        email = parcel.readString(),
        tenantServed = parcel.readString(),
        phoneNumber = parcel.readString(),
        builtUpArea = parcel.readString(),
        floorPosition = parcel.readString(),
        lockInPeriod = parcel.readValue(Int::class.java.classLoader) as? Int,
        furnished = parcel.readString(),
        noOfBedRoom = parcel.readValue(Int::class.java.classLoader) as? Int,
        noOfBathroom = parcel.readValue(Int::class.java.classLoader) as? Int,
        noOfBalcony = parcel.readValue(Int::class.java.classLoader) as? Int,
        isAvailableDiningSpace = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        hasLift = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        hasSecurityGuard = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        hasParking = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        hasGenerator = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        hasGasService = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parkingCharge = parcel.readString(),
        rent = parcel.readString(),
        deposit = parcel.readString(),
        pincode = parcel.readValue(Int::class.java.classLoader) as? Int,
        landMark = parcel.readString(),
        houseNumber = parcel.readValue(Int::class.java.classLoader) as? Int,
        area = parcel.readString(),
        colony = parcel.readString(),
        city = parcel.readString(),
        state = parcel.readString(),
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double,
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double,
        description = parcel.readString(),
        isParkingChargeIncluded = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        postTime = parcel.readString(),
        coverImage = parcel.readString(),
        maintenanceCharge = parcel.readString(),
        road_lane = parcel.readString(),
        userId = parcel.readString(),
        noOfKitchen = parcel.readValue(Int::class.java.classLoader) as? Int,
        imageList = readImageListFromParcel(parcel)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(ownerName)
        dest.writeString(propertyType)
        dest.writeString(preferredTenants)
        dest.writeString(email)
        dest.writeString(tenantServed)
        dest.writeString(phoneNumber)
        dest.writeString(builtUpArea)
        dest.writeString(floorPosition)
        dest.writeValue(lockInPeriod)
        dest.writeString(furnished)
        dest.writeValue(noOfBedRoom)
        dest.writeValue(noOfBathroom)
        dest.writeValue(noOfBalcony)
        dest.writeValue(isAvailableDiningSpace)
        dest.writeValue(hasLift)
        dest.writeValue(hasSecurityGuard)
        dest.writeValue(hasParking)
        dest.writeValue(hasGenerator)
        dest.writeValue(hasGasService)
        dest.writeString(parkingCharge)
        dest.writeString(rent)
        dest.writeString(deposit)
        dest.writeValue(pincode)
        dest.writeString(landMark)
        dest.writeValue(houseNumber)
        dest.writeString(area)
        dest.writeString(colony)
        dest.writeString(city)
        dest.writeString(state)
        dest.writeValue(longitude)
        dest.writeValue(latitude)
        dest.writeString(description)
        dest.writeValue(isParkingChargeIncluded)
        dest.writeValue(postTime)
        dest.writeString(coverImage)
        dest.writeString(maintenanceCharge)
        dest.writeString(road_lane)
        dest.writeString(userId)
        dest.writeValue(noOfKitchen)
        writeImageListToParcel(dest, imageList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post = Post(parcel)
        override fun newArray(size: Int): Array<Post?> = arrayOfNulls(size)

        private fun readImageListFromParcel(parcel: Parcel): Map<String, ArrayList<String>> {
            val map = hashMapOf<String, ArrayList<String>>()
            val size = parcel.readInt()
            for (i in 0 until size) {
                val key = parcel.readString() ?: continue
                val list = parcel.createStringArrayList() ?: arrayListOf()
                map[key] = list
            }
            return map
        }

        private fun writeImageListToParcel(dest: Parcel, map: Map<String, ArrayList<String>>) {
            dest.writeInt(map.size)
            for ((key, value) in map) {
                dest.writeString(key)
                dest.writeStringList(value)
            }
        }
    }
}
