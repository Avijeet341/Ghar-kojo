package com.avi.gharkhojo.Model

class ChatUserListModel(){
    var username:String? = null
    var userimage:String? = null
    var userId:String? = null
     var lastMessageTimestamp:Long? = null
     var lastMessage:String? = null
     var userEmail:String? = null
    var followers: ArrayList<String>? = null
     constructor(username:String?,userImage: String?,userId:String?,userEmail:String?):this()
    {
        this.username=username
        this.userimage=userImage
        this.userId = userId
        this.userEmail = userEmail
    }
    constructor(username:String?,userImage: String?,userId:String?,userEmail:String?,followers:ArrayList<String>?):this(){
        this.username = username
        this.userimage = userImage
        this.userId = userId
        this.userEmail = userEmail
        this.followers = followers
    }
}
