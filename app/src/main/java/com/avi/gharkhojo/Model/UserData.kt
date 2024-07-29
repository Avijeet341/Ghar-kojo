package com.avi.gharkhojo.Model

import com.avi.gharkhojo.R

data object UserData{


    var username: String? = null
    var profilePictureUrl: String? = null
    var email: String? = null
    var phn_no: String? = null
    var address: String? = null
    var uid: String? = null

    fun clear() {
        username = null
        email = null
        address = null
        phn_no = null
        profilePictureUrl = null
        uid = null
    }
}
