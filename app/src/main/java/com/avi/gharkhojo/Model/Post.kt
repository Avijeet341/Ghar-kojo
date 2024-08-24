package com.avi.gharkhojo.Model

import android.net.Uri

data class Post(
    var ownerName:String?,
    var propertyType:String?,
    var preferredTenants:String?,
    var email:String? ,
    var tenantServed:String?,
    var phoneNumber:String? ,
    var builtUpArea:String? ,
    var floorPosition:String? ,
    var lockInPeriod:Int? ,
    var furnished:String? ,
    var noOfBedRoom:Int? ,
    var noOfBathroom:Int? ,
    var noOfBalcony:Int? ,
    var isAvailableDiningSpace:Boolean? ,
    var hasLift:Boolean? ,
    var hasSecurityGuard:Boolean? ,
    var hasParking:Boolean? ,
    var hasGenerator:Boolean? ,
    var hasGasService:Boolean? ,
    var parkingCharge:Double? ,
    var rent:Double? ,
    var deposit:Double? ,
    var pincode:Int? ,
    var landMark:String? ,
    var houseNumber:Int? ,
    var area:String? ,
    var colony:String? ,
    var city:String? ,
    var state:String? ,
    var longitude:Double? ,
    var latitude:Double? ,
    var description:String?,
    var isParkingChargeIncluded: Boolean?,
    var postTime:Long?,
    var coverImage:String?,
    var maintenanceCharge:Double?,
    var road_lane: String?,
    var imageList:Map<String,ArrayList<String>> = hashMapOf(
        "BedRoom" to ArrayList(),
        "Kitchen" to ArrayList(),
        "WashRoom" to ArrayList(),
        "Toilet" to ArrayList(),
        "Balcony" to ArrayList(),
        "Hall" to ArrayList(),
        "Parking" to ArrayList(),
        "Extra" to ArrayList()
    )

 ){




    constructor():this(null,null,
        null,null,
        null,null,
        null,null,
        null,null,
        null,null,
        null,null,
        null,null,
        null,
        null,null,
        null,null,
        null,null,
        null,null,null,
        null,null,null,null,
        null,null,null,
        null,null,null,null
        )
}