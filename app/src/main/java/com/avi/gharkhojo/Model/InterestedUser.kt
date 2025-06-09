package com.avi.gharkhojo.Model

data class InterestedUser(
    val id: String,
    val uid: String,
    val name: String,
    val image: String?,
    val interestedDate: String,
    val post: Post?
)
