package com.avi.gharkhojo.Model

class Message
{
    var messageId:String? = null
    var message:String? = null
    var isImage:Boolean = false
    var senderId:String? = null
    var imageUrl:String? = null
    var img_id:String? = null
    var repliedMsg:String?=null
    var repliedMsgPosition:Int?=null
    var replyToId:String? = null
    var timeStamp:Long= 0
    constructor(){}
    constructor(message: String?,senderId:String?,timeStamp:Long)
    {
        this.message = message
        this.senderId = senderId
        this.timeStamp = timeStamp

    }

}