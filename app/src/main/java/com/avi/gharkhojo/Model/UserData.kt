package com.avi.gharkhojo.Model

data object UserData{


    var username: String? = null
    var profilePictureUrl: String? = null
    var email: String? = null
    var phn_no: String? = null
    var HouseNo:String? = null
    var Road_Lane:String? = null
    var City:String? = null
    var State:String? = null
    var Pincode:String? = null
    var Area:String? = null
    var colony:String? = null
    var LandMark:String? = null
    var uid: String? = null

    fun clear() {
        username = null
        email = null
        HouseNo = null
        Road_Lane = null
        City = null
        State = null
        Pincode = null
        Area = null
        colony = null
        LandMark = null
        phn_no = null
        profilePictureUrl = null
        uid = null
    }
}
