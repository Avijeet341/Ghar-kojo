package com.example.app

 class MessageNotification(){
     lateinit var username:String
     lateinit var message:String
     lateinit var profileImg:String
     lateinit var receiverUid:String
     lateinit var sentId:String

     constructor(
         username:String, message:String,
         profileImg:String, receiverUid:String, sentId:String):this(){
             this.username=username
         this.message=message
         this.profileImg=profileImg
         this.receiverUid=receiverUid
         this.sentId=sentId

     }
 }




