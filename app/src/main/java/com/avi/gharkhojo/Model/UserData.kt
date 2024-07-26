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
        username = R.string.default_username.toString()
        email = R.string.default_email.toString()
        address = R.string.default_address.toString()
        phn_no = R.string.default_phone.toString()
        profilePictureUrl = R.string.profile.toString()
        uid = null
    }
}
