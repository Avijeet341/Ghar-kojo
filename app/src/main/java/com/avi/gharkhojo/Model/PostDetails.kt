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


    fun setToPost():Post = Post().also {
            it.ownerName = ownerName
            it.propertyType = propertyType
            it.preferredTenants = preferredTenants
            it.email = email
            it.tenantServed = tenantServed
            it.phoneNumber = phoneNumber
            it.builtUpArea = builtUpArea
            it.floorPosition = floorPosition
            it.lockInPeriod = lockInPeriod
            it.furnished = furnished
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
            it.landMark = landMark
            it.houseNumber = houseNumber
            it.area = area
            it.colony = colony
            it.city = city
            it.state = state
            it.longitude = longitude
            it.latitude = latitude
            it.description = description

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
                && !description?.trim().isNullOrEmpty()
                


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
    }
}