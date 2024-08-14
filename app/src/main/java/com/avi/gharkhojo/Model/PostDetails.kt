package com.avi.gharkhojo.Model

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
    var parkingCharge:Double? = null
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

    var imageList:Map<String,ArrayList<String>?> = hashMapOf(
        "BedRoom" to ArrayList(),
        "Kitchen" to ArrayList(),
        "Bathroom" to ArrayList(),
        "Toilet" to ArrayList(),
        "Balcony" to ArrayList(),
        "Hall" to ArrayList(),
        "Parking" to ArrayList(),
        "Extra" to ArrayList()
    )


    fun setToPost():Post = Post().also {
            it.ownerName = ownerName?.trim()
            it.propertyType = propertyType?.trim()
            it.preferredTenants = preferredTenants?.trim()
            it.email = email?.trim()
            it.tenantServed = tenantServed?.trim()
            it.phoneNumber = phoneNumber?.trim()
            it.builtUpArea = builtUpArea?.trim()
            it.floorPosition = floorPosition?.trim()
            it.lockInPeriod = lockInPeriod
            it.furnished = furnished?.trim()
            it.noOfBedRoom = noOfBedRoom
            it.noOfBathroom = noOfBathroom
            it.noOfBalcony = noOfBalcony
            it.isAvailableDiningSpace = isAvailableDiningSpace
            it.hasLift = hasLift
            it.hasSecurityGuard = hasSecurityGuard
            it.hasParking = hasParking
            it.parkingCharge = parkingCharge
            it.rent = rent
            it.deposit = deposit
            it.pincode = pincode
            it.landMark = landMark?.trim()
            it.houseNumber = houseNumber
            it.area = area?.trim()
            it.colony = colony?.trim()
            it.city = city?.trim()
            it.state = state?.trim()
            it.longitude = longitude
            it.latitude = latitude
            it.description = description?.trim()
            it.imageList = imageList

        clearAll()
            return it

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
                && parkingCharge != null
                && rent != null
                && deposit != null
                && pincode != null
                && !landMark?.trim().isNullOrEmpty()
                && houseNumber != null
                && !area?.trim().isNullOrEmpty()
                && !colony?.trim().isNullOrEmpty()
                && !city?.trim().isNullOrEmpty()
                && !state?.trim().isNullOrEmpty()
                && longitude != null
                && latitude != null
                


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
        parkingCharge = null
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
            it.value?.clear()
        }
    }
}