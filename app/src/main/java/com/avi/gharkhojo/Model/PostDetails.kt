package com.avi.gharkhojo.Model

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.avi.gharkhojo.OwnerActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object PostDetails {
    var ownerName:String? = null
    var propertyType:String? = null
    var preferredTenants:String? = null
    var email:String? = null
    var tenantServed:String? = null
    var phoneNumber:String? = null
    var builtUpArea:String? = null
    var floorPosition:String? = null
    var lockInPeriod:Int? = null
    var furnished:String? = null
    var noOfBedRoom:Int? = null
    var noOfBathroom:Int? = null
    var noOfBalcony:Int? = null
    var isAvailableDiningSpace:Boolean? = null
    var hasLift:Boolean? = null
    var hasSecurityGuard:Boolean? = null
    var hasParking:Boolean? = null
    var hasGenerator:Boolean? = null
    var hasGasService:Boolean? = null
    var parkingCharge:Double? = null
    var isParkingChargeIncluded:Boolean? = null
    var rent:Double? = null
    var deposit:Double? = null
    var pincode:Int? = null
    var landMark:String? = null
    var houseNumber:Int? = null
    var area:String? = null
    var colony:String? = null
    var city:String? = null
    var state:String? = null
    var longitude:Double? = null
    var latitude:Double? = null
    var description:String? = null
    var coverImage:String? = null
    var maintenanceCharge:Double? = null

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

     fun saveData():Post = Post().also {
            it.ownerName = this@PostDetails.ownerName?.trim()
            it.propertyType = this@PostDetails.propertyType?.trim()
            it.preferredTenants = this@PostDetails.preferredTenants?.trim()
            it.email = this@PostDetails.email?.trim()
            it.tenantServed = this@PostDetails.tenantServed?.trim()
            it.phoneNumber = this@PostDetails.phoneNumber?.trim()
            it.builtUpArea = this@PostDetails.builtUpArea?.trim()
            it.floorPosition = this@PostDetails.floorPosition?.trim()
            it.lockInPeriod = this@PostDetails.lockInPeriod
            it.furnished = this@PostDetails.furnished?.trim()
            it.noOfBedRoom = this@PostDetails.noOfBedRoom
            it.noOfBathroom = this@PostDetails.noOfBathroom
            it.noOfBalcony = this@PostDetails.noOfBalcony
            it.isAvailableDiningSpace = this@PostDetails.isAvailableDiningSpace
            it.hasLift = this@PostDetails.hasLift
            it.hasSecurityGuard = this@PostDetails.hasSecurityGuard
            it.hasGenerator = this@PostDetails.hasGenerator
            it.hasGasService = this@PostDetails.hasGasService
            it.hasParking = this@PostDetails.hasParking
            it.parkingCharge = this@PostDetails.parkingCharge
        it.isParkingChargeIncluded = this@PostDetails.isParkingChargeIncluded
            it.rent = this@PostDetails.rent
            it.deposit = this@PostDetails.deposit
            it.pincode = this@PostDetails.pincode
            it.landMark = this@PostDetails.landMark?.trim()
            it.houseNumber = this@PostDetails.houseNumber
            it.area = this@PostDetails.area?.trim()
            it.colony = this@PostDetails.colony?.trim()
            it.city = this@PostDetails.city?.trim()
            it.state = this@PostDetails.state?.trim()
            it.longitude = this@PostDetails.longitude
            it.latitude = this@PostDetails.latitude
            it.description = this@PostDetails.description?.trim()
            it.imageList = this@PostDetails.imageList
            it.coverImage = this@PostDetails.coverImage.toString().trim()
            it.maintenanceCharge = this@PostDetails.maintenanceCharge






    }

    fun isAllFieldsFilled():Boolean = !ownerName?.trim().isNullOrEmpty()
                && !propertyType?.trim().isNullOrEmpty()
                && !preferredTenants?.trim().isNullOrEmpty()
                && !email?.trim().isNullOrEmpty()
                && !tenantServed?.trim().isNullOrEmpty()
                && !phoneNumber?.trim().isNullOrEmpty()
                && !builtUpArea?.trim().isNullOrEmpty()
                && !floorPosition?.trim().isNullOrEmpty()
                && lockInPeriod != null
                && !furnished?.trim().isNullOrEmpty()
                && noOfBedRoom != null
                && noOfBathroom != null
                && noOfBalcony != null
                && isAvailableDiningSpace != null
                && hasLift!= null
                && hasSecurityGuard != null
                && hasParking != null
            && (isParkingChargeIncluded!=null && isParkingChargeIncluded == true || parkingCharge != null)
                && rent != null
                && deposit != null
                && pincode != null
                && !landMark?.trim().isNullOrEmpty()
                && houseNumber != null
                && !area?.trim().isNullOrEmpty()
                && !colony?.trim().isNullOrEmpty()
                && !city?.trim().isNullOrEmpty()
            && imageList.values.any { it.isNotEmpty() }
            && coverImage != null && coverImage!!.isNotEmpty()
            && maintenanceCharge != null
                


    fun clearAll(){
         ownerName = null
         propertyType = null
         preferredTenants = null
         email = null
         tenantServed = null
         phoneNumber = null
         builtUpArea = null
         floorPosition = null
         lockInPeriod = null
         furnished = null
         noOfBedRoom = null
         noOfBathroom = null
         noOfBalcony = null
         isAvailableDiningSpace = null
         hasLift = null
         hasSecurityGuard = null
         hasParking = null
        hasGenerator = null
        hasGasService = null
        parkingCharge = null
        isParkingChargeIncluded = null
         rent = null
         deposit = null
         pincode = null
         landMark = null
         houseNumber = null
         area = null
         colony = null
         city = null
         state = null
         longitude = null
         latitude = null
         description = null
        imageList.forEach {
            it.value.clear()
        }
    }
}