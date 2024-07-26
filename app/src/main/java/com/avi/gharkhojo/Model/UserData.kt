package com.avi.gharkhojo.Model

object UserData {
    var username: String? = null
    var profilePictureUrl: String? = null
    var email: String? = null
    var phn_no: String? = null
    var address: String? = null
    var uid: String? = null

    fun clear() {
        username = null
        profilePictureUrl = null
        email = null
        phn_no = null
        address = null
        uid = null
    }
}
