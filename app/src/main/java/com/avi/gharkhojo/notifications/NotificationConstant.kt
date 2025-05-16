package com.avi.gharkhojo.notifications

enum class NotificationConstant(val value: String) {

    CHANNEL_ID("gharkhojo_channel"),
    NOTIFICATION_NAME("Message Notification"),
    MESSAGE_NOTIFICATION("message"),
    POST_URL( "https://fcm.googleapis.com/v1/projects/gharkhojo-61e80/messages:send")


}