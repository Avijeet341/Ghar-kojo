package com.avi.gharkhojo.Model

 class ChatUserListModel(){
    var username:String? = null
    var userimage:String? = null
    var userId:String? = null
     constructor(username:String?,userImage: String?,userId:String?):this()
    {
        this.username=username;
        this.userimage=userImage;
        this.userId = userId
    }
}
